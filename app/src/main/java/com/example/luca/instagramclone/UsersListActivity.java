package com.example.luca.instagramclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    ArrayList<ParseObject> parseUsers = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    ParseUser currentUser;
    private String INFO_TAG = "_Info.UsersListActivity";
    private String ERROR_TAG = "_Info.UsersListActivity:ERROR";
    private int READ_EXTERNAL_STORAGE_REQUEST = 1001;
    private int PICK_PICTURE_REQ_CODE = 10001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        //
        currentUser = ParseUser.getCurrentUser();
        setTitle("Users");

        // query
        showUsersList();

    }

    private void showUsersList() {

        // query users
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        // except currentUser
        query.whereNotEqualTo(Const.USERNAME_FIELD, currentUser.getUsername());
        // sort by name
        query.addAscendingOrder(Const.USERNAME_FIELD);
        // call query
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null) {
                    // no errors
                    if (objects.size() > 0) {
                        // we got data


                        for (ParseObject user : objects) {
                            parseUsers.add(user);
                            users.add(user.get(Const.USERNAME_FIELD).toString());

                            ArrayAdapter adapter = new ArrayAdapter(UsersListActivity.this, android.R.layout.simple_list_item_1, users);
                            ListView listView = findViewById(R.id.listView);
                            listView.setAdapter(adapter);

                            // call FeedActivity
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    TextView userTextView = (TextView) view;

                                    String username = userTextView.getText().toString();
                                    Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
                                    intent.putExtra(Const.USERNAME_FIELD, username);
                                    startActivity(intent);
                                }
                            });

                        }
                    } else {
                        Log.i(ERROR_TAG, "showUsersList: List<ParseObject> objects is empty");
                    }
                } else {
                    Log.i(ERROR_TAG, e.getStackTrace().toString());
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*
        if (item.getItemId() == R.id.share) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickPhoto();
            } else if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
            }
        }
        */

        switch (item.getItemId()) {
            case R.id.share:
                // check for READ_EXTERNAL_STORAGE permission
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pickPhoto();
                } else if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
                }

            case R.id.logout:
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // close this activity and go back to MainActivity
                            finish();
                        } else {
                            Log.i(ERROR_TAG, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

        }


        return super.onOptionsItemSelected(item);

    }

    private void pickPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PICTURE_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(INFO_TAG, "onActivityResult");

        if (requestCode == PICK_PICTURE_REQ_CODE && resultCode == RESULT_OK && data != null) {

            Log.i(INFO_TAG, "requestCode == PICK_PICTURE_REQ_CODE && requestCode == RESULT_OK && data != null");
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Log.i(INFO_TAG, "We got the bitmap " + bitmap.toString());

                uploadBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void uploadBitmap(Bitmap bitmap) {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile file = new ParseFile("image.png", byteArray);
        ParseObject object = new ParseObject(Const.IMAGE_CLASS);
        object.put(Const.IMAGE_FIELD, file);
        object.put(Const.USERNAME_FIELD, currentUser.getUsername());
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(UsersListActivity.this, "Image shared", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(ERROR_TAG, e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            pickPhoto();

        }

    }
}
