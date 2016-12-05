package com.ripple.ripple;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// Activity that represents the current user's board.  When this page is navigated to, it will be
// populated with images that are stored in their Account object in firebase.

public class myPageActivity extends AppCompatActivity {
    static ArrayList<String> captionList = new ArrayList<>();
    GridView gridView;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    Firebase firebaseRef;
    Query queryPic;
    ImageAdapter imAdapter;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
        gridView = (GridView)findViewById(R.id.gridViewXMLL);
        String myName = getIntent().getStringExtra("username");
        backButton = (Button) findViewById(R.id.bbMyPageXml);
        imAdapter = new ImageAdapter(getApplicationContext(), captionList, bitmaps);
        gridView.setAdapter(imAdapter);
        firebaseRef = new Firebase("https://rippleapp.firebaseio.com");

        for(int i = 0; i < 40; i++) {
            queryPic = firebaseRef.child("users").child(myName + "Images").child("images").orderByKey().equalTo(Integer.toString(i));
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Helper method to convert an array PicObjects, which each contain a image in String form,
    // to an array of bitmap photos.

    public static ArrayList<Bitmap> convertToBitmaps(ArrayList<PicObject> arrayToConvert){
        ArrayList<PicObject>codedArray = arrayToConvert;
        ArrayList<Bitmap> resultArray = new ArrayList<>();

        if(codedArray != null) {
            for (int i = 0; i < codedArray.size(); i++) {
                Bitmap bitmap = decodeBase64(codedArray.get(i).getImage());
                captionList.add(codedArray.get(i).getCaption());
                resultArray.add(bitmap);
            }
        }

        return resultArray;
    }

    // Method to see if I can process and post to UI one bitmap at a time.

    public static Bitmap convertSingleBitmap (PicObject picOb){
        Bitmap bitmap = decodeBase64(picOb.getImage());
        captionList.add(picOb.getCaption());
        return bitmap;
    }


    // Helper method to decode the photos into bitmaps.

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
