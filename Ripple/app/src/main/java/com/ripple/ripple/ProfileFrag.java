package com.ripple.ripple;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;


public class ProfileFrag extends Fragment {
    static ArrayList<String> captionList = new ArrayList<>();
    GridView gridView;
    ArrayList<Bitmap> bitmaps = new ArrayList<>();
    ArrayList<PicObject> picObList = new ArrayList<>();
    Firebase firebaseRef;
    ImageAdapter imAdapter;
    String myName;
    boolean continueLoad = true;
    ProgressBar spinnerP;

    int totalNumOfPics;
    int picsLoadedSoFar = 0;


    public ProfileFrag() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseRef = new Firebase("https://rippleapp.firebaseio.com");
        myName = MainFragmentAc.myName;
    }




    private View myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_profile, container, false);
        gridView = (GridView) myFragmentView.findViewById(R.id.gridViewXMLL);
        spinnerP = (ProgressBar) myFragmentView.findViewById(R.id.progressBarProfile);
        imAdapter = new ImageAdapter(getActivity(), captionList, bitmaps);
        gridView.setAdapter(imAdapter);
        spinnerP.setVisibility(View.VISIBLE);



        // Grab all the images under this current user's name in the database,
        // and loop through them and add them each to an array of picObjects.

        Query queryAllImages = firebaseRef.child("users").child(myName + "Images").orderByKey();
        queryAllImages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                totalNumOfPics = (int)dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PicObject picOb = snapshot.getValue(PicObject.class);
                    picObList.add(0,picOb);
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
                            spinnerP.setVisibility(View.GONE);
                        }
                        else {
                            spinnerP.setVisibility(View.VISIBLE);
                            continueLoad = false;
                            loadTenPics(picsLoadedSoFar);
                        }
                    }
                }
            }
        });


        return myFragmentView;
    }



    // Load ten images from the list of picobjects, or less than ten if there isn't another ten.
    // This involved converting them to bitmaps and captions, and displaying them onscreen.

    public void loadTenPics(int startNum){
        for(int i = startNum; i < startNum + 10 && i < picObList.size(); i++){
            Bitmap bitmap = convertSingleBitmap(picObList.get(i));
            bitmaps.add(bitmap);
            picsLoadedSoFar++;
        }
        spinnerP.setVisibility(View.GONE);
        imAdapter.notifyDataSetChanged();
        continueLoad = true;
    }


    // Method to see if I can process and post to UI one bitmap at a time.

    public static Bitmap convertSingleBitmap (PicObject picOb){
        Bitmap bitmap = decodeBase64(picOb.getImage());
        captionList.add(picOb.getCaption());
        return bitmap;
    }

    // Helper method to decode the photos into bitmaps.

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
