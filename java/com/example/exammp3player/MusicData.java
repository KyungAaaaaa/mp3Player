package com.example.exammp3player;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MusicData{
    private String fileName;
    private String title;
    private String singer;
    private Bitmap bitmap;
    private String duration;

    public MusicData(String title, String singer) {
        this.title = title;
        this.singer = singer;
    }

    public MusicData(String fileName, String title, String singer, Bitmap bitmap, String duration) {
        this.fileName = fileName;
        this.title = title;
        this.singer = singer;
        this.bitmap = bitmap;
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
