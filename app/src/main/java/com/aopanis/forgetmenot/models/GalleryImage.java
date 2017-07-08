package com.aopanis.forgetmenot.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class GalleryImage implements Parcelable{

    private String uri;
    private double latitude, longitude;

    public GalleryImage(String uri, double latitude, double longitude) {
        this.uri = uri;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public GalleryImage(Uri uri, double latitude, double longitude) {
        this.uri = uri.toString();
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public GalleryImage(String uri) {
        this(uri, Double.NaN, Double.NaN);
    }
    public GalleryImage(Uri uri) {
        this(uri, Double.NaN, Double.NaN);
    }

    protected GalleryImage(Parcel in) {
        this.uri = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
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

    public Uri getUri() {
        return Uri.parse(this.uri);
    }
    public void setUri(Uri value) {
        this.uri = value.toString();
    }
    public Double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(Double value) {
        this.latitude = value;
    }
    public Double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(Double value) {
        this.longitude = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uri);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }
}
