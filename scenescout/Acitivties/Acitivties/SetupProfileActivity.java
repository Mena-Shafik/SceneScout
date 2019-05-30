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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class SetupProfileActivity extends AppCompatActivity {

    private EditText  name, phoneNum, desc;
    private Button create;
    private ImageView pic;
    private String email;

    private User user;
    private Uri imageUri;
    private String id;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "Users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        ActionBar myActionBar=getSupportActionBar();
        myActionBar.hide();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        id = currentUser.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference(FB_STORAGE_PATH);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        create = findViewById(R.id.CreatePButton);
        name = findViewById(R.id.nameCreate);
        phoneNum = findViewById(R.id.phonenum);
        desc = findViewById(R.id.desc);
        pic = findViewById(R.id.pic);
        email = mAuth.getCurrentUser().getEmail().toString();



        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String fullname = name.getText().toString().trim();
                final String teleNum = phoneNum.getText().toString().trim();
                final String descp = desc.getText().toString().trim();


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
                                    user = new User(imageLink,fullname,email,descp,teleNum);
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

                Intent intent = new Intent(SetupProfileActivity.this, MenuActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });




        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("", " click");
                openGallery();
                Toast.makeText(SetupProfileActivity.this, "Test Getting pic",Toast.LENGTH_SHORT).show();
            }
        });

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {
            if (data != null) {
                Log.i("urlcheck",data.getData().toString());
                imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    String path = saveImage(bitmap);
                    Toast.makeText(SetupProfileActivity.this, "Image Set!", Toast.LENGTH_SHORT).show();
                    pic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SetupProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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
