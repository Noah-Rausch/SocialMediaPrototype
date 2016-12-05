package com.ripple.ripple;

import android.graphics.Bitmap;

// Object to represent a picture, along with the likes/comments.  consists of a bitmap and int value,
// which keeps track of likes.

public class PicObject {

    private String image;
    private String caption;
    private int timeStamp;

    public PicObject(String imageStr, String cap, int ts){

        this.image = imageStr;
        this.caption = cap;
        this.timeStamp = ts;
    }
    // Empty constructor.

    public PicObject(){

    }

    public void insertImage(String image){
        this.image = image;
    }

    public void setCaption(String message){
        this.caption = message;
    }

    public String getImage() {
        return image;
    }

    public String getCaption() {

        return caption;
    }

    public void setTimeStamp(int ts){
        this.timeStamp = ts;
    }

    public int getTimeStamp(){
        return timeStamp;
    }


    // Allow sorting of picObjects by their timestamps.


}
