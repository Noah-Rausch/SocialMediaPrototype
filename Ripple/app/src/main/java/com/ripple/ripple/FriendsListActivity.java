package com.ripple.ripple;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.List;

// Activity that displays the user's friends list.

public class FriendsListActivity extends AppCompatActivity {
    FriendHolder myFriendsHolder = new FriendHolder();
    ArrayList<String> myFriends = new ArrayList<>();
    ListView friendsList;
    ArrayAdapter arrayAdapter;
    String myName;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Firebase firebaseRef = new Firebase("https://rippleapp.firebaseio.com");
        Button requestsBT = (Button) findViewById(R.id.requestsButtonXML);
        Button addFriendBT = (Button) findViewById(R.id.addFriendButtonXML);
        friendsList = (ListView) findViewById(R.id.friendsListViewXML);
        myName = getIntent().getStringExtra("username");

        FetchFromDBTask grabFriendsListFromDB = new FetchFromDBTask();
        grabFriendsListFromDB.execute(myName);

        // When the user clicks "add friend", we open up an activity where they can add a friend by name.

        addFriendBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsListActivity.this, AddFriendActivity.class);
                intent.putExtra("username", myName);
                startActivity(intent);
            }
        });

        // When the user clicks "requests", we open the activity where the pending requests are listed.

        requestsBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendsListActivity.this, RequestsActivity.class);
                intent.putExtra("username", myName);
                startActivity(intent);
            }
        });

        // When the user clicks on a friend's name, open an activity that loads the contents of their wall.

        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendsName = ((TextView) view).getText().toString();
                Intent intent = new Intent(FriendsListActivity.this, FriendsPageActivity.class);
                intent.putExtra("nameOfFriend", friendsName);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Class for loading data from the database in a separate thread.

    public class FetchFromDBTask extends AsyncTask<String, Void, ArrayList<String>> {
        ArrayList<String> myFriends = new ArrayList<>();
        ListView friendsList;
        Firebase firebaseRef = new Firebase("https://rippleapp.firebaseio.com");


        @Override
        protected ArrayList<String> doInBackground(final String... params) {
            Query queryRef = firebaseRef.child("users").orderByKey().equalTo(myName + "Friends");
            queryRef.addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    myFriendsHolder = dataSnapshot.getValue(FriendHolder.class);
                    myFriends = myFriendsHolder.getFriends();
                    friendsList = (ListView) findViewById(R.id.friendsListViewXML);
                    ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, myFriends);
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

            return myFriends;
        }
    }
}
