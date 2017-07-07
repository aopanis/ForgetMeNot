package com.aopanis.forgetmenot.helpers;

import android.Manifest;

public class Permission {
    private String permission;
    public String getPermission() {
        return this.permission;
    }
    private String rationale;
    public String getRationale() {
        return this.rationale;
    }

    public Permission(String permission, String rationale) {
        this.permission = permission;
        this.rationale = rationale;
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
}
