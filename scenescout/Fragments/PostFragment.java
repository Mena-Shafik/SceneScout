package com.example.mena.scenescout.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mena.scenescout.Model.Post;
import com.example.mena.scenescout.PostReAdapter;
import com.example.mena.scenescout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;   // if not work go back to empty

    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("image/");
    private String id;
    //private ArrayList<Post> postList;
    public PostFragment() {
        // Required empty public constructor
    }

    //String imglink1 = "http://www.kinyu-z.net/data/wallpapers/190/1368568.jpg";
    //String imglink2 = "http://www.kinyu-z.net/data/wallpapers/190/1368631.jpg";
    //String imglink3 = "https://wallpapercave.com/wp/wp1951689.jpg";
    //ArrayList<String> imglist = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Post");

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        id = currentUser.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Posts/"+id);

        //postList = new ArrayList<Post>();

        //updateBtn = view.findViewById(R.id.updateButton);
        //imglist.add(imglink1);
        //imglist.add(imglink2);
        //imglist.add(imglink3);

        /*final ArrayList<Post> moviesList = new ArrayList<>();
        moviesList.add(new Post(imglist, "Cool Apartment", "Mississauga", "ON","gotta nothing to say", 2));
        moviesList.add(new Post(imglist, "Amazing House for Filming", "Mississauga", "ON","gotta nothing to say", 4));
        moviesList.add(new Post(imglist, "Could be a Scary Place to Film", "Mississauga", "ON","gotta nothing to say", 3));
        moviesList.add(new Post(imglist, "Divergent", "Mississauga", "ON","gotta nothing to say", 5));
        moviesList.add(new Post(imglist, "Fight Club", "Mississauga", "ON","gotta nothing to say", 1));
        moviesList.add(new Post(imglist, "Jaws", "Mississauga", "ON","gotta nothing to say", 4));
        moviesList.add(new Post(imglist, "Pirates of the Caribbean", "Mississauga", "ON","gotta nothing to say", 3));
        moviesList.add(new Post(imglist, "Star Wars", "Mississauga", "ON","gotta nothing to say", 2));
        moviesList.add(new Post(imglist, "The Grey", "Mississauga", "ON","gotta nothing to say", 4));*/

        mDatabaseRef.child(id);
        mDatabaseRef.addValueEventListener(postListener);







        //Listview Doesn't work for fragments
/*        mAdapter = new PostAdapter(getActivity(), moviesList);
        listView.setAdapter(mAdapter);
        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //if(i == 0)
                //{
                Intent myintent = new Intent(getActivity().getApplication(), PostDetailActivity.class);
                startActivity(myintent);
                //}
            }
        });*/

        return view;

    }


    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            ArrayList<Post> postList = new ArrayList<Post>();
            for(DataSnapshot dp: dataSnapshot.getChildren())
            {
                Post post = new Post();
                post.setTitle(dp.getValue(Post.class).getTitle());
                post.setStreet(dp.getValue(Post.class).getStreet());
                post.setCity(dp.getValue(Post.class).getCity());
                post.setProvince(dp.getValue(Post.class).getProvince());
                post.setPostDesc(dp.getValue(Post.class).getPostDesc());
                post.setRating(dp.getValue(Post.class).getRating());
                post.setSpec(dp.getValue(Post.class).getSpec());
                post.setCost(dp.getValue(Post.class).getCost());
                post.setCostRate(dp.getValue(Post.class).getCostRate());
                post.setTagList(dp.getValue(Post.class).getTagList());
                post.setImageUrls(dp.getValue(Post.class).getImageUrls());
                postList.add(post);
                //Log.i("imageurl",post.getImageUrls().toString());
                //Log.i("test",mStorageRef.child(post.getImageUrls().toString()));

                //name.setText(post.getName());
                //phoneNum.setText(post.getPhoneNum());
                //email.setText(post.getEmail());
                //desc.setText(post.getAbout());
                //ImageView test = getView().findViewById(R.id.profilePic);
                //String url = mStorageRef.child(post.getImageUrls()).getPath();

                //new PostFragment.DownloadImageTask(test)
                        //.execute(post.getImageUrls());
            }

            //showData(dataSnapshot);


            recyclerView = getView().findViewById(R.id.recycleView);
            //PostReAdapter postReAdapter= new PostReAdapter(moviesList);
            PostReAdapter postReAdapter= new PostReAdapter(postList);
            recyclerView.setHasFixedSize(true);

            recyclerView.setAdapter(postReAdapter);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);


        }


        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("read FB realtime: ", "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

    /*private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
    }*/

}
