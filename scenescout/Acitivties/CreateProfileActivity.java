package com.example.mena.scenescout.Acitivties;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mena.scenescout.R;
import com.example.mena.scenescout.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CreateProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private EditText profileName, profileEmail, profilePhone, profileDesc;
    private Button saveBtn, addBtn;
    private User user;
    private String id;
    // bundle = new Bundle();
    //private String id = bundle.getString("id");
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Uri imageUri;
    private Bitmap bmp;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1;


    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "Users";
    public static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Log.d("check",FirebaseAuth.getInstance().getUid());
        //Log.d("check",FirebaseStorage.getInstance().getReference(FB_STORAGE_PATH).toString());
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        id = currentUser.getUid();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mStorageRef = FirebaseStorage.getInstance().getReference(FB_STORAGE_PATH);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseRef.keepSynced(true);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("name");
        String email = bundle.getString("email");
        String phoneNum = bundle.getString("phonenum");
        String desc = bundle.getString("desc");


        profileImage = findViewById(R.id.postImage);
        profileName = findViewById(R.id.postTitle);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        profileDesc = findViewById(R.id.profileDesc);

        saveBtn = findViewById(R.id.saveProfileButton);
        addBtn = findViewById(R.id.addPostImages);

        profileName.setText(name);
        profileEmail.setText(email);
        profilePhone.setText(phoneNum);
        profileDesc.setText(desc);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                openGallery();



                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE);*/
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String fullname = profileName.getText().toString().trim();
                final String teleNum = profilePhone.getText().toString().trim();
                final String descp = profileDesc.getText().toString().trim();

                if(TextUtils.isEmpty(fullname))
                {
                    Toast.makeText(getApplicationContext(), "Please enter a Name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(teleNum))
                {
                    Toast.makeText(getApplicationContext(), "Enter a Phone Number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(descp))
                {
                    Toast.makeText(getApplicationContext(), "Write a bit about your self or just N/A if you wish to keep it private", Toast.LENGTH_SHORT).show();
                    return;
                }


                //Log.i("imageURI:", imageUri.toString());
                if (imageUri != null)
                {
                    //final ProgressDialog dialog = new ProgressDialog(getApplicationContext());
                    //dialog.setTitle("Uploading Image");
                    //dialog.show();

                    // Get the storage reference
                    //StorageReference reference = mStorageRef.child(id + "/" + System.currentTimeMillis() + "." + getImageExt((imageUri)));
                    StorageReference reference = mStorageRef.child(id + "/" + "profilePic" + "." + getImageExt((imageUri)));

                    //Add file to reference
                    reference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //Dismiss dialog on success
                            //dialog.dismiss();
                            //Display success toast msg
                            Log.i("OnSuccess:", "Image Uploaded");
                            Toast.makeText(getApplicationContext(),"Image Uploaded", Toast.LENGTH_SHORT).show();

                            //Save image info into Firebase DataBase
                            Log.i("Check extension:", getImageExt(imageUri));
                            String imageLink = taskSnapshot.getDownloadUrl().toString();
                            user = new User(imageLink,profileName.getText().toString(),profileEmail.getText().toString(),profileDesc.getText().toString(),profilePhone.getText().toString());
                            Log.i("user:", taskSnapshot.getDownloadUrl().toString());
                            String uploadId = mDatabaseRef.push().getKey();


                            Log.i("id",id);
                            mDatabaseRef.child(id).setValue(user);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            //Dismiss dialog on error
                            //dialog.dismiss();
                            //Display error toast msg
                            Log.i("OnFailure:", e.getMessage());
                            Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Show Upload progress
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            //dialog.setMessage("Uploaded " + (int)progress +"");

                            //Show
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Please Select Image", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(CreateProfileActivity.this, MenuActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        /*if(requestCode ==  REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }*/


        if (requestCode == GALLERY) {
            if (data != null) {
                Log.i("urlcheck",data.getData().toString());
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    String path = saveImage(bitmap);
                    Toast.makeText(CreateProfileActivity.this, "Image Set!", Toast.LENGTH_SHORT).show();
                    profileImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreateProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    // getting extension from browsing for image
    public String getImageExt(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void openGallery()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.i("Image FIlE path", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

}
