package com.example.mena.scenescout.Acitivties;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.mena.scenescout.Fragments.PostFragment;
import com.example.mena.scenescout.Fragments.ProfileFragment;
import com.example.mena.scenescout.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuActivity extends AppCompatActivity {

    FragmentTransaction transaction;
    private Button updateBtn;
    private FirebaseAuth mAuth;
    private  FirebaseUser currentUser;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        //Bundle bundle = getIntent().getExtras();
        //id = bundle.getString("id");
        //currentUser = mAuth.getCurrentUser();
        //Log.i("UID in Menu",currentUser.getUid());

        updateBtn = findViewById(R.id.updateButton);
        /*Button profileBtn = (Button) findViewById(R.id.ProfileButton);
        Button postBtn = (Button) findViewById(R.id.PostButton);
        Button groupBtn = (Button) findViewById(R.id.GroupButton);
        Button searchBtn = (Button) findViewById(R.id.SearchButton);*/
        android.support.v4.app.FragmentManager fragMan = getSupportFragmentManager();
        //transaction = fragMan.beginTransaction()..beginTransaction();
        //final Fragment myFrag = new Fragment();
        //transaction.add(myFrag,"");
        //transaction.commit();


/*       profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuActivity.this, PostActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent);

            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MenuActivity.this, SearchActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent);

            }
        });*/
        final ProfileFragment pfrag = new ProfileFragment();
        //Bundle bundle = new Bundle();
        //bundle.putString("id", id);
        //pfrag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment, pfrag, "").
                commit();



        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        //android.support.v4.app.Fragment selectedFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
                        switch (item.getItemId()) {
                            case R.id.action_post:
                                //Intent intent2 = new Intent(MenuActivity.this, PostActivity.class);
                                //intent.putExtras(bundle);
                                //startActivity(intent2);
                                //getSupportFragmentManager().beginTransaction().remove(selectedFragment).commit();
                                //transaction.setCustomAnimations(R.anim.enter_from_right);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new PostFragment(), "post").commit();
                                break;
                            case R.id.action_profile:
                                //Intent intent3 = new Intent(MenuActivity.this, ProfileActivity.class);
                                //intent.putExtras(bundle);
                                //startActivity(intent3);
                                //onCreateOptionsMenu();
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new ProfileFragment(), "profile").commit();
                                break;
                            case R.id.action_logout:
                                mAuth.signOut();
                                finish();
                                Intent intent3 = new Intent(MenuActivity.this, LoginActivity.class);
                                startActivity(intent3);
                                break;
                            /*case R.id.action_group:
                                //Intent intent4 = new Intent(MenuActivity.this, GroupActivity.class);
                                //intent.putExtras(bundle);
                                //startActivity(intent4);
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new GroupFragment(), "group").commit();
                                break;*/
                        }
                        return true;
                    }
                });

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        //SearchView searchView = (SearchView) item.getActionView();
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }

/*        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(MenuActivity.this, "search submitted", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Toast.makeText(MenuActivity.this, "text changed", Toast.LENGTH_LONG).show();
                return false;
            }
        });*/

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
           /* case R.id.action_gotosearch:
                //Intent intent1 = new Intent(MenuActivity.this, SearchActivity.class);
                //intent.putExtras(bundle);
                //startActivity(intent1);
                break;*/
            case R.id.action_gotoadd:
                Intent intent2 = new Intent(MenuActivity.this, CreatePostActivity.class);
                intent2.putExtra("isEdit",false);
                startActivity(intent2);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void buttonClick(View v) {
        switch(v.getId()) {
            case R.id.updateButton:
                Intent intent2 = new Intent(MenuActivity.this, CreateProfileActivity.class);
                // for ex: your package name can be "com.example"
                // your activity name will be "com.example.Contact_Developer"
                startActivity(intent2);
                //finish();
                break;
        }
    }



/*    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_search) {
            //Toast.makeText(this, "", Toast.LENGTH_SHORT).show();.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            //Intent intent = new Intent(MenuActivity.this, SearchActivity.class);
            //intent.putExtras(bundle);
            //startActivity(intent);
            //return true;
        //}

        return super.onOptionsItemSelected(item);
    }*/

    public FirebaseUser getID(){
        return currentUser;
    }
}
