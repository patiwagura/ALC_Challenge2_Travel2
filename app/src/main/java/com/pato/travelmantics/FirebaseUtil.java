package com.pato.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class FirebaseUtil {

    // avoid code repetition.
    //this class creates an instance of firebase database and gets a databasereference

    public static FirebaseDatabase firebaseDb;
    public static DatabaseReference dbReference;
    private static FirebaseUtil firebaseUtil;

    //constants.
    private static final int RC_SIGN_IN = 123;
    public static final String STORAGE_DEAL_PICS = "deals_pictures";

    private static Activity callerActivity;

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
    public static void openFbReference(String childRef, AppCompatActivity callerUi) {
        if (firebaseUtil == null) {
            //a reference to the Activity where this function was called from.
            callerActivity = callerUi;

            //instance of FirebaseUtil.
            firebaseUtil = new FirebaseUtil();
            firebaseDb = FirebaseDatabase.getInstance();

            //instance of FirebaseAuth
            mFirebaseAuth = FirebaseAuth.getInstance();
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

    //method to attach the authstate listener.
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
        mStorageRef = mStorage.getReference().child(STORAGE_DEAL_PICS);
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


}
