package com.aopanis.forgetmenot.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aopanis.forgetmenot.R;
import com.aopanis.forgetmenot.controllers.GalleryActivity;
import com.aopanis.forgetmenot.controllers.ImageActivity;
import com.aopanis.forgetmenot.models.GalleryImage;
import com.aopanis.forgetmenot.views.SquareImageView;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageHolder> {

    // Log.d tag constant
    private static final String TAG = "ImageGalleryAdapter";

    private ArrayList<GalleryImage> galleryImages;
    private final RequestManager glide;
    private Context context;

    public ImageGalleryAdapter(RequestManager glide, Context context, GalleryImage... images) {
        this.glide = glide;
        this.context = context;
        this.galleryImages = new ArrayList<GalleryImage>();
        for(GalleryImage image : images) {
            this.galleryImages.add(image);
        }
    }

    public void AddImage(GalleryImage image) {
        this.galleryImages.add(image);
    }
    public GalleryImage getImage(int position) {
        return this.galleryImages.get(position);
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
        SquareImageView imageView = holder.imageView;
        ImageView locationView = holder.locationView;

        this.glide.load(galleryImage.getUri())
                .into(imageView);

        // Set the visibility of the location icon based on whether there is
        // location data for this image
        locationView.setVisibility(
                galleryImage.getLatitude().isNaN() && galleryImage.getLongitude().isNaN() ?
                View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return this.galleryImages.size();
    }

    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Reference to ImageView to hold on to
        private SquareImageView imageView;
        private ImageView locationView;

        public ImageHolder(View itemView) {
            super(itemView);
            this.imageView = (SquareImageView) itemView.findViewById(R.id.galleryImageItem);
            this.locationView = (ImageView) itemView.findViewById(R.id.galleryLocationIconItem);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = this.getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                GalleryImage image = galleryImages.get(position);
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra(GalleryActivity.IMAGE_EXTRA, image);
                context.startActivity(intent);
            }
        }
    }
}
