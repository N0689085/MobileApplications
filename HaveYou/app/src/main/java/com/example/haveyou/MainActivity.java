package com.example.haveyou;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //navigation
    private Button BackButton;
    private Button EditButton;
    private Button AddButton;

    //date
    public TextView dateTimeDisplay;
    public Calendar calendar;
    private SimpleDateFormat dateFormat;
    public String date;

    //Array
    ArrayList<list_row_data> dataModels;
    private CustomAdapter adapter;
    public static final String TAG = "TAG";

    //list
    ListView listView;
    TextView name;

    //user
    FirebaseAuth fbAuth;
    FirebaseFirestore fbStore;
    String userId;


////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint({"WrongViewCast", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dateTimeDisplay = (TextView)findViewById(R.id.date);
        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(calendar.getTime());
        dateTimeDisplay.setText(date); //sets date

        //Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_LONG).show();

////////////////////////////////////////////////////////////////////////////////////////////////

        name = findViewById(R.id.name);

        fbAuth = FirebaseAuth.getInstance();
        fbStore = FirebaseFirestore.getInstance();

        userId = fbAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fbStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                name.setText(documentSnapshot.getString("usersName")); //sets name

            }
        });


////////////////////////////////////////////////////////////////////////////////////////////////
    //Get a document - FireStore guide
        listView = (ListView) findViewById(R.id.yellowlist);
        final String userID = fbAuth.getCurrentUser().getUid();

        final DocumentReference documentReferenceRetrieve = fbStore.collection("users").document(userID)
                                                                   .collection("date").document(String.valueOf(date));


        documentReferenceRetrieve.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
/////////////////////////////////////

                        dataModels = new ArrayList<>();
                        Map<String, Object> mappedDocument = document.getData();

                        for (String key: mappedDocument.keySet()) {
                            Object value = mappedDocument.get(key);
                            dataModels.add(new list_row_data(key, (Boolean) value));
                        }


                        Log.d(TAG, "DocumentSnapshot DATA MODELS: " + dataModels);
/////////////////////////////////////



                        adapter = new CustomAdapter(dataModels, getApplicationContext());

                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView parent, View view, int position, long id) {

                                list_row_data dataModel= dataModels.get(position);
                                dataModel.checked = !dataModel.checked;
                                adapter.notifyDataSetChanged();

                            }
                        });
/////////////////////////////////////
                    } else {
                        DocumentReference documentReferenceSavedDocument = fbStore.collection("users").document(userID)
                                                                                  .collection("date").document("saved");

                        documentReferenceSavedDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    Log.d(TAG, "No such document, pulling saved document");


                                    dataModels = new ArrayList<>();
                                    Map<String, Object> mappedDocument = document.getData();

                                    Log.d(TAG, "document......" + mappedDocument);

                                    for (String key: mappedDocument.keySet()) {
                                        Object value = mappedDocument.get(key);
                                        dataModels.add(new list_row_data(key, (Boolean) value));
                                    }

                                    adapter = new CustomAdapter(dataModels, getApplicationContext());

                                    listView.setAdapter(adapter);

                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView parent, View view, int position, long id) {

                                            list_row_data dataModel = dataModels.get(position);
                                            dataModel.checked = !dataModel.checked;
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }

                            }
                        });
/////////////////////////////////////
                    }
                } else {
                    Log.d(TAG, "Failed with ", task.getException());
                }
            }
        });

////////////////////////////////////////////////////////////////////////////////////////////////
        ///On Click Buttons
        EditButton = (Button) findViewById(R.id.edit);
        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), edit_habits.class));
            }
        });

        AddButton = (Button) findViewById(R.id.home);
        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), trends.class));
            }
        });

        BackButton = (Button) findViewById(R.id.logout);
        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });


////////////////////////////////////////////////////////////////////////////////////////////////
        View submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                DocumentReference documentReferenceUpdate = fbStore.collection("users").document(userID)
                                                                   .collection("date").document(String.valueOf(date));


                HashMap<String, Object> habitsMap = new HashMap<>();
                for (list_row_data data : dataModels) {

                    habitsMap.put(data.getName(), data.isChecked());
                    Log.d("Test", String.valueOf(dataModels.size()));

                }
                documentReferenceUpdate.set(habitsMap); //updates new with new habit data
////////////////////////////////////////////////////////////////////////////////////////////////

                DocumentReference documentReference = fbStore.collection("users").document(userId);
                ////////DANGER
                Map<String,Object> MapDate = new HashMap<>();
                MapDate.put("habitDate", date );
                documentReference.update(MapDate); //////// ADDS HABIT DATE TO USER FIELDS
                ////////DANGER

////////////////////////////////////////////////////////////////////////////////////////////////

                DocumentReference documentReferenceSaved = fbStore.collection("users").document(userID)
                                                             .collection("date").document("saved");
                for (list_row_data data : dataModels) {

                    habitsMap.put(data.getName(), false); //saves most recent habits for app relaunch
                }
                documentReferenceSaved.set(habitsMap); //updates saved document


            }

        });
////////////////////////////////////////////////////////////////////////////////////////////////


    }
////////////////////////////////////////////////////////////////////////////////////////////////

}








