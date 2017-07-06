package com.aopanis.forgetmenot.controllers;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.adapters.ImageGalleryAdapter;

import java.io.IOException;

public class GalleryActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private ImageGalleryAdapter imageGalleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Retrieve reference to the RecyclerView
        this.recyclerView = (RecyclerView) this.findViewById(R.id.imageGallery);
        // TODO: Replace number of columns with a setting
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.imageGalleryAdapter = new ImageGalleryAdapter(this.getApplicationContext());
        this.recyclerView.setAdapter(this.imageGalleryAdapter);

        this.loadImages();
    }

    private void loadImages() {
        final Object data = this.getLastCustomNonConfigurationInstance();

        if(data == null) {
            new AsyncLoadImages(this).execute();
        }
        else {
            Bitmap[] images = (Bitmap[]) data;

            if(images.length == 0) {
                new AsyncLoadImages(this).execute();
            }
            else {
                this.AddImage(images);

                // Load any leftover images
                // Get an array containing the image ID column that we want
                String[] projection = { MediaStore.Images.Thumbnails._ID };
                // Create a cursor pointing to the images
                Cursor cursor = getContentResolver().query(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);

                int size = cursor.getCount();

                if(size > images.length) {
                    new AsyncLoadImages(this, images.length - 1).execute();
                }
            }
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        int imageCount = this.imageGalleryAdapter.getItemCount();
        Bitmap[] images = new Bitmap[imageCount];

        for(int i = 0; i < imageCount; i++) {
            images[i] = this.imageGalleryAdapter.GetImage(i);
        }

        return images;
    }

    public void AddImage(Bitmap... image) {
        for(int i = 0; i < image.length; i++) {
            this.imageGalleryAdapter.AddImage(image[i]);
        }

        this.imageGalleryAdapter.notifyDataSetChanged();
    }

    protected class AsyncLoadImages extends AsyncTask<Object, Bitmap, Object> {

        private GalleryActivity activity;
        private int startPos = -1;

        public AsyncLoadImages(GalleryActivity activity) {
            this(activity, -1);
        }
        public AsyncLoadImages(GalleryActivity activity, int startPos) {
            this.activity = activity;
            this.startPos = startPos;
        }

        @Override
        protected Object doInBackground(Object... params) {
            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;

            // Get an array containing the image ID column that we want
            String[] projection = { MediaStore.Images.Thumbnails._ID };
            // Create a cursor pointing to the images
            Cursor cursor = getContentResolver().query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int size = cursor.getCount();

            // If size is zero, there are no images
            // TODO: Implement "no images to display" dialogue
            if(size == 0) {
                Log.d("ImageGallery", "No images to display");
            }

            int imageId = 0;

            // If we are not starting from the beginning, move to the position to start from
            if(startPos != -1) {
                cursor.moveToPosition(startPos);
            }

            while(cursor.moveToNext()) {
                // Get the image ID based off of the index retrieved earlier
                imageId = cursor.getInt(columnIndex);
                uri = uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageId);

                // Attempt to load the bitmap from the uri
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    if (bitmap != null) {
                        newBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
                        bitmap.recycle();
                        if (newBitmap != null) {
                            this.publishProgress(newBitmap);
                        }
                    }
                } catch (IOException e) {
                    //Error fetching image, try to recover
                }
            }

            // Close the cursor

            return null;
        }

        @Override
        public void onProgressUpdate(Bitmap... value) {
            this.activity.AddImage(value);
        }

        @Override
        protected void onPostExecute(Object result) {
            // TODO: Set some "Loading Images" dialogue visibility to false
        }
    }
}
