package com.pato.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


    }

    @Override
    protected void onPause() {
        super.onPause();
        //remove the firebase AuthState Listener.
       FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUtil.openFbReference("traveldeals", this);
        RecyclerView recyclerDeals = (RecyclerView) findViewById(R.id.recyclerDeals);
        final DealAdapter mAdapter = new DealAdapter(this);
        recyclerDeals.setAdapter(mAdapter);

        //create a layout manager.
        LinearLayoutManager dealsLayoutMgr = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerDeals.setLayoutManager(dealsLayoutMgr);

        FirebaseUtil.attachListener();
    }

    //this method redraws the menus again
    public void showMyMenus(){
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);

        MenuItem insertMenu = menu.findItem(R.id.insert_menu);
        if(FirebaseUtil.isAdmin == true){
            //enable the menus if user is admin.
            insertMenu.setVisible(true);
        }else{
            //disable the menus.
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.insert_menu:
                Intent mIntent = new Intent(this, DealActivity.class);
                startActivity(mIntent);
                return true;

            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                Log.d("LOGOUT", "User logged out.");

                                //goback to login screen if user is not logged in.
                                FirebaseUtil.attachListener();
                            }
                        });

                FirebaseUtil.detachListener();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
