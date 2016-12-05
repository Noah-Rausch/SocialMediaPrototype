package com.ripple.ripple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;

// Activity that displays to the user all the photos sent to the friend they clicked on.
// Will be just like the "myPage" activity.
public class FriendsPageActivity extends AppCompatActivity {
    static ArrayList<String> captionList = new ArrayList<>();
    ArrayList<PicObject> picObList = new ArrayList<>();
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ImageAdapter imApapter;
    GridView gridView;
    Firebase firebaseRef;
    String friendsName;
    int timesLoaded = 0;
    boolean continueLoad = true;
    ProgressBar spinnerF;
    Toolbar myToolbar;

    int totalNumOfPics;
    int picsLoadedSoFar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_page);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbarFriends);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        firebaseRef = new Firebase("https://rippleapp.firebaseio.com");
        gridView = (GridView) findViewById(R.id.gridViewFriendsXMLL);
        imApapter = new ImageAdapter(getApplicationContext(), captionList, bitmaps);
        gridView.setAdapter(imApapter);
        friendsName = getIntent().getStringExtra("nameOfFriend");
        getSupportActionBar().setTitle(friendsName);
        spinnerF = (ProgressBar) findViewById(R.id.progressFriendsPage);
        spinnerF.setVisibility(View.VISIBLE);

        // When the user clicks the back arrow, close the activity.

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captionList = new ArrayList<String>();
                finish();
            }
        });



        // Grab all the images under this current user's name in the database,
        // and loop through them and add them each to an array of picObjects.

        Query queryAllImages = firebaseRef.child("users").child(friendsName + "Images").orderByKey();
        queryAllImages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                totalNumOfPics = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PicObject picOb = snapshot.getValue(PicObject.class);
                    picObList.add(0, picOb);
                }
                picObList.remove(0);
                loadTenPics(picsLoadedSoFar);
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


        // Set the listener on the gridview, so we know to load another 10 images
        // when the user reaches the end of the list.  If the user scrolls to the very end,
        // meaning there are no more pictures to load, make the spinner go away.

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (continueLoad) {
                    if (i + i1 >= i2) {
                        if(picsLoadedSoFar == totalNumOfPics-1){
                            spinnerF.setVisibility(View.GONE);
                        }
                        else {
                            spinnerF.setVisibility(View.VISIBLE);
                            continueLoad = false;
                            loadTenPics(picsLoadedSoFar);
                        }
                    }
                }
            }
        });
    }


    // Load ten images from the list of picobjects, or less than ten if there isn't another ten.
    // This involved converting them to bitmaps and captions, and displaying them onscreen.

    public void loadTenPics(int startNum){
        for(int i = startNum; i < startNum + 10 && i < picObList.size(); i++){
            Bitmap bitmap = convertSingleBitmap(picObList.get(i));
            bitmaps.add(bitmap);
            picsLoadedSoFar++;
        }
        spinnerF.setVisibility(View.GONE);
        imApapter.notifyDataSetChanged();
        continueLoad = true;
    }



    // Helper Method to convert the String image codes to actual bitmap objects.

    public static ArrayList<Bitmap> convertToBitmaps(ArrayList<PicObject> arrayToConvert){
        ArrayList<PicObject>codedArray = arrayToConvert;
        ArrayList<Bitmap> resultArray = new ArrayList<>();

        // If the array that was passed actually exists, decode it.  This involves translating the string representation to a bitmap.

        if(codedArray != null) {
            for (int i = 0; i < codedArray.size(); i++) {
                Bitmap bitmap = decodeBase64(codedArray.get(i).getImage());
                captionList.add(codedArray.get(i).getCaption());
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

    // Method to see if I can process and post to UI one bitmap at a time.

    public static Bitmap convertSingleBitmap (PicObject picOb){
        Bitmap bitmap = decodeBase64(picOb.getImage());
        captionList.add(picOb.getCaption());
        return bitmap;
    }

    @Override
    public void onDestroy(){
        firebaseRef = null;
        super.onDestroy();
    }
}
