package com.example.mena.scenescout.Acitivties;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mena.scenescout.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;


    private FirebaseAuth mAuth;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ActionBar myActionBar=getSupportActionBar();
        myActionBar.hide();

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        registerBtn = findViewById(R.id.RegisterButton);
        emailInput = findViewById(R.id.regemail);
        passwordInput = findViewById(R.id.regpassword);


        progressBar = findViewById(R.id.progressBar);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.i("Sign Up ","Failed");
                                    //Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),Toast.LENGTH_SHORT).show();
                                    Toast.makeText(SignUpActivity.this, "Account Creation Failed." + task.getException(),Toast.LENGTH_SHORT).show();
                                    //Intent intent = new Intent(SignUpActivity.this, .class);
                                    //intent.putExtras(bundle);
                                    //startActivity(intent);
                                } else {
                                    Log.i("Login ","Back to login page");
                                    final FirebaseUser currentUser = mAuth.getCurrentUser();
                                    id = currentUser.getUid();
                                    Intent intent = new Intent(SignUpActivity.this, SetupProfileActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });



                //Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                //intent.putExtras(bundle);
                //startActivity(intent);

                //finish();

            }
        });

        /*pic.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {
                Log.v("", " click");
                openGallery();
                //Toast.makeText(SignUpActivity.this, "Test Getting pic",Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }




}
