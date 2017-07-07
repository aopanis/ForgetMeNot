package com.aopanis.forgetmenot.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aopanis.forgetmenot.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageHolder> {

    private Context context;
    // The list of images to display
    private ArrayList<Uri> uris = new ArrayList<Uri>();

    public ImageGalleryAdapter(Context context) {
        this.context = context;
    }

    public void AddImage(Uri uri) {
        this.uris.add(uri);
    }

    public Uri GetUri(int position) {
        return this.uris.get(position);
    }

    public long GetItemId(int position) {
        return position;
    }

    // Create a new ImageHolder when it is needed
    @Override
    public ImageGalleryAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_row, parent, false);
        return new ImageHolder(inflatedView, this.context);
    }

    // Set the image when a new ImageHolder is needed
    @Override
    public void onBindViewHolder(ImageGalleryAdapter.ImageHolder holder, int position) {
        holder.BindImage(this.uris.get(position));
    }

    @Override
    public int getItemCount() {
        return this.uris.size();
    }

    // Image holder class to contain references to views for RecyclerView
    protected class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private Context context;
        private Uri uri;

        public ImageHolder(View itemView, Context context) {
            super(itemView);

            this.context = context;
            this.imageView = (ImageView) itemView.findViewById(R.id.galleryImageItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("ImageGallery", "Image clicked");
        }

        public void BindImage(Uri uri) {
            this.uri = uri;
            Glide.with(this.context)
                .load(this.uri)
                .thumbnail(0.5f)
                .into(this.imageView);

            Log.d("ImageGallery", "Loaded image from URI " + this.uri.toString() +
                    " into ImageView " + this.imageView.toString());
        }
    }
}
