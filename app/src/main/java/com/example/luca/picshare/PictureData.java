package com.example.luca.picshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

public class PictureData {

    /*
    This class contains the data to be passed to the PictureDataAdapter
     */
    private final String addedDate;
    private final ParseObject object;
    private String ERROR_TAG = "_Info.PictureData:ERROR";
    private String INFO_TAG = "_Info.PictureData";

    public PictureData(ParseObject object) {
        this.addedDate = object.getCreatedAt().toString();
        this.object = object;
    }

    public Bitmap getBitmap() {

        Log.i(INFO_TAG, "getBitmap::Object " + object.toString() + " - createdAt: " + this.addedDate);
        // get ParseFile
        ParseFile file = (ParseFile) object.get(Const.IMAGE_FIELD);
        // get data
        Bitmap bitmap = null;
        try {
            byte[] data = file.getData();

            // TODO: 15/05/2018 this line is throwing OutOfMemory
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);


        } catch (ParseException e) {
            Log.i(ERROR_TAG, e.getMessage());
            e.printStackTrace();
        } catch (Exception exeption) {
            Log.i(ERROR_TAG, exeption.getMessage());
            exeption.printStackTrace();
        }


        return bitmap;
    }

    public String getAddedDate() {
        return addedDate;
    }
}
