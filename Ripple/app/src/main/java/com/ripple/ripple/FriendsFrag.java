package com.ripple.ripple;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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


public class FriendsFrag extends Fragment {
    FriendHolder myFriendsHolder = new FriendHolder();
    ListView friendsList;
    String myName;
    Firebase firebaseRef;
    ArrayList<String> myFriends = new ArrayList<>();
    ArrayAdapter arrayAdapter;


    public FriendsFrag() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseRef = new Firebase("https://rippleapp.firebaseio.com");
        myName = MainFragmentAc.myName;

        // Query Firebase for the current user's friendslist.  Once found, we will update the array adapter.

        Query queryRef = firebaseRef.child("users").orderByKey().equalTo(myName + "Friends");
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                myFriendsHolder = dataSnapshot.getValue(FriendHolder.class);
                myFriends = myFriendsHolder.getFriends();
                arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, myFriends);
                friendsList.setAdapter(arrayAdapter);
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
    // Here I also create the empty arraylist and adapter, and when the friendslist loads, update the adapter.

    private View myFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // When the view is created, get a reference to the view inflater so we can reference the individual UI elements in our code.

        myFragmentView = inflater.inflate(R.layout.fragment_friends, container, false);
        friendsList = (ListView) myFragmentView.findViewById(R.id.friendsListFragXml);
        Button addFriendBut = (Button) myFragmentView.findViewById(R.id.addFrButXml);
        Button requestsBut = (Button) myFragmentView.findViewById(R.id.requestsButXml);
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, myFriends);
        friendsList.setAdapter(arrayAdapter);



        // Set up listener so when the user clicks on a name in the list, we start an activity to display that person's page.

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String friendsName = ((TextView) view).getText().toString();
                Intent intent = new Intent(getActivity(), FriendsPageActivity.class);
                intent.putExtra("nameOfFriend", friendsName);
                friendsName = "";
                startActivity(intent);
            }
        });


        // When the user clicks "add friend", start the activity where that users can add  a friend.

         addFriendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                intent.putExtra("username", myName);
                startActivity(intent);
            }
        });


        // When the user clicks requests, start the activity that displays all their friend requests.

        requestsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RequestsActivity.class);
                intent.putExtra("username", myName);
                startActivity(intent);
            }
        });


        return myFragmentView;
    }
}
