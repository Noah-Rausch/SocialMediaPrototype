package com.ripple.ripple;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;

public class AddFriendActivity extends AppCompatActivity {

    AccountInfo friendsAccInfo = new AccountInfo();
    String myName;
    Button requestFriendBT;
    EditText nameToAddET;
    String nameInputted;
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbarAddFriend);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Firebase firebaseRef = new Firebase("https://rippleapp.firebaseio.com");
        nameToAddET = (EditText) findViewById(R.id.nameToAddEDXML);
        requestFriendBT = (Button) findViewById(R.id.requestFriendBTXML);
        myName = getIntent().getStringExtra("username");


        // When the user clicks the back arrow, close the activity.

        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // When the user clicks add, take the inputed name, search the database, and let them know if they exist or not.

        requestFriendBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameInputted = nameToAddET.getText().toString();
                Query queryRef = firebaseRef.child("users").orderByKey().equalTo(nameInputted);
                queryRef.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        friendsAccInfo = dataSnapshot.getValue(AccountInfo.class);
                        Toast.makeText(getApplicationContext(), "Friend Request Sent", Toast.LENGTH_LONG).show();
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


                // Do the same query, except add the current user's name to their new friend's friend list.  If the current user already send
                // This person a request, don't add multiples.

                Query queryAddToFriends = firebaseRef.child("users").orderByKey().equalTo(nameInputted + "Friends");
                queryAddToFriends.addChildEventListener(new ChildEventListener() {

                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        FriendHolder friendsFriendHolder = dataSnapshot.getValue(FriendHolder.class);
                        if(!friendsFriendHolder.getFriendRequests().contains(myName)) {
                            friendsFriendHolder.addRequest(myName);
                            firebaseRef.child("users").child(nameInputted + "Friends").setValue(friendsFriendHolder);
                        }

                        else{
                            Toast.makeText(getApplicationContext(), "Already added friend", Toast.LENGTH_LONG).show();
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
            } // End of onClick.
        });
    }


    public class WriteToFile extends AsyncTask<ArrayList<String>, Void, Void> {

        @Override
        protected Void doInBackground(ArrayList<String>... params) {

            return null;
        }
    }
}
