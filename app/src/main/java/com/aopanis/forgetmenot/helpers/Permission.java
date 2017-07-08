package com.aopanis.forgetmenot.helpers;

import android.Manifest;

@Deprecated
public class Permission {
    @Deprecated
    private String permission;
    @Deprecated
    public String getPermission() {
        return this.permission;
    }
    @Deprecated
    private String rationale;
    @Deprecated
    public String getRationale() {
        return this.rationale;
    }

    @Deprecated
    public Permission(String permission, String rationale) {
        this.permission = permission;
        this.rationale = rationale;
    }

    @Deprecated
    public void SetRationale(String value) {
        this.rationale = value;
    }

    public static Permission PERMISSION_READ_EXTERNAL_STORAGE =
        new Permission(Manifest.permission.READ_EXTERNAL_STORAGE,
            "We need permission to read from the system storage in order " +
            "to access media on your device.");
    public static Permission PERMISSION_WRITE_EXTERNAL_STORAGE =
        new Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "We need permission to write to the system storage in order " +
            "to save media onto your device.");
    public static Permission PERMISSION_CAMERA =
        new Permission(Manifest.permission.CAMERA,
            "We need permission to access your camera in order " +
            "to take pictures.");
    public static Permission PERMISSION_ACCESS_COARSE_LOCATION =
            new Permission(Manifest.permission.ACCESS_COARSE_LOCATION,
            "We need permission to access your gps location in order " +
            "to tag your photos.");
    public static Permission PERMISSION_ACCESS_FINE_LOCATION =
            new Permission(Manifest.permission.ACCESS_FINE_LOCATION,
            "We need permission to access your network location in order " +
            "to more accurately tag your photos.");
}
