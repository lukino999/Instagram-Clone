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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
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

    private final String USER_CLASS = "User";
    private final String USERNAME_FIELD = "username";
    private final String IMAGE_CLASS = "Image";
    private final String IMAGE_FIELD = "image";
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

        checkLoggedUser();

        // query

        getUsers();

    }

    private void getUsers() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo(USERNAME_FIELD, currentUser.getUsername());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if (e == null) {

                    if (objects.size() > 0) {
                        for (ParseObject user : objects) {
                            parseUsers.add(user);
                            users.add(user.get(USERNAME_FIELD).toString());

                            ArrayAdapter adapter = new ArrayAdapter(UsersListActivity.this, android.R.layout.simple_list_item_1, users);
                            ListView listView = findViewById(R.id.listView);
                            listView.setAdapter(adapter);

                        }
                    } else {
                        Log.i(ERROR_TAG, "getUsers: List<ParseObject> objects is empty");
                    }
                } else {
                    Log.i(ERROR_TAG, e.getStackTrace().toString());
                }

            }
        });
    }

    private void checkLoggedUser() {
        currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Logged in as " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();
            Log.i(INFO_TAG, "Logged in as " + currentUser.getUsername());
        } else {
            Log.i(INFO_TAG, "Not logged in");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i(INFO_TAG, "onOptionsItemSelected");
        if (item.getItemId() == R.id.share) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickPhoto();
            } else if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST);
            }
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
        ParseObject object = new ParseObject(IMAGE_CLASS);
        object.put(IMAGE_FIELD, file);
        object.put(this.USERNAME_FIELD, currentUser.getUsername());
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
