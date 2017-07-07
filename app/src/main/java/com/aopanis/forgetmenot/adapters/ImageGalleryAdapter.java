package com.aopanis.forgetmenot.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.models.GalleryImage;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageHolder> {

    // Log.d tag constant
    private static final String TAG = "ImageGalleryAdapter";

    private ArrayList<GalleryImage> galleryImages;
    private final RequestManager glide;

    public ImageGalleryAdapter(RequestManager glide, GalleryImage... images) {
        this.glide = glide;
        this.galleryImages = new ArrayList<GalleryImage>(0);
        for(GalleryImage image : images) {
            this.galleryImages.add(image);
        }
    }

    public void AddImage(Uri uri) {
        this.galleryImages.add(new GalleryImage(uri));
    }
    public Uri GetUri(int position) {
        return this.galleryImages.get(position).GetUri();
    }

    @Override
    public ImageGalleryAdapter.ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View imageView = inflater.inflate(R.layout.gallery_row, parent, false);
        ImageGalleryAdapter.ImageHolder imageHolder = new ImageGalleryAdapter.ImageHolder(imageView);
        return imageHolder;
    }

    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        GalleryImage galleryImage = this.galleryImages.get(position);
        ImageView imageView = holder.imageView;

        this.glide.load(galleryImage.GetUri())
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return this.galleryImages.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Reference to ImageView to hold on to
        private ImageView imageView;

        public ImageHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.galleryImageItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                // TODO: Handle image on click
            }
        }
    }
}
