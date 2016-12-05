package com.ripple.ripple;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;


// Activity that takes an email and password, and attempts to log the user in.  This will only need to happen
// When they first create an account, and after they log out, otherwise this activity should be skipped.
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(getApplicationContext());

        final EditText emailED = (EditText) findViewById(R.id.emailEDXML);
        final EditText passwordED = (EditText) findViewById(R.id.passwordEDXML);
        Button loginBT = (Button) findViewById(R.id.loginBTXML);
        String username;
        final Firebase firebaseRef = new Firebase("https://rippleapp.firebaseio.com");

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {

                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Intent changeToMyBoard = new Intent(LoginActivity.this, MainFragmentAc.class);
                        startActivity(changeToMyBoard);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_LONG).show();
                    }
                };

                if(emailED.getText() == null || passwordED.getText() == null) {

                    // Do not assign the variables if any one of them is null.
                }

                else {
                    final String passwordLoginStr = passwordED.getText().toString();
                    final String emailLoginStr = emailED.getText().toString();
                    firebaseRef.authWithPassword(emailLoginStr, passwordLoginStr, authResultHandler);
                }
            } // End of onCLick.
        });
    }

    @Override
    protected void onPause(){
        super.onPause();

    }
}
