package com.example.sandip.expensetracker;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;


public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Element aboutUs = new Element();
        aboutUs.setTitle("Islington College");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_inapp_logo)
                .setDescription("Thanks for using expensify")
                .addItem(new Element().setTitle("Version 1.0"))
                .addItem(aboutUs)
                .addItem(new Element().setTitle("Mukesh Regmi"))
                .addItem(new Element().setTitle("Bhim Bahadur Sunar"))
                .addItem(createCopyrights())
                .create();
        setContentView(aboutPage);
    }
    private Element createCopyrights(){
        Element copyright = new Element();
        final String  copyrightString = String.format("Copyright %d by Sandip Shrestha", Calendar.getInstance().get(Calendar.YEAR));
        copyright.setTitle(copyrightString);
        copyright.setGravity(Gravity.CENTER);
        copyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(About.this,copyrightString,Toast.LENGTH_SHORT).show();
            }
        });
        return copyright;
    }
}
