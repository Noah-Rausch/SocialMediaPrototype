package com.ripple.ripple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;


import java.lang.reflect.Array;
import java.util.ArrayList;

public class FeedFrag extends Fragment {

    // Object that deals with refreshing the page.
    private SwipeRefreshLayout mSwipeRefreshLayout;
    Firebase firebaseRef;
    ArrayList<String> friendsNames = new ArrayList<>();
    static ArrayList<String> captionListNews = new ArrayList<>();
    static ArrayList<PicObject> picObList = new ArrayList<>();
    static ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
    Query queryRef;
    String myName;
    GridView gridView;
    ImageAdapter imageAdapter;
    ProgressBar spinner;
    boolean continueLoad = true;

    int totalNumOfPics;
    int picsLoadedSoFar = 0;


    public FeedFrag() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseRef = new Firebase("https://rippleapp.firebaseio.com");
        myName = MainFragmentAc.myName;



        // Find the friends of the current user through query.  Then, loop through those friends and
        // load their images into an array.

        queryRef = firebaseRef.child("users").orderByKey().equalTo(myName + "Friends");
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                FriendHolder myFriends = dataSnapshot.getValue(FriendHolder.class);
                friendsNames = myFriends.getFriends();
                refresh();
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



    // Hold a reference to the fragment view, so I can reference the UI elements in the fragment, and post to the UI.
    // Here I also create the empty gridview adapter, and add more as they load.

    private View myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myFragmentView = inflater.inflate(R.layout.fragment_feed, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) myFragmentView.findViewById(R.id.swipe_container);
        gridView = (GridView) myFragmentView.findViewById(R.id.gridViewFragXMLL);
        spinner = (ProgressBar) myFragmentView.findViewById(R.id.progressBarFeed);
        continueLoad = true;
        imageAdapter = new ImageAdapter(getActivity(), captionListNews, bitmaps);
        gridView.setAdapter(imageAdapter);
        spinner.setVisibility(View.VISIBLE);



        // Set up the listener on the refreshlayout.

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refresh();

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
                            spinner.setVisibility(View.GONE);
                        }
                        else {
                            spinner.setVisibility(View.VISIBLE);
                            continueLoad = false;
                            loadTenPics(picsLoadedSoFar);
                        }
                    }
                }
            }
        });

        return myFragmentView;
    }


    // Method to see if I can process and post to UI one bitmap at a time.

    public static Bitmap convertSingleBitmap (PicObject picOb){
        Bitmap bitmap = decodeBase64(picOb.getImage());
        captionListNews.add(picOb.getCaption());
        return bitmap;
    }


    // Helper method to decode the photos into bitmaps.

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }



    // Load ten images from the list of picobjects, or less than ten if there isn't another ten.
    // This involved converting them to bitmaps and captions, and displaying them onscreen.

    public void loadTenPics(int startNum){
        for(int i = startNum; i < startNum + 5 && i < picObList.size(); i++){
            Bitmap bitmap = convertSingleBitmap(picObList.get(i));
            bitmaps.add(bitmap);
            picsLoadedSoFar++;
        }
        spinner.setVisibility(View.GONE);
        imageAdapter.notifyDataSetChanged();
        continueLoad = true;
    }



    // Method to "refresh" the screen.  Redoes the query, and loads the results on screen.

    public void refresh(){

        // Grab all the images under this current user's name in the database,
        // and loop through them and add them each to an array of picObjects.

        for(String friend : friendsNames) {
            Query queryAllImages = firebaseRef.child("users").child(friend + "Images").orderByKey();
            queryAllImages.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    totalNumOfPics += (int) dataSnapshot.getChildrenCount();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PicObject picOb = snapshot.getValue(PicObject.class);
                        picObList.add(0,picOb);
                    }
                    picObList.remove(0);
                    loadTenPics(picsLoadedSoFar);
                    mSwipeRefreshLayout.setRefreshing(false);
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
