package com.quickbuttons.data_model;

import android.graphics.Bitmap;

public class QuickButton {

    long id;
    public String label;
    public String text;
    public Bitmap image;
    public int page;
    public int position;

    QuickButton(long id){
        this.id = id;
    }

    public QuickButton(String label, String text, int page, int position) {
        this.label = label;
        this.text = text;
        this.page = page;
        this.position = position;
    }



}
