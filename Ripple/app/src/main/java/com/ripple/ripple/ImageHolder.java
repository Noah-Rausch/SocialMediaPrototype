package com.ripple.ripple;

import java.util.ArrayList;

public class ImageHolder {
    private ArrayList<PicObject> images = new ArrayList<>();

    public ImageHolder(){
        this.images = new ArrayList<>();
    }

    public void addPicOb(PicObject pO){
        this.images.add(0, pO);
    }

    // Remove the first image, which will be a test image.

    public void removePicOb(){
        images.remove(images.size()-1);
    }

    public ArrayList<PicObject> getImages() {
        return images;
    }
}
