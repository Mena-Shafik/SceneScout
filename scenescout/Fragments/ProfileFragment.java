package com.example.mena.scenescout.Fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mena.scenescout.Acitivties.CreateProfileActivity;
import com.example.mena.scenescout.Acitivties.MenuActivity;
import com.example.mena.scenescout.R;
import com.example.mena.scenescout.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView name, phoneNum, email,desc;
    private ImageView profilePic;
    private String id = "empty";
    private FirebaseAuth mAuth;
    private Button updateButton;
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users"); // if not work go back to empty
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("image/");
    private FirebaseUser currentUser;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseRef.addValueEventListener(userListener);
        mAuth = FirebaseAuth.getInstance();



        //currentUser  = ((MenuActivity)getActivity()).getID();


        //currentUser = mAuth.getCurrentUser();


    }


    @Override
    public void onResume() {
        super.onResume();


        //id = mAuth.getCurrentUser().getUid();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //((MenuActivity)getActivity()).getSupportActionBar().setTitle("Profile");
        getActivity().setTitle("Profile");


        profilePic = view.findViewById(R.id.profilePic);
        name = view.findViewById(R.id.pName);
        phoneNum = view.findViewById(R.id.pPhone);
        email = view.findViewById(R.id.pEmail);
        desc = view.findViewById(R.id.pDesc);
        updateButton = view.findViewById(R.id.updateButton);

        //Bundle bundle = getArguments();
        //id = bundle.getString("id");
        id = mAuth.getCurrentUser().getUid();


        //mAuth = FirebaseAuth.getInstance();
        //final FirebaseUser currentUser = mAuth.getCurrentUser();

        //Log.i("ID:",id);
        mDatabaseRef.addValueEventListener(userListener);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), CreateProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("email", email.getText().toString());
                bundle.putString("name", name.getText().toString());
                bundle.putString("phonenum", phoneNum.getText().toString());
                bundle.putString("desc", desc.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
            });


        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_profile_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_addProfile:
                Intent intent2 = new Intent(getActivity(), CreateProfileActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent2);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void showData(DataSnapshot dataSnapshot)
    {


        for(DataSnapshot dp: dataSnapshot.getChildren())
        {
            String keyid = dp.getKey();
            if(keyid.equals(id)) {
                User user = new User();
                user.setName(dp.child(id).getValue(User.class).getName());
                user.setPhoneNum(dp.child(id).getValue(User.class).getPhoneNum());
                user.setEmail(dp.child(id).getValue(User.class).getEmail());
                user.setAbout(dp.child(id).getValue(User.class).getAbout());

                name.setText(user.getName());
                phoneNum.setText(user.getPhoneNum());
                email.setText(user.getEmail());
                desc.setText(user.getAbout());
            }
        }
    }*/

    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot dp: dataSnapshot.getChildren())
            {
                //Log.i("UID in Profile", id);
                Log.i("key", dp.getKey());
                String keyid = dp.getKey();
                if(keyid.equals(id)) {
                    User user = new User();
                    user.setName(dp.getValue(User.class).getName());
                    user.setPhoneNum(dp.getValue(User.class).getPhoneNum());
                    user.setEmail(dp.getValue(User.class).getEmail());
                    user.setAbout(dp.getValue(User.class).getAbout());
                    user.setImageUrl(dp.getValue(User.class).getImageUrl());

                    Log.i("imageurl", user.getImageUrl());
                    Log.i("test", mStorageRef.child(user.getImageUrl()).toString());

                    name.setText(user.getName());
                    phoneNum.setText(user.getPhoneNum());
                    email.setText(user.getEmail());
                    desc.setText(user.getAbout());
                    //String url = mStorageRef.child(user.getImageUrl()).getPath();
                    new DownloadImageTask(profilePic).execute(user.getImageUrl());
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("read FB realtime: ", "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
