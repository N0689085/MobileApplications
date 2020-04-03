package com.example.haveyou;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    EditText mEmail,mPassword;
    Button mRegister,mLogin;
    ProgressBar progressBar;
    FirebaseAuth fbAuth;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mEmail      = findViewById(R.id.email);
        mPassword   = findViewById(R.id.password);

        mRegister   = findViewById(R.id.registerButton);
        mLogin      = findViewById(R.id.loginButton);

        progressBar = findViewById(R.id.progressBarLogin);
        fbAuth      = FirebaseAuth.getInstance();
////////////////////////////////////////////////////////////////////////////////////////////////
        if(fbAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
////////////////////////////////////////////////////////////////////////////////////////////////
        mRegister = (Button) findViewById(R.id.registerButton);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),register.class));
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////
        mLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email Must Be Submitted!");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password Must Be Submitted!");
                    return;
                }
                if (password.length() < 6){
                    mPassword.setError("Password Must Be Longer Than 5 Characters!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
////////////////////////////////////////////////////////////////////////////////////////////////
                fbAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(login.this,"Logged In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else {
                            Toast.makeText(login.this,"Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });
    }
}
