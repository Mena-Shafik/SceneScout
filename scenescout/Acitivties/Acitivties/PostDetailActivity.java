package com.example.mena.scenescout.Acitivties;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mena.scenescout.RecyclerItemClickListener;
import com.google.android.gms.gcm.Task;
import com.example.mena.scenescout.Fragments.MapFragment;
import com.example.mena.scenescout.GalleryChosenAdapter;
import com.example.mena.scenescout.R;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.pchmn.materialchips.ChipView;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class PostDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Button  editPostButton;
    private ImageButton specButton, descButton;
    private Intent intent;
    private TextView detailTitle, detailDesc, detailLocation, costPerRate;
    private CheckBox parkingBox, smokingBox, elecBox, restroomBox, petsBox, wheelchairBox, eatingBox, kitchenBox, resistrctionBox, garageBox, wifiBox;
    private RatingBar detailrate;
    private LinearLayout tagsLayout;
    //private MapView mapView;
    private GoogleMap gmap;
    //private ImageView detailImage;
    private Boolean pressed, pressed2, pressed3;
    boolean clicked;
    //map stuff
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted = false;
    private String address = "Lost";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String TAG = "PostDetailActivity";

    private RecyclerView recyclerView;

    private ArrayList<String> imgList;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        final Bundle bundle;
        bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        String desc = bundle.getString("desc");
        String street = bundle.getString("street");
        //Log.i("street",street);
        String city = bundle.getString("city");
        String prov = bundle.getString("prov");
        String costRate = bundle.getString("price") + "  Per " + bundle.get("rate");
        address = street+ ", " +city+", " +prov;
        int rate = bundle.getInt("rate");
        final ArrayList<String> tags = bundle.getStringArrayList("tags");
        //imgList = new ArrayList<>();
        imgList = bundle.getStringArrayList("imglist");

        String longs = bundle.getString("long");
        final String latis = bundle.getString("lati");

        specButton = findViewById(R.id.specButton2);
        descButton = findViewById(R.id.descButton2);
        detailTitle = findViewById(R.id.detailTitle);
        detailDesc = findViewById(R.id.detailDesc);
        detailLocation = findViewById(R.id.detailLocation);
        detailrate = findViewById(R.id.listRatingBar);
        //mapView = findViewById(R.id.mapView);
        tagsLayout = findViewById(R.id.tagsLayout);
        //detailImage = findViewById(R.id.detailImageView);
        pressed = false;
        pressed2 = false;
        pressed3 = true;
        clicked = false;
        detailTitle.setText(title);
        detailDesc.setText(desc);
        detailLocation.setText(street+ ", " +city+ ", " +prov);
        detailrate.setRating(rate);

        //new PostDetailActivity.DownLoadImageTask(detailImage).execute(imgList.get(0));
        costPerRate = findViewById(R.id.costPerRate);

        costPerRate.setText(costRate);

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

        editPostButton = findViewById(R.id.editPostButton);

        parkingBox.setChecked(bundle.getBoolean("parking"));
        smokingBox.setChecked(bundle.getBoolean("smoking"));
        elecBox.setChecked(bundle.getBoolean("elec"));
        restroomBox.setChecked(bundle.getBoolean("restroom"));
        petsBox.setChecked(bundle.getBoolean("pets"));
        wheelchairBox.setChecked(bundle.getBoolean("wheel"));
        eatingBox.setChecked(bundle.getBoolean("eating"));
        kitchenBox.setChecked(bundle.getBoolean("kitchen"));
        resistrctionBox.setChecked(bundle.getBoolean("food"));
        garageBox.setChecked(bundle.getBoolean("garage"));
        wifiBox.setChecked(bundle.getBoolean("wifi"));



        recyclerView = this.findViewById(R.id.detailGalleryView);
        recyclerView.setHorizontalScrollBarEnabled(true);

        GalleryChosenAdapter galleryAdapter = new GalleryChosenAdapter(imgList);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 1);

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setHasFixedSize(true);


        final ImageView expandedImage = findViewById(R.id.expandedImage);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        //Toast.makeText(getApplicationContext(),"clicked", Toast.LENGTH_SHORT).show();
                        //layoutManager.findViewByPosition(position);

                        if(!clicked) {
                            //Toast.makeText(getApplicationContext(),"false", Toast.LENGTH_SHORT).show();
                            //recyclerView.get
                            //ImageView image = findViewById(R.id.imageGallery);
                            //Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                            expandedImage.setVisibility(View.VISIBLE);
                            new DownLoadImageTask(expandedImage).execute(imgList.get(position));
                            //rotate(90,expandedImage);
                            //expandedImage.setImageBitmap(bitmap);
                            detailrate.setVisibility(View.GONE);
                            clicked = true;
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        recyclerView.setAdapter(galleryAdapter);
        recyclerView.setLayoutManager(layoutManager);

        expandedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    expandedImage.setVisibility(View.GONE);
                    detailrate.setVisibility(View.VISIBLE);
                    clicked = false;
            }
        });
        //showExif(imgList.get(0));

        // getSupportFragmentManager().beginTransaction().add(R.id.fragment2, new MapFragment(), "").
        //commit();


        if(longs == null && latis == null)
            getLocationPermission();
        else
            moveCamera(new LatLng(Double.parseDouble(latis), Double.parseDouble(longs)), 15, address);


        for(int i =0;i <tags.size(); i++) {
            final ChipView chipView = new ChipView(getBaseContext());
            chipView.setLabel(tags.get(i).toString());
            chipView.setPadding(2, 2, 2, 2);
            chipView.setChipBackgroundColor(getResources().getColor(R.color.colorPrimary));
            chipView.setLabelColor(getResources().getColor(R.color.white));
            chipView.setDeleteIconColor(getResources().getColor(R.color.white));
            chipView.setHasAvatarIcon(false);
            chipView.setDeletable(false);
            tagsLayout.addView(chipView);
        }

        editPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailActivity.this, CreatePostActivity.class);
                Bundle editbundle = new Bundle();
                editbundle.putString("id",detailTitle.getText().toString());
                editbundle.putString("title",detailTitle.getText().toString());
                editbundle.putString("desc",detailDesc.getText().toString());
                editbundle.putString("address",detailLocation.getText().toString());

                editbundle.putStringArrayList("tagsList",tags);
                editbundle.putStringArrayList("imgList",imgList);

                editbundle.putBoolean("parking",parkingBox.isChecked());
                editbundle.putBoolean("smoking",smokingBox.isChecked());
                editbundle.putBoolean("elec",elecBox.isChecked());
                editbundle.putBoolean("restroom",restroomBox.isChecked());
                editbundle.putBoolean("pets",petsBox.isChecked());
                editbundle.putBoolean("wheel",wheelchairBox.isChecked());
                editbundle.putBoolean("eating",eatingBox.isChecked());
                editbundle.putBoolean("kitchen",kitchenBox.isChecked());
                editbundle.putBoolean("resist",resistrctionBox.isChecked());
                editbundle.putBoolean("garage",garageBox.isChecked());
                editbundle.putBoolean("wifi",wifiBox.isChecked());
                editbundle.putBoolean("isEdit", true);

                editbundle.putString("price", bundle.getString("price"));
                editbundle.putString("rate",bundle.getString("rate"));
                intent.putExtras(editbundle);
                startActivity(intent);
                finish();

            }
        });

        /*String tag;
        tag = taginput.getText().toString();
        if(tag.equals(""))
            //Toast.makeText(getApplicationContext(),"Empty tags not allowed!", Toast.LENGTH_SHORT).show();
            //no empty tags allowed
        else if(tagsList.contains(tag))
            Toast.makeText(getApplicationContext(),"No Duplicates!", Toast.LENGTH_SHORT).show();
        else
        {
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
        }*/

        specButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pressed) {
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
                } else if (pressed) {
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
                if (!pressed2) {
                    detailDesc.setVisibility(View.GONE);
                    pressed2 = true;
                    descButton.setImageResource(R.drawable.angle_pointing_left);
                } else if (pressed2) {
                    detailDesc.setVisibility(View.VISIBLE);
                    pressed2 = false;
                    descButton.setImageResource(R.drawable.angle_arrow_down);
                }
            }
        });


    }

    private void getDeviceLocation() {
        Log.d("getDeviceLocation", "getting current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                com.google.android.gms.tasks.Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        if (task.isSuccessful()) {
                            Log.d("onComplete", "found location");
                            Location currentLocation = (Location) task.getResult();


                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15, address);
                        } else {
                            Log.d("onComplete", "current location is null");
                            Toast.makeText(PostDetailActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("getDeviceLocation", "Security Expection: " + e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom, String address) {
        Log.d("moveCamera", "moving camera to: latitude:" + latLng.latitude + " longitude: " + latLng.longitude);
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions  options = new MarkerOptions().position(latLng).title(address);
        gmap.addMarker(options);
    }

    private void initMap() {
        Log.d("initMap", "initializing map");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        supportMapFragment.getMapAsync(PostDetailActivity.this);


    }

    private void getLocationPermission() {
        Log.d("getLocationPermission", "getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("RequestPermissResult", "called");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d("RequestPermissResult", "failed");
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    Log.d("RequestPermissResult", "granted");
                    // initialize our map
                    initMap();
                }
            }
        }
    }

    /*  private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }


            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.

        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();

                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.

                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }


            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }*/


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        //mapView.onSaveInstanceState(mapViewBundle);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d("onMapReady", "Map is ready");
        gmap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            gmap.setMyLocationEnabled(true);
        }
    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private void rotate(float degree, ImageView imageView) {
        final RotateAnimation rotateAnim = new RotateAnimation(0.0f, degree,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnim.setDuration(0);
        rotateAnim.setFillAfter(true);
        imageView.startAnimation(rotateAnim);
    }
}
