package com.example.luca.picshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class FeedActivity extends AppCompatActivity {


    LinearLayout linearLayout;
    ImageView imageView;
    private String ERROR_TAG = "_Info.FeedActivity:ERROR";
    private String INFO_TAG = "_Info.FeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


        String username = getIntent().getExtras().get(Const.USERNAME_FIELD).toString();

        setTitle(addPossessiveToUsername(username) + " PicShare");

        linearLayout = findViewById(R.id.linearLayout);


        ParseQuery<ParseObject> query = new ParseQuery<>(Const.IMAGE_CLASS);
        query.whereEqualTo(Const.USERNAME_FIELD, username);
        query.orderByDescending(Const.CREATED_AT_FIELD);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {
                    // no errors
                    Log.i(INFO_TAG, "List<ParseObject> objects.size() = " + objects.size());

                    if (objects.size() > 0) {
                        // we got pictures

                        // loop through all objects
                        for (ParseObject object : objects) {
                            Log.i(INFO_TAG, "Picture: " + object.toString());

                            // get ParseFile
                            ParseFile file = (ParseFile) object.get(Const.IMAGE_FIELD);
                            // get data
                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if (e == null) {
                                        // no error

                                        if (data != null) {
                                            // Add the picture

                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                            imageView = new ImageView(getApplicationContext());

                                            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                            ));

                                            imageView.setPadding(0, 10, 0, 0);

                                            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                                            imageView.setImageBitmap(bitmap);

                                            linearLayout.addView(imageView);

                                        } else {
                                            Log.i(ERROR_TAG, "file.getDataInBackground::byte[] data =  null");
                                        }
                                    } else {
                                        Log.i(ERROR_TAG, e.getMessage());
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }

                    } else {
                        // no pics for this user
                        Log.i(INFO_TAG, "No pics");
                        Toast.makeText(getApplicationContext(), "No pictures found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.i(ERROR_TAG, e.getMessage());
                    e.printStackTrace();
                }

            }
        });

    }

    private String addPossessiveToUsername(String username) {

        if (username.endsWith("s")) {
            username += "\'";
        } else {
            username += "\'s";
        }

        return username;
    }


}
