package com.example.haveyou;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import static java.lang.String.*;

public class trends extends AppCompatActivity {

    //navigation
    private Button BackButton;
    private Button HomeButton;

    //date
    public TextView dateTimeDisplay;
    public Calendar calendar;
    private SimpleDateFormat dateFormat;
    public String date;

    //Array
    ArrayList<String> edit;
    public static final String TAG = "TAG";

    //user
    FirebaseAuth fbAuth;
    FirebaseFirestore fbStore;
    String userId;

    //View and adapter
    private RecyclerView recyclerView;
    private Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<array_item> Array_Item = new ArrayList<>();
    ArrayList<Object> data;
    ListView contents;




    ////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressLint({"WrongViewCast", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trends);

        dateTimeDisplay = (TextView)findViewById(R.id.date);
        calendar = Calendar.getInstance();

        dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        date = dateFormat.format(calendar.getTime());
        dateTimeDisplay.setText(date); //sets date


////////////////////////////////////////////////////////////////////////////////////////////////

        fbAuth = FirebaseAuth.getInstance();
        fbStore = FirebaseFirestore.getInstance();

        userId = fbAuth.getCurrentUser().getUid();

////////////////////////////////////////////////////////////////////////////////////////////////
        //Get a document - FireStore guide
        final String userID = fbAuth.getCurrentUser().getUid();

        CollectionReference documentReferenceRetrieve = fbStore.collection("users").document(userID)
                                                               .collection("date");


        documentReferenceRetrieve.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    //ArrayList<array_item> Array_Item = new ArrayList<>();
                    edit = new ArrayList<>();
                    for (QueryDocumentSnapshot documentReferenceRetrieve : task.getResult()) {

                        edit.add(documentReferenceRetrieve.getId());
                        Array_Item.add(new array_item(R.drawable.ic_history, "Open Date", documentReferenceRetrieve.getId()));

                        recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setHasFixedSize(true);

                        mLayoutManager = new LinearLayoutManager(null);
                        mAdapter = new Adapter(Array_Item);

                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setAdapter(mAdapter);
                    }

////////////////////////////////////////////////////////////////////////////////////////////////
                    //Click Card to get data
                    mAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Array_Item.get(position).getHistory(position);
                            String dateLocation = edit.get(position);
                            Log.d(TAG, "Clicked Position: " + dateLocation);

                            DocumentReference documentReferenceDate = fbStore.collection("users").document(userID)
                                                                             .collection("date").document(dateLocation);


                            contents = findViewById(R.id.dateContents);
                            documentReferenceDate.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        data = new ArrayList<>();
                                        Map<String, Object> contentMap = document.getData();

                                        for (Map.Entry<String, Object> entry : contentMap.entrySet()){

                                            data.add(entry.getKey()); // Adds String

                                            if (entry.getValue().equals(false)) // Adds Object
                                            {
                                                data.add("No");
                                            }
                                            if (entry.getValue().equals(true)) // Adds Object
                                            {
                                                data.add("Yes");
                                            }
                                        }
                                        Log.d(TAG, "THIS IS AN ARRAY : " + data); //Test
                                        Log.d(TAG, "This is a hash map : " + document.getData()); //Test
                                    }
                                    else {
                                        Log.d(TAG, "Failed with ", task.getException());
                                    }

                                }
                            });
                            // contents.set here
                                //MyAdapter adapter = new MyAdapter(cinemas);
                                //list.setAdapter(adapter);

                        }
                    });
                }


////////////////////////////////////////////////////////////////////////////////////////////////
                ///On Click Buttons

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
                        Toast.makeText(trends.this, "Logged out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), login.class));
                    }
                });

////////////////////////////////////////////////////////////////////////////////////////////////
            }


////////////////////////////////////////////////////////////////////////////////////////////////

        });
    }
}