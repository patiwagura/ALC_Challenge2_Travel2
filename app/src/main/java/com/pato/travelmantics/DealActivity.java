package com.pato.travelmantics;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDb;
    private DatabaseReference mDbReference;
    private EditText txtTitle, txtPrice, txtDesc;
    TravelDeal deal;
    private Button btnUploadImg;
    private ImageView imgViewDeal;
    //constant
    private static final int PICTURE_RESULT = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        //Using FirebaseUtil class get a reference to FirebaseDatabase and DatabaseReference
        //FirebaseUtil.openFbReference("traveldeals", new ListActivity());
        mFirebaseDb = FirebaseUtil.firebaseDb;
        mDbReference = FirebaseUtil.dbReference;

        //get reference to ui-widgets declared in layout_xml
        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtPrice = (EditText) findViewById(R.id.txtPrice);
        txtDesc = (EditText) findViewById(R.id.txtDescription);
        //uploding and showing images using picasso library.
        imgViewDeal = (ImageView) findViewById(R.id.imgDealPic);

        //get intent passed when starting Activity.
        Intent sIntent = getIntent();
        TravelDeal myDeal = (TravelDeal) sIntent.getSerializableExtra("Deal");
        if (myDeal == null) {
            myDeal = new TravelDeal();
        }
        this.deal = myDeal;

        //set text of the ui to match selected deal.
        txtTitle.setText(deal.getTitle());
        txtDesc.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());

        //button to upload image.
        btnUploadImg = findViewById(R.id.btnImage);
        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent upPicIntent = new Intent(Intent.ACTION_GET_CONTENT);
                upPicIntent.setType("image/jpeg");
                upPicIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(upPicIntent, "Insert Picture"), PICTURE_RESULT);

                //show image selected by user.
                //showImage(deal.getImageUrl());
                Log.d("BTN-UPLOAD-IMG","Button upload image clicked. Function End.");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        if(FirebaseUtil.isAdmin){
            menu.findItem(R.id.save_menu).setVisible(true);
            menu.findItem(R.id.delete_menu).setVisible(true);
            enableEditTexts(true);
        }else{
            //user not admin.
            menu.findItem(R.id.save_menu).setVisible(false);
            menu.findItem(R.id.delete_menu).setVisible(false);
            enableEditTexts(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                cleanTxt(); //Reset the EditText.
                backToList();
                return true;

            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * method to save a Deal.
     */
    private void saveDeal() {
        //String title = txtTitle.getText().toString();
        //String desc = txtDesc.getText().toString();
        //String price = txtPrice.getText().toString();

        //instance of TravelDeal to hold data.
        //TravelDeal deal = new TravelDeal(title, desc, price, "");

        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDesc.getText().toString());
        deal.setPrice(txtPrice.getText().toString());

        //check if deal exists or its a new one.
        if (deal.getId() == null) {
            //save the new deal.
            mDbReference.push().setValue(deal);
        } else {
            //deal exists. retrieve deal specified by id.
            mDbReference.child(deal.getId()).setValue(deal);
        }


        Log.d("SAVE_DEAL", " TITLE " + txtTitle.getText().toString() + ":  Price " + txtPrice.getText().toString() + " DESC : " + txtDesc.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            //To upload an image to firebaseStorage you need 3 steps.
            //1. obtain uri of file to upload.
            //2. get firebase storage reference of node to store the file.
            //3. use putFile() method with returns an async task.
            Uri imageUri = data.getData();  //get uri from intent received.
            StorageReference storeRef = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            //upload image to firebase-storage. this method returns asynchronous task e.g thread.
            //add a listener here to get notified of upload success or failure
            storeRef.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // on successful upload we get the url of the image that was uploaded.
                    String imgUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                    deal.setImageUrl(imgUrl);
                    showImage(imgUrl);

                    Log.d("UPLOAD_ONSUCCESS", "Image url : onSuccess UPLOAD TASK : " + imgUrl);
                }
            });

            Log.d("UPLOAD_TASK_RESULT", "uploadTask FILE UPLOADED : LAST_PATH_SEGMENT : " + imageUri.getLastPathSegment() );

        }
    }

    //method to delete existing deal.
    private void deleteDeal() {
        //check if deal exists if not prompt user to save a deal first.
        if (deal == null) {
            Toast.makeText(this, "Please save a deal before deleting", Toast.LENGTH_LONG).show();
            return;
        }
        //get current deal and remove it.
        mDbReference.child(deal.getId()).removeValue();

    }

    //goback to listActivity after saving deal.
    private void backToList() {
        Intent mIntent = new Intent(this, ListActivity.class);
        startActivity(mIntent);
    }

    //method to load image to imageView.
    private void showImage(String url) {
        //url should not be empty and null
        if (url != null && url.isEmpty() == false) {
            //get width of phone screen.
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, (width * 2/3))
                    .centerCrop()
                    .into(imgViewDeal)
            ;

            Log.d("SHOW_IMG", "URL :[ " +url +" ]  imgViewDeal- ID : " + imgViewDeal.getId());
        }
    }

    //method to clear the EditText fields
    private void cleanTxt() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDesc.setText("");
        //set focus to Title_textField.
        txtTitle.requestFocus();

    }

    //this method disables and enables the EditTexts.
    private void enableEditTexts(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDesc.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }
}
