package com.example.haveyou;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mName,mEmail,mPassword;
    Button mRegister,mLogin;
    ProgressBar progressBar;

    //fire store
    FirebaseAuth fbAuth;
    FirebaseFirestore fbStore;

    //user
    String userID;
    ArrayList<list_row_data> dataModels;

    //date
    public TextView dateTimeDisplay;
    public Calendar calendar;
    private SimpleDateFormat dateFormat;
    public String date;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mName       = findViewById(R.id.name);
        mEmail      = findViewById(R.id.email);
        mPassword   = findViewById(R.id.password);

        mRegister   = findViewById(R.id.registerButton);
        mLogin      = findViewById(R.id.loginButton);

        progressBar = findViewById(R.id.progressBarRegister);
        fbAuth      = FirebaseAuth.getInstance();
        fbStore     = FirebaseFirestore.getInstance();
////////////////////////////////////////////////////////////////////////////////////////////////
        //date
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(calendar.getTime());
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
                startActivity(new Intent(getApplicationContext(),login.class));
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////
        mLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String name = mName.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();


                if(TextUtils.isEmpty(name)){
                    mName.setError("First Name Must Be Submitted!");
                    return;
                }
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
                fbAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(register.this,"Welcome "+ name + " To Have You?", Toast.LENGTH_SHORT).show();

                            userID = fbAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fbStore.collection("users").document(userID);

                            Map<String,Object> user = new HashMap<>();
                            user.put("usersName", name );

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: "+ userID + " has been created");
                                }
                            });
/////////////////////////////////////////////////////////////////////
                            dataModels = new ArrayList<>();
                            // basic set of habits for new users
                            dataModels.add(new list_row_data("Eaten Healthy Foods", false));
                            dataModels.add(new list_row_data("Been Respectful", false));
                            dataModels.add(new list_row_data("Sleep On Time", false));
                            dataModels.add(new list_row_data("Gone To Gym", false));
                            dataModels.add(new list_row_data("Clean Up Mess", false));
                            dataModels.add(new list_row_data("Been Punctual", false));
                            dataModels.add(new list_row_data("Do Mobile Work", false));

                            DocumentReference documentReferenceUpdate = fbStore.collection("users").document(userID)
                                                                               .collection("date").document(String.valueOf(date));


                            HashMap<String, Object> habitsMap = new HashMap<>();
                            for (list_row_data data : dataModels) {

                                habitsMap.put(data.getName(), data.isChecked());
                                Log.d("Test", String.valueOf(dataModels.size()));

                            }
                            //implements basic habits on user creation
                            documentReferenceUpdate.set(habitsMap);

                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else {
                            Toast.makeText(register.this,"Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });
    }
}
