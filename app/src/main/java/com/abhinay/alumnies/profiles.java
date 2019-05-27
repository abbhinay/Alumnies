package com.abhinay.alumnies;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class profiles extends AppCompatActivity {

    FirebaseStorage storage;
    FirebaseDatabase database;
    FirebaseFirestore db;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        db = FirebaseFirestore.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyAdapter myAdapter = new MyAdapter(recyclerView, getApplicationContext(), new ArrayList<String>(), new ArrayList<String>());
        recyclerView.setAdapter(myAdapter);

        db.collection("alumni")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String fileName = document.getId();
//                                Log.d("profiles", fileName);
//                                String url = document.getData().get(fileName).toString();
//                                ((MyAdapter)recyclerView.getAdapter()).update(fileName, url);
//                                //Log.d(TAG, document.getId() + " => " + document.getData());
                                String name = document.get("name").toString();
                                String email = document.get("email").toString();
                                String branch = document.get("branch").toString();
                                ((MyAdapter)recyclerView.getAdapter()).update(name, email, branch);
                                Log.d("profiles", name);
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}
