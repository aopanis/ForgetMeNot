package com.aopanis.forgetmenot.controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.GridView;

import com.aopanis.forgetmenot.R;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Retrieve reference to the RecyclerView
        GridView recyclerView = (GridView) this.findViewById(R.id.imageGallery);
    }
}
