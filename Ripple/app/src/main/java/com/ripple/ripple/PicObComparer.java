package com.ripple.ripple;


// A class that given a list of picobjects can sort them based on when they were taken (timestamp).

import java.util.ArrayList;

public class PicObComparer {
    ArrayList<PicObject> unsortedList = new ArrayList<>();

    public PicObComparer(ArrayList<PicObject> list){
        this.unsortedList = list;
    }

    public void sortByRecent(){
        for(PicObject picOb1 : this.unsortedList){
            for(PicObject picOb2 : this.unsortedList){
                if(picOb1.getTimeStamp() < picOb2.getTimeStamp()){

                }
            }
        }
    }
}
