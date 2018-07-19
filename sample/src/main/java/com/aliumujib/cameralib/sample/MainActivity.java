package com.aliumujib.cameralib.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.aliumujib.cameralib.CameraLibActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    public static int CAMERA_ACTIVITY_REQUEST_CODE = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent cameraIntent = new Intent(this, CameraLibActivity.class);
        cameraIntent.putExtra(CameraLibActivity.SET_RESULT_BTN_TITLE, "SEND RESULTS");
        cameraIntent.putExtra(CameraLibActivity.MAX_PHOTO_COUNT, 10);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(cameraIntent, CAMERA_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView resTextView = findViewById(R.id.results_tv);

        if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                ArrayList<String> result=data.getStringArrayListExtra(CameraLibActivity.PHOTO_DATA_TAG);
                for (String s : result) {
                    resTextView.append(s);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//on

}
