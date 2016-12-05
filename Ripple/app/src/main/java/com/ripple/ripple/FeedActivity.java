package com.ripple.ripple;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Activity that will show the user all the images that were send to the user's friends.
// I will get all the images in all this user's friend's accounts.

public class FeedActivity extends AppCompatActivity {
    Firebase firebaseRef;
    GridView gridView;
    ArrayList<String> friendsNames = new ArrayList<>();
    static ArrayList<String> captionListNews = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    ImageAdapter imAdapter;
    Query queryRef;
    Query queryPic;
    static String mCurrentPhotoPath;
    String myName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Button homeBT = (Button)findViewById(R.id.newsBTXML);
        Button friendsBT = (Button)findViewById(R.id.friendsBTXML);
        Button takePicBT = (Button)findViewById(R.id.takePicBTXML);
        gridView = (GridView) findViewById(R.id.gridViewNewsXMLL);
        imAdapter = new ImageAdapter(getApplicationContext(), captionListNews, bitmaps);
        gridView.setAdapter(imAdapter);
        firebaseRef = new Firebase("https://rippleapp.firebaseio.com");


        // Grab the current user's friends list.  Once we have the friends list, loop through it, adding each friend's images to a single
        // list.  We will then display that total list to the UI.  This allows the user to view all the images that were sent to their friends.
        // This is done by querying the current user's friends list, and once the result is returned, query the each friend on the list.

        queryRef = firebaseRef.child("users").orderByKey().equalTo(myName + "Friends");
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendHolder myFriends = dataSnapshot.getValue(FriendHolder.class);
                friendsNames = myFriends.getFriends();

                for (String friend : friendsNames) {
                    for(int i = 0; i < 40; i++){
                        queryPic = firebaseRef.child("users").child(friend + "Images").child("images").orderByKey().equalTo(Integer.toString(i));
                        queryPic.addChildEventListener(new ChildEventListener() {

                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                PicObject picOb = dataSnapshot.getValue(PicObject.class);
                                bitmaps.add(convertSingleBitmap(picOb));
                                imAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        // When the "take pic" button is pressed, we open the camera app, take a photo, and store this photo in a file.

        takePicBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takePhotoMethod();
            }
        });


        // When the "friends" button is clicked, start the activity that shows the user's friends list.

        friendsBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendsListActIntent = new Intent(FeedActivity.this, FriendsListActivity.class);
                friendsListActIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(0, 0);
                friendsListActIntent.putExtra("username", myName);
                startActivity(friendsListActIntent);
                overridePendingTransition(0, 0);

            }
        });


        // When the "news" button is clicked, we go to the newsfeed page.

        homeBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FeedActivity.this, MainFragmentAc.class);
                startActivity(intent);


                /* Intent intent = new Intent(FeedActivity.this, myPageActivity.class);
                intent.putExtra("username", myName);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                */
            }
        });


        // When the user clicks "upload", go to the gallery and allow them to upload an image.
        /*

        uploadBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),2);
            }
        });
        */
    }

    // Helper Method to convert the String image codes to actual bitmap objects.

    public static ArrayList<Bitmap> convertToBitmaps(ArrayList<PicObject> arrayToConvert){

        // Loop through the encoded photos, and convert each one to a bitmap,
        // and place in a bitmap array of photos.

        ArrayList<PicObject>codedArray = arrayToConvert;
        ArrayList<Bitmap> resultArray = new ArrayList<>();

        if(codedArray != null) {

            for (int i = 0; i < codedArray.size(); i++) {
                Bitmap bitmap = decodeBase64(codedArray.get(i).getImage());
                captionListNews.add(codedArray.get(i).getCaption());
                resultArray.add(bitmap);
            }
        }
        return resultArray;
    }

    // Helper method to decode the photos into bitmaps.

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    // Helper method to create image path file.

    private static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // When the image is taken, let the user select which friends to send it to.

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Intent chooseRecipientsIntent = new Intent(FeedActivity.this, AfterPhotoActivity.class);
            chooseRecipientsIntent.putExtra("imagePath", mCurrentPhotoPath);
            chooseRecipientsIntent.putExtra("myName", myName);
            startActivity(chooseRecipientsIntent);
        }

        //  If the image was uploaded from gallery, the image is sent to friends in the same way.
        // *** NOT WORKING.  WILL BE ADDED LATER ***

        /*

        else if(requestCode == 2 && resultCode == RESULT_OK){

            try {
                Uri uploadUri = data.getData();
                uploadPath = uploadUri.toString();
                Intent chooseRecipientsIntent = new Intent(FeedActivity.this, AfterPhotoActivity.class);
                chooseRecipientsIntent.putExtra("imagePath", uploadPath);
                chooseRecipientsIntent.putExtra("myName", myName);
                startActivity(chooseRecipientsIntent);
            }

            catch(Exception e){

            }
        }
        */

        else{

            Toast.makeText(getApplicationContext(), "Try again", Toast.LENGTH_LONG).show();
        }
    }

    // Method to open the camera app so the user can take a photo.

    private void takePhotoMethod() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

                // Error occurred while creating the File
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }


    // Method to see if I can process and post to UI one bitmap at a time.

    public static Bitmap convertSingleBitmap (PicObject picOb){
        Bitmap bitmap = decodeBase64(picOb.getImage());
        captionListNews.add(picOb.getCaption());
        return bitmap;
    }
}
