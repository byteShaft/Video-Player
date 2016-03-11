package com.byteshaft.videoplayer;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.TypedValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

public class Helpers extends ContextWrapper {

    public Helpers(Context base) {
        super(base);
    }

    float getDensityPixels(int pixels) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics());
    }

    ArrayList<String> getAllVideosUri() {
        ArrayList<String> uris = new ArrayList<>();
        Cursor cursor = getVideosCursor();
        int pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        while (cursor.moveToNext()) {
            uris.add(cursor.getString(pathColumn));
        }
        cursor.close();
        return uris;
    }

    private Cursor getVideosCursor() {
        String[] Projection = {MediaStore.Video.Media._ID, MediaStore.Images.Media.DATA};
        return getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                Projection, null, null, null);
    }

    ArrayList<String> getVideoTitles(ArrayList<String> videos) {
        ArrayList<String> vids = new ArrayList<>();
        for (String video : videos) {
            File file = new File(video);
            vids.add(file.getName());
        }
        return vids;
    }

    int getDurationForVideo(int databaseIndex) {
        String[] projection = {MediaStore.Video.Media.DURATION};
        Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);
        cursor.moveToPosition(databaseIndex);
        String duration = cursor.getString(durationColumn);
        cursor.close();
        return Integer.valueOf(duration);
    }

    String getFormattedTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}
