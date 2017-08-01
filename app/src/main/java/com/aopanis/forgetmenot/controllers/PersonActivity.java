package com.aopanis.forgetmenot.controllers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.aopanis.forgetmenot.R;

import java.io.IOException;

public class PersonActivity extends AppCompatActivity {
    private final String TAG = "PersonActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        Intent intent = this.getIntent();
        Uri uri = intent.getParcelableExtra("ProcessedImageURI");
        ImageView imageView = (ImageView) this.findViewById(R.id.personImageView);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            Log.d(TAG, "onCreate: Bitmap Load successful");
            imageView.setImageBitmap(bitmap);
        } catch(IOException e) {
            Toast.makeText(this, "Failed to load iamge", Toast.LENGTH_LONG).show();
        }
    }
}
