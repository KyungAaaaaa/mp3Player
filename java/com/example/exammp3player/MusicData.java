package com.example.exammp3player;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class MusicData implements Parcelable {
    private String fileName;
    private String title;
    private String singer;
    private Bitmap bitmap;
    private String duration;

    public MusicData(String fileName, String title, String singer, Bitmap bitmap, String duration) {
        this.fileName = fileName;
        this.title = title;
        this.singer = singer;
        this.bitmap = bitmap;
        this.duration = duration;
    }

    protected MusicData(Parcel in) {
        fileName = in.readString();
        title = in.readString();
        singer = in.readString();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        duration = in.readString();
    }

    public static final Creator<MusicData> CREATOR = new Creator<MusicData>() {
        @Override
        public MusicData createFromParcel(Parcel in) {
            return new MusicData(in);
        }

        @Override
        public MusicData[] newArray(int size) {
            return new MusicData[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fileName);
        parcel.writeString(title);
        parcel.writeString(singer);
        parcel.writeParcelable(bitmap, i);
        parcel.writeString(duration);
    }
}
