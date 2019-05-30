package com.example.mena.scenescout.Acitivties;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mena.scenescout.GalleryAdapter;
import com.example.mena.scenescout.Model.LocationSpec;
import com.example.mena.scenescout.Model.Post;
import com.example.mena.scenescout.Model.User;
import com.example.mena.scenescout.NumberTextWatcher;
import com.example.mena.scenescout.PackageManagerUtils;
import com.example.mena.scenescout.R;
import com.example.mena.scenescout.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pchmn.materialchips.ChipView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreatePostActivity extends AppCompatActivity {

    private Button savePostButton, addImagesButton, addTagsButton;
    private ImageButton specButton, descButton;
    private EditText title, street, city, province, postDesc, taginput, costinput;
    private CheckBox parkingBox, smokingBox, elecBox, restroomBox, petsBox, wheelchairBox, eatingBox, kitchenBox, resistrctionBox, garageBox, wifiBox;
    private RadioGroup radioGroup;
    private RadioButton rb1,rb2;
    private LinearLayout tagsLayout;
    private String id;
    private Post post;
    private LocationSpec locationSpec;
    private Boolean pressed, pressed2;
    private String longitude, latitude;
    private ArrayList<String> imgList;
    private ArrayList<String> imgListShow;
    private ArrayList<String> tagsList;
    private ArrayList <String> oldTagelist = new ArrayList<String>();
    private ArrayList<Uri> imgUriList;
    private ArrayList<Bitmap> bitmapArrayList;
    private Uri imgUri;
    private String postCostRate, userEmail, userPhone;
    String imageEncoded;
    List<String> imagesEncodedList;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private  DatabaseReference mDatabaseRef2;

    private RecyclerView recyclerView;

    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1;

    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "Posts";


    private static final String CLOUD_VISION_API_KEY = "***************************************";
    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final int MAX_LABEL_RESULTS = 10;
    private static final int MAX_DIMENSION = 1200;

    private static final String TAG = CreateProfileActivity.class.getSimpleName();
    private static final int GALLERY_PERMISSIONS_REQUEST = 0;
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        id = currentUser.getUid();

        mStorageRef = FirebaseStorage.getInstance().getReference(FB_STORAGE_PATH);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);
        mDatabaseRef2 = FirebaseDatabase.getInstance().getReference("Users/"+id);
        mDatabaseRef.keepSynced(true);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // text fields
        title = findViewById(R.id.postTitle);
        street = findViewById(R.id.postStreet);
        city = findViewById(R.id.postCity);
        province = findViewById(R.id.postProv);
        postDesc = findViewById(R.id.postDesc);
        taginput = findViewById(R.id.tagInput);
        costinput = findViewById(R.id.cost);

        // radio buttons
        radioGroup = findViewById(R.id.radioGroup);
        rb1 = findViewById(R.id.radioButton);
        rb2 = findViewById(R.id.radioButton2);

        // all checkboxs
        parkingBox = findViewById(R.id.checkBoxParking);
        smokingBox = findViewById(R.id.checkBoxSmoking);
        elecBox = findViewById(R.id.checkBoxElec);
        restroomBox = findViewById(R.id.checkBoxRestroom);
        petsBox = findViewById(R.id.checkBoxPets);
        wheelchairBox = findViewById(R.id.checkBoxWheelchair);
        eatingBox = findViewById(R.id.checkBoxEating);
        kitchenBox = findViewById(R.id.checkBoxKitchen);
        resistrctionBox = findViewById(R.id.checkBoxResitrict);
        garageBox = findViewById(R.id.checkBoxGarage);
        wifiBox = findViewById(R.id.checkBoxWifi);

        // buttons and tags
        savePostButton = findViewById(R.id.savePostButton);
        addImagesButton = findViewById(R.id.addPostImages);
        addTagsButton = findViewById(R.id.tagsButton);
        tagsLayout = findViewById(R.id.tagsLayout);

        specButton = findViewById(R.id.specButton);
        descButton = findViewById(R.id.descButton);

        // list for images and tags
        imgUriList = new ArrayList<>();
        imgListShow = new ArrayList<>();
        imgList = new ArrayList<>();
        bitmapArrayList = new ArrayList<>();
        tagsList = new ArrayList<>();
        pressed = true;
        pressed2 = false;
        costinput.addTextChangedListener(new NumberTextWatcher(costinput, "#,###"));


        Bundle editBundle = getIntent().getExtras();
        Boolean idEdit = editBundle.getBoolean("isEdit");
        // if we are editing post
        /*if(idEdit)
        {
            title.setText(editBundle.getString("title"));
            String[] address = editBundle.getString("address").split(",");
            street.setText(address[0]);
            city.setText(address[1]);
            province.setText(address[2]);
            postDesc.setText(editBundle.getString("desc"));
            costinput.setText(editBundle.getString("price"));

            parkingBox.setChecked(editBundle.getBoolean("parking"));
            smokingBox.setChecked(editBundle.getBoolean("smoking"));
            elecBox.setChecked(editBundle.getBoolean("elec"));
            restroomBox.setChecked(editBundle.getBoolean("restroom"));
            petsBox.setChecked(editBundle.getBoolean("pets"));
            wheelchairBox.setChecked(editBundle.getBoolean("wheel"));
            eatingBox.setChecked(editBundle.getBoolean("eating"));
            kitchenBox.setChecked(editBundle.getBoolean("kitchen"));
            resistrctionBox.setChecked(editBundle.getBoolean("resist"));
            garageBox.setChecked(editBundle.getBoolean("garage"));
            wifiBox.setChecked(editBundle.getBoolean("wifi"));

            tagsList = editBundle.getStringArrayList("tagsList");
            for(int i = 0;i <tagsList.size();i++)
            {
                final ChipView chipView = new ChipView(getBaseContext());
                chipView.setLabel(tagsList.get(i));
                chipView.setPadding(2, 2, 2, 2);
                chipView.setChipBackgroundColor(getResources().getColor(R.color.colorPrimary));
                chipView.setLabelColor(getResources().getColor(R.color.white));
                chipView.setDeleteIconColor(getResources().getColor(R.color.white));
                chipView.setHasAvatarIcon(false);
                chipView.setDeletable(true);
                tagsLayout.addView(chipView);
                taginput.setText("");

                chipView.setOnDeleteClicked(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tagsLayout.removeView(chipView);
                        tagsList.remove(chipView.getLabel().toString());
                    }
                });
            }

            String rate = editBundle.getString("rate");
            if(rate.equals("Day"))
            {
                rb2.setChecked(true);
            }
            else if(rate.equals("Hour"))
            {
                rb1.setChecked(true);
            }

            ArrayList<String> imageList = editBundle.getStringArrayList("imgList");
            recyclerView = this.findViewById(R.id.galleryView);
            recyclerView.setHorizontalScrollBarEnabled(true);

            final GalleryAdapter galleryAdapter= new GalleryAdapter(bitmapArrayList);
            final GridLayoutManager layoutManager = new GridLayoutManager(this,1);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {

                            view.setVisibility(View.GONE);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(galleryAdapter);
                            Toast.makeText(getApplicationContext(),"Image Removed", Toast.LENGTH_SHORT).show();
                            bitmapArrayList.remove(position);
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );


            savePostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = currentUser.getUid();
                    //mStorageRef.get
                }
            });
        }*/



        //onMapReady(gmap);g
        addImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        specButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pressed)
                {
                    parkingBox.setVisibility(View.GONE);
                    smokingBox.setVisibility(View.GONE);
                    elecBox.setVisibility(View.GONE);
                    restroomBox.setVisibility(View.GONE);
                    petsBox.setVisibility(View.GONE);
                    wheelchairBox.setVisibility(View.GONE);
                    eatingBox.setVisibility(View.GONE);
                    kitchenBox.setVisibility(View.GONE);
                    resistrctionBox.setVisibility(View.GONE);
                    garageBox.setVisibility(View.GONE);
                    wifiBox.setVisibility(View.GONE);
                    pressed = true;
                    specButton.setImageResource(R.drawable.angle_pointing_left);
                }
                else if(pressed)
                {
                    parkingBox.setVisibility(View.VISIBLE);
                    smokingBox.setVisibility(View.VISIBLE);
                    elecBox.setVisibility(View.VISIBLE);
                    restroomBox.setVisibility(View.VISIBLE);
                    petsBox.setVisibility(View.VISIBLE);
                    wheelchairBox.setVisibility(View.VISIBLE);
                    eatingBox.setVisibility(View.VISIBLE);
                    kitchenBox.setVisibility(View.VISIBLE);
                    resistrctionBox.setVisibility(View.VISIBLE);
                    garageBox.setVisibility(View.VISIBLE);
                    wifiBox.setVisibility(View.VISIBLE);
                    pressed = false;
                    specButton.setImageResource(R.drawable.angle_arrow_down);
                }

            }
        });

        descButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pressed2)
                {
                    postDesc.setVisibility(View.GONE);
                    pressed2 = true;
                    descButton.setImageResource(R.drawable.angle_pointing_left);
                }
                else if(pressed2)
                {
                    postDesc.setVisibility(View.VISIBLE);
                    pressed2 = false;
                    descButton.setImageResource(R.drawable.angle_arrow_down);
                }
            }
        });

        savePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String postTitle = title.getText().toString().trim();
                final String postStreet = street.getText().toString().trim();
                final String postCity = city.getText().toString().trim();
                final String postdescp = postDesc.getText().toString().trim();
                final String prov = province.getText().toString().trim();
                final String cost = costinput.getText().toString();
                //final Double cost = Double.parseDouble(tempCost);

                mDatabaseRef2.child(id);
                mDatabaseRef2.addValueEventListener(userListener);

                if(rb1.isChecked())
                    postCostRate = rb1.getText().toString();
                else if(rb2.isChecked())
                    postCostRate = rb2.getText().toString();

                //String imglink1 = "http://www.kinyu-z.net/data/wallpapers/190/1368568.jpg";
                //String imglink2 = "http://www.kinyu-z.net/data/wallpapers/190/1368631.jpg";
                //String imglink3 = "https://wallpapercave.com/wp/wp1951689.jpg";
                //final ArrayList<String> imglist = new ArrayList<String>();
                //imglist.add(imglink1);
                //imglist.add(imglink2);
                //imglist.add(imglink3);


                if (TextUtils.isEmpty(postTitle)) {
                    Toast.makeText(getApplicationContext(), "Please enter a title!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(postCity)) {
                    Toast.makeText(getApplicationContext(), "Enter a city!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(postdescp)) {
                    Toast.makeText(getApplicationContext(), "Write a bit about the location you are posting as well as payment plans, day available to rent on< what equipment available or they have to bring their own", Toast.LENGTH_SHORT).show();
                    return;
                }
                //mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users/"+id);
                //final String phone = mDatabaseRef.child("phoneNum").toString();
                //String email = mDatabaseRef.child("email").toString();
                locationSpec = new LocationSpec(parkingBox.isChecked(), smokingBox.isChecked(), elecBox.isChecked(),wifiBox.isChecked(), restroomBox.isChecked(), petsBox.isChecked(), garageBox.isChecked(),
                        kitchenBox.isChecked(), wheelchairBox.isChecked(), eatingBox.isChecked(),resistrctionBox.isChecked());



                //Log.i("imageURI:", imageUri.toString());
                if (imgUriList != null)
                //if(true)
                {
                    // Get the storage reference
                    final String uploadId = mDatabaseRef.push().getKey();
                    for (int i = 0; i < imgUriList.size(); i++) {
                        StorageReference reference = mStorageRef.child(id + "/Posts/" + uploadId + "/" + "postImage" + i + "." + getImageExt((imgUriList.get(i))));
                        reference.putFile(imgUriList.get(i))
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //ArrayList<String> list = new ArrayList<String>();
                                        //Dismiss dialog on success
                                        //dialog.dismiss();
                                        //Display success toast msg
                                        Log.i("OnSuccess:", "Image Uploaded");
                                        Toast.makeText(getApplicationContext(), "Uploaded " + imgUriList.size() + " Image", Toast.LENGTH_SHORT).show();
                                        imgList.add(taskSnapshot.getDownloadUrl().toString());
                                        //Save image info into Firebase DataBase
                                        Log.i("Check extension:", getImageExt(imgUri));
                                        String imageLink = id + "/" + "profilePic" + "." + getImageExt((imgUri));
                                        post = new Post(imgList, postTitle, postStreet, postCity, prov, postdescp, 0, tagsList, cost, postCostRate,locationSpec, userEmail, userPhone);
                                        Log.i("post:", taskSnapshot.getDownloadUrl().toString());


                                        Log.i("id", id);
                                        mDatabaseRef.child(id + "/" + uploadId).setValue(post);

                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                //Dismiss dialog on error
                                //dialog.dismiss();
                                //Display error toast msg
                                Log.i("OnFailure:", e.getMessage());
                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

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
                    /*StorageReference reference = mStorageRef.child(id + "/Posts/" + uploadId + "/"+"postImage" +0+ "." + getImageExt((imgUri)));

                    //Add file to reference
                    reference.putFile(imgUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //ArrayList<String> list = new ArrayList<String>();
                                    //Dismiss dialog on success
                                    //dialog.dismiss();
                                    //Display success toast msg
                                    Log.i("OnSuccess:", "Image Uploaded");
                                    Toast.makeText(getApplicationContext(),"Image Uploaded", Toast.LENGTH_SHORT).show();
                                    imgList.add(taskSnapshot.getDownloadUrl().toString());
                                    //Save image info into Firebase DataBase
                                    Log.i("Check extension:", getImageExt(imgUri));
                                    String imageLink = id + "/" + "profilePic" + "." + getImageExt((imgUri));
                                    post = new Post(imgList, postTitle, postCity, prov, postdescp, 0);
                                    Log.i("post:", taskSnapshot.getDownloadUrl().toString());
                                    //recyclerView = findViewById(R.id.galleryView);
                                    //GalleryAdapter galleryAdapter = new GalleryAdapter(imgList);
                                    //.setAdapter(galleryAdapter);
                                    //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    //recyclerView.setLayoutManager(layoutManager);


                                    Log.i("id",id);
                                    mDatabaseRef.child(id +"/" + uploadId).setValue(post);

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
                    });*/
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(CreatePostActivity.this, MenuActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });

        addTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String tag;
                tag = taginput.getText().toString();
                if (tag.equals(""))
                    Toast.makeText(getApplicationContext(), "Empty tags not allowed!", Toast.LENGTH_SHORT).show();
                else if (tagsList.contains(tag))
                    Toast.makeText(getApplicationContext(), "No Duplicates!", Toast.LENGTH_SHORT).show();
                else {
                    tagsList.add(tag);
                    final ChipView chipView = new ChipView(getBaseContext());
                    chipView.setLabel(tag);
                    chipView.setPadding(2, 2, 2, 2);
                    chipView.setChipBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    chipView.setLabelColor(getResources().getColor(R.color.white));
                    chipView.setDeleteIconColor(getResources().getColor(R.color.white));
                    chipView.setHasAvatarIcon(false);
                    chipView.setDeletable(true);
                    tagsLayout.addView(chipView);
                    taginput.setText("");

                    chipView.setOnDeleteClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tagsLayout.removeView(chipView);
                            tagsList.remove(chipView.getLabel().toString());
                        }
                    });
                }
            }
        });

        //ChipView chipView = new ChipView(this);
        //chipView.setLabel("test");
        //chipView.setPadding(2,2,2,2);
        //chipView.setHasAvatarIcon(false);
        //chipView.setDeletable(true);


        //tagsLayout.addView(chipView);




        /*else
        {
            String imglink1 = "http://www.kinyu-z.net/data/wallpapers/190/1368568.jpg";
            String imglink2 = "http://www.kinyu-z.net/data/wallpapers/190/1368631.jpg";
            String imglink3 = "https://wallpapercave.com/wp/wp1951689.jpg";
            ArrayList<String> imglistt = new ArrayList<String>();
            imglistt.add(imglink1);
            imglistt.add(imglink2);
            imglistt.add(imglink3);
            //recyclerView = findViewById(R.id.galleryView);
            //GalleryAdapter galleryAdapter = new GalleryAdapter(imglistt);
            //recyclerView.setAdapter(galleryAdapter);
            //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
            //recyclerView.setLayoutManager(layoutManager);
        }*/
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY) {

            String [] imagepthList = {MediaStore.Images.Media.DATA};
            Log.i("imagespaths", imagepthList.toString());
            // if adding only 1 image
            if (data != null) {
                Log.i("urlcheck",data.getData().toString());
                imgUri = data.getData();
                //showExif(imgUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                    String path = saveImage(bitmap);
                    bitmapArrayList.add(bitmap);
                    Toast.makeText(CreatePostActivity.this, "Images Selected", Toast.LENGTH_SHORT).show();
                    //postImages.setImageBitmap(bitmap);
                    imgListShow.add(imgUri.toString());
                    imgUriList.add(imgUri);

                    recyclerView = this.findViewById(R.id.galleryView);
                    recyclerView.setHorizontalScrollBarEnabled(true);

                    final GalleryAdapter galleryAdapter= new GalleryAdapter(bitmapArrayList);
                    final GridLayoutManager layoutManager = new GridLayoutManager(this,1);
                    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    /*recyclerView.addOnItemTouchListener(
                            new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                                @Override public void onItemClick(View view, int position) {

                                    //view.setVisibility(View.GONE);
                                    //recyclerView.setLayoutManager(layoutManager);
                                    //recyclerView.setAdapter(galleryAdapter);
                                    //Toast.makeText(getApplicationContext(),"Image Removed", Toast.LENGTH_SHORT).show();
                                    //bitmapArrayList.remove(position);
                                    //imgUriList.remove(position);
                                }

                                @Override public void onLongItemClick(View view, int position) {
                                    // do whatever
                                }
                            })
                    );*/
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(galleryAdapter);
                    //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    //recyclerView.setLayoutManager(layoutManager);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CreatePostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
            /*else if (data.getClipData() != null)
            {
                ClipData mClipData = data.getClipData();
                //ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    imgUriList.add(uri);
                    //recyclerView = findViewById(R.id.galleryView);
                    //GalleryAdapter galleryAdapter = new GalleryAdapter(imgList);
                    //recyclerView.setAdapter(galleryAdapter);
                    //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    //recyclerView.setLayoutManager(layoutManager);
                    // Get the cursor
                    Cursor cursor = getContentResolver().query(uri, imagepthList, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(imagepthList[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    imagesEncodedList.add(imageEncoded);
                    cursor.close();

                }
            }*/
            else
                Toast.makeText(CreatePostActivity.this, "ERROR!", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    // extract image geolocation
    /*@TargetApi(Build.VERSION_CODES.N)
    void showExif(Uri photoUri) {
        if (photoUri != null) {

            ParcelFileDescriptor parcelFileDescriptor = null;

            /*
            How to convert the Uri to FileDescriptor, refer to the example in the document:
            https://developer.android.com/guide/topics/providers/document-provider.html
             *//*
            try {
                parcelFileDescriptor = getContentResolver().openFileDescriptor(photoUri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();


                ExifInterface exifInterface = new ExifInterface(fileDescriptor);
                String exif = "Exif: " + fileDescriptor.toString();
                exif += "\n DATETIME: " +
                        exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                exif += "\n TAG_GPS_LATITUDE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                exif += "\n TAG_GPS_LATITUDE_REF: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
                exif += "\n TAG_GPS_LONGITUDE: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                exif += "\n TAG_GPS_LONGITUDE_REF: " +
                        exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

                longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);

                parcelFileDescriptor.close();

                Toast.makeText(getApplicationContext(),
                        exif,
                        Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        "Something wrong:\n" + e.toString(),
                        Toast.LENGTH_LONG).show();
            }

            String strPhotoPath = photoUri.getPath();

        } else {
            Toast.makeText(getApplicationContext(),
                    "photoUri == null",
                    Toast.LENGTH_LONG).show();
        }
    }*/

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
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(photoPickerIntent, 1);
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        callCloudVision(myBitmap);
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







    private Vision.Images.Annotate prepareAnnotationRequest(final Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        com.google.api.client.json.JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            com.google.api.services.vision.v1.model.Image base64EncodedImage = new com.google.api.services.vision.v1.model.Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature labelDetection = new Feature();
                labelDetection.setType("LABEL_DETECTION");
                labelDetection.setMaxResults(MAX_LABEL_RESULTS);
                add(labelDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<CreatePostActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(CreatePostActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return response.toString();

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            CreatePostActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {

                Set<String> stringSet = new HashSet<>();

                try {
                    JSONObject obj = new JSONObject(result);
                    JSONArray stockObject = obj.getJSONArray("responses");
                    JSONArray statsArray = stockObject.getJSONObject(0).getJSONArray("labelAnnotations");
                    for(int i = 0; i < statsArray.length(); i++) {
                        stringSet .add(statsArray.getJSONObject(i).getString("description"));
                    }
                    tagsList.clear();
                    tagsList.addAll(stringSet);

                    for(int i = 0; i < tagsList.size(); i++){
                        //tagsList.add(stringSet.);

                            final ChipView chipView = new ChipView(getBaseContext());
                            if(!oldTagelist.contains(tagsList.get(i))) {
                                chipView.setLabel(statsArray.getJSONObject(i).getString("description"));
                                chipView.setPadding(2, 2, 2, 2);
                                chipView.setChipBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                chipView.setLabelColor(getResources().getColor(R.color.white));
                                chipView.setDeleteIconColor(getResources().getColor(R.color.white));
                                chipView.setHasAvatarIcon(false);
                                chipView.setDeletable(true);
                                tagsLayout.addView(chipView);
                                taginput.setText("");
                            }

                        chipView.setOnDeleteClicked(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tagsLayout.removeView(chipView);
                                tagsList.remove(chipView.getLabel().toString());
                            }
                        });
                    }
                    Log.d("COMPARE", oldTagelist.toString());
                    oldTagelist.clear();
                    oldTagelist = tagsList;
                    Log.d("COMPARE", oldTagelist.toString());
                    Log.d("COMPARE", taginput.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.d(TAG, result);
                Toast.makeText(getApplicationContext(),"Tags have been generated", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),result, Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Switch text to loading
       // mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            userEmail = dataSnapshot.getValue(User.class).getEmail();
            userPhone = dataSnapshot.getValue(User.class).getPhoneNum();

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("read FB realtime: ", "loadUSer:onCancelled", databaseError.toException());
        }
    };


}
