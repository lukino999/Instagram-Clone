package com.example.luca.instagramclone;

import com.parse.Parse;
import android.app.Application;

public class ParseApp extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        // Remove for production, use to verify FCM is working
        // Look for ParseFCM: FCM registration success messages in Logcat to confirm.
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(ParseServerCredentials.APPLICATION_ID)
                .clientKey(ParseServerCredentials.CLIENT_KEY)
                .server(ParseServerCredentials.SERVER)
                .build()
        );
    }
}