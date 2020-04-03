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

public class edit_habits extends AppCompatActivity {

    public static final String TAG = "TAG";

    //navigation
    private Button BackButton;
    private Button HomeButton;

    EditText mhabit;
    Button mSubmit;
    ProgressBar progressBar;

    //fire store
    FirebaseAuth fbAuth;
    FirebaseFirestore fbStore;

    //user
    String userID;

    //date
    public Calendar calendar;
    private SimpleDateFormat dateFormat;
    public String date;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_habits);

////////////////////////////////////////////////////////////////////////////////////////////////
        //date
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(calendar.getTime());


////////////////////////////////////////////////////////////////////////////////////////////////
        //xml
        mhabit       = findViewById(R.id.enterHabit);
        mSubmit      = findViewById(R.id.submitHabit);

        progressBar = findViewById(R.id.progressBarRegister2);
////////////////////////////////////////////////////////////////////////////////////////////////

        fbAuth = FirebaseAuth.getInstance();
        fbStore = FirebaseFirestore.getInstance();

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String habit = mhabit.getText().toString().trim();

                if (TextUtils.isEmpty(habit)) {
                    mhabit.setError("New Habit cannot be empty!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

////////////////////////////////////////////////////////////////////////////////////////////////
                userID = fbAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fbStore.collection("users").document(userID)
                                                             .collection("date").document(String.valueOf(date));

                Map<String, Object> newHabit = new HashMap<>();
                newHabit.put(habit, false);

                documentReference.update(newHabit);

                DocumentReference documentReferenceSaved = fbStore.collection("users").document(userID)
                                                                  .collection("date").document("saved");
                newHabit.put(habit, false);

                documentReferenceSaved.update(newHabit);

                progressBar.setVisibility(View.INVISIBLE);

                startActivity(new Intent(getApplicationContext(),MainActivity.class));

            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////

        HomeButton = (Button) findViewById(R.id.home);
        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        BackButton = (Button) findViewById(R.id.logout);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////////

    }
}


