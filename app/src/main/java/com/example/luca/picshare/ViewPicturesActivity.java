package com.example.luca.picshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewPicturesActivity extends AppCompatActivity {

    ArrayList<PictureData> pictures = new ArrayList<>();
    private String ERROR_TAG = "_Info.ViewPicturesActivity:ERROR";
    private String INFO_TAG = "_Info.ViewPicturesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);


        String username = getIntent().getExtras().get(Const.USERNAME_FIELD).toString();

        setTitle(addPossessiveToUsername(username) + " PicShare");


        ParseQuery<ParseObject> query = new ParseQuery<>(Const.IMAGE_CLASS);
        query.orderByDescending("createdAt");
        query.whereEqualTo(Const.USERNAME_FIELD, username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {
                    // no errors
                    Log.i(INFO_TAG, "List<ParseObject> objects.size() = " + objects.size());

                    if (objects.size() > 0) {
                        // we got pictures
                        for (ParseObject object : objects) {
                            pictures.add(new PictureData(object));
                        }

                        showListView();

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


    private void showListView() {
        ListView listView = findViewById(R.id.picturesListView);

        PictureDataAdapter adapter = new PictureDataAdapter(getApplicationContext(), R.layout.picture_list_item, pictures);

        listView.setAdapter(adapter);
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
