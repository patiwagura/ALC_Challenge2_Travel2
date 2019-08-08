package com.pato.travelmantics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {

    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mDbReference;
    private ChildEventListener mChildListener;  //listen to any changes in our data
    private DealAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FirebaseUtil.openFbReference("traveldeals", this);
        RecyclerView recyclerDeals = (RecyclerView) findViewById(R.id.recyclerDeals);
        mAdapter = new DealAdapter(this);
        recyclerDeals.setAdapter(mAdapter);

        //create a layout manager.
        LinearLayoutManager dealsLayoutMgr = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerDeals.setLayoutManager(dealsLayoutMgr);

        //Using FirebaseUtil class get a reference to FirebaseDatabase and DatabaseReference
        //FirebaseUtil.openFbReference("traveldeals");
        //mFirebaseDb = FirebaseUtil.firebaseDb; //reference to firebaseDb.
        //mDbReference = FirebaseUtil.dbReference; //

        //get instance of FirebaseDatabase.
        //mFirebaseDb = FirebaseDatabase.getInstance();

        //db reference.
        //mDbReference = mFirebaseDb.getReference().child("traveldeals");

        //create a childEvent listener to listen to changes in our firebase db.
       /* mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //method called when activity is first time loaded, all data-items in db will trigger event here.
                TextView tvDeals = (TextView) findViewById(R.id.tvDeals);
                TravelDeal tDeal = dataSnapshot.getValue(TravelDeal.class);

                //append new deals to existing text.
                tvDeals.setText(tvDeals.getText() + "\n" + tDeal.getTitle() );

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        */

        //add the childListener.
        //mDbReference.addChildEventListener(mChildListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //remove the firebaseAuthstate listener.
       FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUtil.openFbReference("traveldeals", this);
        RecyclerView recyclerDeals = (RecyclerView) findViewById(R.id.recyclerDeals);
        mAdapter = new DealAdapter(this);
        recyclerDeals.setAdapter(mAdapter);

        //create a layout manager.
        LinearLayoutManager dealsLayoutMgr = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerDeals.setLayoutManager(dealsLayoutMgr);

        FirebaseUtil.attachListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.insert_menu:
                Intent mIntent = new Intent(this, DealActivity.class);
                startActivity(mIntent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
