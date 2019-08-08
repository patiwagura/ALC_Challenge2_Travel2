package com.pato.travelmantics;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    //this class creates an instance of firebase database and gets a database reference

    public static FirebaseDatabase firebaseDb;
    public static DatabaseReference dbReference;
    private static FirebaseUtil firebaseUtil;

    //constants.
    private static final int RC_SIGN_IN = 123;
    public static boolean isAdmin;
    private static final String FIREDB_ADMIN_CHILD_REF = "administrators";
    public static final String FIRESTORE_DEALS_PIC_REF = "deals_pictures";

    private static ListActivity callerActivity;

    //reference fields to firebase storage.
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;

    //Firebase authentication-ui
    public static FirebaseAuth mFirebaseAuth;
    //auth state listener listens for login / logout or any change in auth state.
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<TravelDeal> mDeals;

    //private constructor, prevent creating an instance of this class from outside this class.
    private FirebaseUtil() {
    }

    //this method opens a firebase_DB_reference of the child passed as a parameter.
    public static void openFbReference(String childRef, final ListActivity callerUi) {
        if (firebaseUtil == null) {
            //instance of FirebaseUtil.
            firebaseUtil = new FirebaseUtil();

            //FirebaseDatabase instance.
            firebaseDb = FirebaseDatabase.getInstance();

            //instance of FirebaseAuth
            mFirebaseAuth = FirebaseAuth.getInstance();

            //get a reference to the Activity where this function was called from.
            callerActivity = callerUi;

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    //check if user is logged in or not
                    if (firebaseAuth.getCurrentUser() == null) {
                        //no user is logged in, call sign.
                        FirebaseUtil.signIn();

                        //Toast.makeText(callerActivity.getBaseContext(), "Welcome Back [ "+ firebaseAuth.getCurrentUser().getDisplayName() +" ]", Toast.LENGTH_LONG).show();
                    } else{
                        //get userid sent from firebase data-object.
                        String userId = firebaseAuth.getUid();
                        //check if the user is admin.
                        checkAdmin(userId);

                    }

                    Toast.makeText(callerActivity.getBaseContext(), "Welcome Back", Toast.LENGTH_LONG).show();

                }
            };

            //Note sure this should be here.
            //mFirebaseAuth.addAuthStateListener(mAuthListener);

            connectStorage();
        }

        //refresh arrayList every time this class is called.
        mDeals = new ArrayList<TravelDeal>();
        dbReference = firebaseDb.getReference().child(childRef);

    }


    //method to attach the authState Listener.
    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    //method to remove the authStateListener.
    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    //method to init FireBase Storage.
    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child(FIRESTORE_DEALS_PIC_REF);
    }

    //method to sign user to firebase
    private static void signIn() {
        //Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()


                /**
                 * new AuthUI.IdpConfig.PhoneBuilder().build()
                 new AuthUI.IdpConfig.GoogleBuilder().build(),
                 new AuthUI.IdpConfig.FacebookBuilder().build(),
                 new AuthUI.IdpConfig.TwitterBuilder().build() */);

        //Create and launch sign-in intent
        callerActivity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    //method to check if user is admin.
    private static void checkAdmin(String userId) {
        FirebaseUtil.isAdmin = false;
        //Create a reference to firebasedatabase for Administrators node whose child node matches the userId.
        DatabaseReference mdbRef = firebaseDb.getReference().child(FIREDB_ADMIN_CHILD_REF).child(userId);
        ChildEventListener childListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //this method is only called when child is an administrator matching userId.
                FirebaseUtil.isAdmin = true;
                callerActivity.showMyMenus();

                Log.d("Admin", "You are an administrator.");

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

        //add childlistener to the database.
        mdbRef.addChildEventListener(childListener);
    }


}
