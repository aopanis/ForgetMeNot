package com.aopanis.forgetmenot.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class GalleryImage implements Parcelable{

    private String uri;

    public GalleryImage(String uri) {
        this.uri = uri;
    }
    public GalleryImage(Uri uri) {
        this.uri = uri.toString();
    }

    protected GalleryImage(Parcel in) {
        this.uri = in.readString();
    }

    public static final Creator<GalleryImage> CREATOR = new Creator<GalleryImage>() {
        @Override
        public GalleryImage createFromParcel(Parcel in) {
            return new GalleryImage(in);
        }

        @Override
        public GalleryImage[] newArray(int size) {
            return new GalleryImage[size];
        }
    };

    public Uri GetUri() {
        return Uri.parse(this.uri);
    }
    public void SetUri(Uri value) {
        this.uri = value.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uri);
    }
}
