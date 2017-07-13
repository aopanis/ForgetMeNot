package com.aopanis.forgetmenot.models;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.media.ExifInterface;

import com.aopanis.forgetmenot.helpers.FileHelper;
import com.aopanis.forgetmenot.helpers.GPSHelper;

import java.io.File;
import java.io.IOException;

public class GalleryImage implements Parcelable{

    private String uri;
    private double latitude, longitude;
    private long id;

    public GalleryImage(String uri, double latitude, double longitude, long id) {
        this.uri = uri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }
    public GalleryImage(Uri uri, double latitude, double longitude, long id) {
        this.uri = uri.toString();
        this.latitude = latitude;
        this.longitude = longitude;
        this.id = id;
    }
    public GalleryImage(String uri, long id) {
        this(uri, Double.NaN, Double.NaN, id);
    }
    public GalleryImage(Uri uri, long id) {
        this(uri, Double.NaN, Double.NaN, id);
    }

    protected GalleryImage(Parcel in) {
        this.uri = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.id = in.readLong();
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
    public void setLatitude(Double value, Context context) {
        this.latitude = value;

        ExifInterface exifInterface = this.getExif(context);
        exifInterface.setAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE,
                GPSHelper.convertToDms(this.latitude));
        exifInterface.setAttribute(android.media.ExifInterface.TAG_GPS_LATITUDE_REF,
                GPSHelper.latitudeRefDtS(this.latitude));
        try {
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(Double value, Context context) {
        this.longitude = value;

        ExifInterface exifInterface = this.getExif(context);
        exifInterface.setAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE,
                GPSHelper.convertToDms(this.longitude));
        exifInterface.setAttribute(android.media.ExifInterface.TAG_GPS_LONGITUDE_REF,
                GPSHelper.latitudeRefDtS(this.longitude));
        try {
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public long getId() {
        return this.id;
    }
    public void setId(long value) {
        this.id = value;
    }
    public ExifInterface getExif (Context context) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(FileHelper.getRealPathFromURI(this.getUri(), context));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exifInterface;
    }

    private File getFile() {
        return new File(this.getUri().getPath());
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
        dest.writeLong(this.id);
    }
}
