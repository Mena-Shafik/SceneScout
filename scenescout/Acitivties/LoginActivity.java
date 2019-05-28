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

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn, signUpBtn, changePwBtn;
    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar myActionBar=getSupportActionBar();
        myActionBar.hide();

        loginBtn = findViewById(R.id.LoginButton);
        signUpBtn = findViewById(R.id.SignUpButton);
        changePwBtn = findViewById(R.id.ResetButton);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        //final FirebaseUser currentUser = mAuth.getCurrentUser();
        //Log.i("UID",currentUser.getUid());
        emailInput.setText("mena@mena.com");
        passwordInput.setText("bossman");

        /*mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    finish();
                }
                // ...
            }
        });*/



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = emailInput.getText().toString();
                final String password = passwordInput.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            // User is signed in
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            finish();
                        } else {
                            // User is signed out
                            //authenticate user
                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            // If sign in fails, display a message to the user. If sign in succeeds
                                            // the auth state listener will be notified and logic to handle the
                                            // signed in user can be handled in the listener.
                                            progressBar.setVisibility(View.GONE);
                                            if (!task.isSuccessful()) {
                                                // there was an error
                                                if (password.length() < 6) {
                                                    passwordInput.setError(getString(R.string.minimum_password));
                                                    Log.e("Login password","Fail");
                                                } else {
                                                    Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                mAuth = FirebaseAuth.getInstance();
                                                final FirebaseUser currentUser = mAuth.getCurrentUser();
                                                Log.i("UID in Login",currentUser.getUid());
                                                Log.i("Login ","Success");
                                                Toast.makeText(LoginActivity.this, "Login Succesful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                Bundle bundle = new Bundle();
                                                //String id = currentUser.getUid();
                                                bundle.putString("id",currentUser.getUid());
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                        }
                        // ...
                    }
                });

            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        changePwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, ChangepwActivity.class);
                //intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
