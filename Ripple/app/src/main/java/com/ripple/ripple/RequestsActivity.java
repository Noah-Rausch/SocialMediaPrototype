package com.ripple.ripple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;


// Activity that displays to the user people who have sent them a friend request.

public class RequestsActivity extends AppCompatActivity {
    FriendHolder myFriendHolder = new FriendHolder();
    String myName;
    ArrayList<String> myRequestsArray = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;
    String friendName;
    Toolbar myToolbar;
    ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        final Firebase firebaseRef = new Firebase("https://rippleapp.firebaseio.com");
        listView = (ListView) findViewById(R.id.listViewOfRequestsXML);
        myName = getIntent().getStringExtra("username");
        arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, myRequestsArray);
        listView.setAdapter(arrayAdapter);

        // When the user clicks the back arrow, close the activity.

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Query queryRef = firebaseRef.child("users").orderByKey().equalTo(myName + "Friends");
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                myFriendHolder = dataSnapshot.getValue(FriendHolder.class);
                myRequestsArray = myFriendHolder.getFriendRequests();
                arrayAdapter.notifyDataSetChanged();
                arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, myRequestsArray);
                listView.setAdapter(arrayAdapter);
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
        }); // End of addChildEventListener.

        // When the user clicks on a name in their requests, add eachother to friends list.

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friendName = ((TextView)view).getText().toString();
                Query queryRef = firebaseRef.child("users").orderByKey().equalTo(friendName + "Friends");
                queryRef.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        FriendHolder friendHolder = dataSnapshot.getValue(FriendHolder.class);
                        myFriendHolder.removeRequest(friendName);
                        myFriendHolder.addFriend(friendName);
                        friendHolder.addFriend(myName);
                        firebaseRef.child("users").child(friendName + "Friends").setValue(friendHolder);
                        firebaseRef.child("users").child(myName + "Friends").setValue(myFriendHolder);
                        arrayAdapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), "Friend Added", Toast.LENGTH_LONG).show();
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
            } // End of onItemClick.
        });

        /* backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        */
    }
}
