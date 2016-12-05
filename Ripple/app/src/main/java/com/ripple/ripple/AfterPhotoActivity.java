package com.ripple.ripple;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class AfterPhotoActivity extends AppCompatActivity {


    ArrayAdapter arrayAdapter;
    Bitmap bitmap;
    ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_photo);

        final Firebase firebaseRef = new Firebase("https://rippleapp.firebaseio.com");
        final String myName;
        final ListView listView = (ListView) findViewById(R.id.listViewAfterPhotoXML);
        final Button button = (Button) findViewById(R.id.sendBTXML);
        final EditText editText = (EditText) findViewById(R.id.captionEDXML);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxXML);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        // Grab the path of our photo, and create a bitmap from it, given its path.

        String imagePathString = getIntent().getStringExtra("imagePath");
        myName = getIntent().getStringExtra("myName");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        // We can afford a low resolution for landscape photos, as they will be shrunk.
        options.inSampleSize = 10;

        // Load the image from the file into this Bitmap variable.  This bitmap is lower quality
        // than the original, otherwise it would take up too much memory.

        bitmap = BitmapFactory.decodeFile(imagePathString, options);


        // If the bitmap is a low resolution, it doesn't need to be reduced as much.

        if(bitmap.getByteCount() < 240000){
            options.inSampleSize = 2;
            bitmap = BitmapFactory.decodeFile(imagePathString, options);
        }

        // Given what orientation the image is in, we adjust it accordingly, so it is portrait mode
        // every time.

        try {
            ExifInterface exif = new ExifInterface(imagePathString);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                // Change the resolution since this image will be stretched.
                options.inSampleSize = 6;
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                options.inSampleSize = 6;
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

        } catch (Exception e) {

        }

        // Delete the image from the source, we don't want this flooding the users gallery.

        File file = new File(android.os.Environment.getExternalStorageDirectory() + imagePathString);
        if (file.exists()) {

            file.delete();
        }

        Query queryRef = firebaseRef.child("users").orderByKey().equalTo(myName + "Friends");
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ArrayList<String> friendsToSend = new ArrayList<String>();
                FriendHolder tempFriendHolder = dataSnapshot.getValue(FriendHolder.class);
                friendsToSend = tempFriendHolder.getFriends();
                if(friendsToSend != null) {
                    listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_multiple_choice, friendsToSend);
                    listView.setAdapter(arrayAdapter);
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

        // When user clicks "send", round up all the names that were checked off, and loop through them, find each one in the database,
        // and add the current image to their ImageHolder.

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                final String captionStr = editText.getText().toString();
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                final ArrayList<String> selectedItems = new ArrayList<String>();;
                for (int i = 0; i < checked.size(); i++) {
                    int position = checked.keyAt(i);
                    if (checked.valueAt(i))
                        selectedItems.add(arrayAdapter.getItem(position).toString());
                }



                // For each person who was checked off, get their list of images, add the just taken picture and add it to that list.

                for(final String person : selectedItems) {

                    String encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.PNG, 100);
                    PicObject picObject = new PicObject();
                    int timeStamp = (int)System.currentTimeMillis();
                    picObject.setTimeStamp(timeStamp);
                    picObject.insertImage(encodedImage);

                    if (captionStr != null) {
                        if (checkBox.isChecked()) {
                            picObject.setCaption("Anonymous" + " > " + person + "\n \n " + captionStr);
                        } else {
                            picObject.setCaption("" + myName + " > " + person + "\n \n " + captionStr);
                        }
                    }

                    firebaseRef.child("users").child(person + "Images").child("images").push().setValue(picObject);
                }
                // Close this activity after the images are sent.
                finish();
            }
        });
    } // End of onCreate.

    // Helper methods to code/decode from bitmap to base64.
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
