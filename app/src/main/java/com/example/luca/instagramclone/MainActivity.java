package com.example.luca.instagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener {

    EditText userEditText;
    EditText passwEditText;
    boolean loginEnabled = true;
    private String INFO_TAG = "_Info.MainActivity";
    private String ERROR_TAG = "_Info.MainActivity:ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check whether already logged in
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Logged in as " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();
            Log.i(INFO_TAG, "Logged in as " + currentUser.getUsername());
            goToUsersListActivity();
        }


        userEditText = findViewById(R.id.usernameEditText);
        passwEditText = findViewById(R.id.passwordEditText);

        // set on click listener so it will hide the keyboard
        ConstraintLayout background = findViewById(R.id.backgroundLayout);
        background.setOnClickListener(this);
        ImageView logo = findViewById(R.id.logo);
        logo.setOnClickListener(this);

    }




    public void signUp() {

        String user = userEditText.getText().toString();
        String password = passwEditText.getText().toString();
        String message = "";

        if (user.isEmpty()) {
            message = "Please insert your username";
        }

        if (password.isEmpty()) {
            if (!message.isEmpty()) {
                message += "\n";
            }
            message += "Please insert password";
        }

        if (!message.isEmpty()) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        if (!user.isEmpty() && !password.isEmpty()) {
            ParseUser parseUser = new ParseUser();
            parseUser.setUsername(user);
            parseUser.setPassword(password);
            parseUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    String signUpMessage;
                    if (e == null) {
                        signUpMessage = "Signed up successfully";
                        toggleLogInSignUp();
                        hideKeyboard();
                    } else {
                        signUpMessage = e.getMessage();
                    }
                    Log.i(INFO_TAG, signUpMessage);
                    Toast.makeText(getApplicationContext(), signUpMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }




    public void logIn() {

        String user = userEditText.getText().toString();
        String password = passwEditText.getText().toString();
        String message = "";

        if (user.isEmpty()) {
            message = "Please insert your username";
        }

        if (password.isEmpty()) {
            if (!message.isEmpty()) {
                message += "\n";
            }
            message += "Please insert password";
        }

        if (!message.isEmpty()) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        if (!user.isEmpty() && !password.isEmpty()) {

            // log in
            ParseUser.logInInBackground(user, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        if (user != null) {
                            hideKeyboard();

                            // go to usersListActivity
                            goToUsersListActivity();

                        } else {
                            Log.i(INFO_TAG, "user = null");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void goToUsersListActivity() {
        Intent intent = new Intent(getApplicationContext(), UsersListActivity.class);
        startActivity(intent);
    }


    public void btnClick(View view) {

        if (loginEnabled) {
            logIn();
        } else {
            signUp();
        }
    }






    public void textViewClick(View view) {

        toggleLogInSignUp();
    }

    private void toggleLogInSignUp() {
        Button button = findViewById(R.id.button);
        TextView textView = findViewById(R.id.textView);

        if (loginEnabled) {
            button.setText("Sign up");
            textView.setText("Log in");
        } else {
            button.setText("Log in");
            textView.setText("Sign up");
        }

        // toggle
        loginEnabled = !loginEnabled;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            btnClick(v);
        }

        return false;
    }





    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.backgroundLayout || v.getId() == R.id.logo) {
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
}
