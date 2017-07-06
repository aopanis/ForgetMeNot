package com.aopanis.forgetmenot.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageGalleryAdapter extends BaseAdapter{

    private Context context;

    public ImageGalleryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView pictureView;

        if(convertView == null) {
            pictureView = new ImageView(this.context);
        }
        else {
            pictureView = (ImageView) convertView;
        }

        return pictureView;
    }
}
