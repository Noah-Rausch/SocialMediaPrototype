package com.ripple.ripple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.OutputStreamWriter;
import java.util.Map;


// Activity that deals with the functionality of creating a new account for a user.
public class CreateAccountActivity extends AppCompatActivity {

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Firebase.setAndroidContext(getApplicationContext());
        final Firebase firebaseRef = new Firebase("https://rippleapp.firebaseio.com");

        final EditText usernameET = (EditText) findViewById(R.id.usernameEDXML);
        final EditText emailET = (EditText) findViewById(R.id.emailEDXML);
        final EditText passwordET = (EditText) findViewById(R.id.passwordEDXML);
        Button createAccBT = (Button) findViewById(R.id.createAccBTXML);
        loginButton = (Button) findViewById(R.id.loginBut);


        // When user clicks "create account", use data in textfields to create account.

        createAccBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usernameET.getText().toString() == null) {
                    return;
                }
                else if(emailET.getText().toString() == null) {
                    return;
                }
                else if(passwordET.getText().toString() == null) {
                    return;
                }
                // If none of the inputs are null, continue with the program.

                final String usernameSTR = usernameET.getText().toString();
                final String emailSTR = emailET.getText().toString();
                final String passwordSTR = passwordET.getText().toString();

                Query queryRef = firebaseRef.child("users").orderByKey().equalTo(usernameSTR);
                queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            Toast.makeText(getApplicationContext(), "Username Exists, Try Another Name.", Toast.LENGTH_LONG).show();
                        } else {

                            // Use info in the string variables to
                            firebaseRef.createUser(emailSTR, passwordSTR, new Firebase.ValueResultHandler<Map<String, Object>>() {

                                @Override
                                public void onSuccess(Map<String, Object> stringObjectMap) {

                                    // If the account was created, toast to let me know.
                                    // If the user succesfully created an account, They can move to login screen.

                                    Toast successToast = Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_LONG);
                                    successToast.show();

                                    Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {

                                        public void onAuthenticated(AuthData authData) {

                                            AccountInfo accountInfo = new AccountInfo(usernameSTR, emailSTR, passwordSTR, authData.getUid());
                                            FriendHolder friendHolder = new FriendHolder();
                                            friendHolder.addFriend("Noah");
                                            ImageHolder imageHolder = new ImageHolder();
                                            PicObject pic = new PicObject("", "First Image", 0);
                                            imageHolder.addPicOb(pic);

                                            firebaseRef.child("users").child(usernameSTR).setValue(accountInfo);
                                            firebaseRef.child("users").child(usernameSTR + "Friends").setValue(friendHolder);
                                            firebaseRef.child("users").child(usernameSTR + "Images").child("images").child("keyTestString").setValue(pic);

                                            // Write the user's username to a file, so we can keep using it in the future.

                                            try {
                                                OutputStreamWriter out = new OutputStreamWriter(openFileOutput("myUsername.txt",0));
                                                out.write(usernameSTR);
                                                out.close();
                                            }

                                            catch (java.io.IOException e) {

                                            }


                                            Intent toNextActivity = new Intent(CreateAccountActivity.this, MainFragmentAc.class);
                                            startActivity(toNextActivity);
                                        }

                                        public void onAuthenticationError(FirebaseError firebaseError) {

                                            // If there is an error authenticating...
                                        }
                                    };

                                    // Log in the user.

                                    firebaseRef.authWithPassword(emailSTR, passwordSTR, authResultHandler);
                                }

                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    Toast failToast = Toast.makeText(getApplicationContext(), firebaseError.toString(), Toast.LENGTH_LONG);
                                    failToast.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });



        // When the user clicks login, it is assumed they already have an account, and simply want to login.

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {

                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Intent changeToMyBoard = new Intent(CreateAccountActivity.this, MainFragmentAc.class);
                        startActivity(changeToMyBoard);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_LONG).show();
                    }
                };

                if (emailET.getText() == null || passwordET.getText() == null) {

                    // Do not assign the variables if any one of them is null.
                } else {
                    final String passwordLoginStr = passwordET.getText().toString();
                    final String emailLoginStr = emailET.getText().toString();
                    firebaseRef.authWithPassword(emailLoginStr, passwordLoginStr, authResultHandler);
                }
            }
        });
    }
}
