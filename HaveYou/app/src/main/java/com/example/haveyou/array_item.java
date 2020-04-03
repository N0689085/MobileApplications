package com.example.haveyou;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class array_item {
    private int mImage;
    private String mText;
    private String mSubText;

    public array_item(int image, String MainText ,String SubText){
        mImage = image;
        mText = MainText;
        mSubText = SubText;

    }

    public void getHistory(int position){

    }

    public int getImage() {
        return mImage;
    }

    public String getMainText() {
        return mText;
    }

    public String getSubText() {
        return mSubText;
    }


}
