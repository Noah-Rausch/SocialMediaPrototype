package com.ripple.ripple;

import android.graphics.Bitmap;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<Bitmap> bitmapList;
    private ArrayList<String> captionList;
    private static LayoutInflater inflater = null;


    // Constructor
    public ImageAdapter(Context c, ArrayList<String> captions, ArrayList<Bitmap> bitmapArrayList) {
        this.mContext = c;
        this.bitmapList = bitmapArrayList;
        this.captionList = captions;
        inflater = ( LayoutInflater)mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return bitmapList.size();
    }

    public Bitmap getItem(int position) {

        return bitmapList.get(position);
    }

    public long getItemId(int position) {

        return position;
    }

    public class Holder
    {
        TextView captionTV;
        ImageView imgV;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();
        View rowV;

        rowV = inflater.inflate(R.layout.ui_layout, null);
        holder.captionTV = (TextView) rowV.findViewById(R.id.captionXML);
        holder.imgV = (ImageView) rowV.findViewById(R.id.imgXML);

        holder.captionTV.setText(captionList.get(position));
        holder.imgV.setImageBitmap(bitmapList.get(position));

        return rowV;
    }
}