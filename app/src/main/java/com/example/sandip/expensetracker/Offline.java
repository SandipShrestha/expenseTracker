package com.example.sandip.expensetracker;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Sandip on 4/28/2018.
 */

public class Offline extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
    /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}