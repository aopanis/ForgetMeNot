package com.aopanis.forgetmenot.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aopanis.forgetmenot.R;

import java.util.ArrayList;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageHolder> {

    private Context context;
    // The list of images to display
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();

    public ImageGalleryAdapter(Context context) {
        this.context = context;
    }

    public void AddImage(Bitmap image) {
        this.images.add(image);
    }

    public Bitmap GetImage(int position) {
        return this.images.get(position);
    }

    public long GetItemId(int position) {
        return position;
    }

    // Create a new ImageHolder when it is needed
    @Override
    public ImageGalleryAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_row, parent, false);
        return new ImageHolder(inflatedView);
    }

    // Set the image when a new ImageHolder is needed
    @Override
    public void onBindViewHolder(ImageGalleryAdapter.ImageHolder holder, int position) {
        holder.BindImage(this.images.get(position));
    }

    @Override
    public int getItemCount() {
        return this.images.size();
    }

    // Image holder class to contain references to views for RecyclerView
    protected class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private Bitmap image;

        public ImageHolder(View itemView) {
            super(itemView);

            this.imageView = (ImageView) itemView.findViewById(R.id.galleryImageItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("ImageGallery", "Image clicked");
        }

        public void BindImage(Bitmap image) {
            this.image = image;
            this.imageView.setImageBitmap(this.image);
        }
    }
}