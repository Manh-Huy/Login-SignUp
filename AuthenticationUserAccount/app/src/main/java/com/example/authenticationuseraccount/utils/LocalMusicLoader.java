package com.example.authenticationuseraccount.utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.LocalSong;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicLoader {
    private static LocalMusicLoader instance;

    // Private constructor to prevent instantiation
    private LocalMusicLoader() { }

    // Public method to provide access to the singleton instance
    public static synchronized LocalMusicLoader getInstance() {
        if (instance == null) {
            instance = new LocalMusicLoader();
        }
        return instance;
    }

    public List<LocalSong> loadMusic(Activity activity) {
        List<LocalSong> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] CURSOR_PROJECTION = new String[]{"_id", "artist", "album", "title", "duration", "_display_name", "_data", "_size"};
        String selection = "is_music != 0";
        String sortOrder = "_display_name ASC";
        Cursor cursor = activity.getContentResolver().query(uri, CURSOR_PROJECTION, selection, null, sortOrder);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = Integer.parseInt(getCursorStringByIndex(cursor, "_id"));
                String artistName = getCursorStringByIndex(cursor, "artist");
                String albumName = getCursorStringByIndex(cursor, "album");
                String title = getCursorStringByIndex(cursor, "title");
                String displayName = getCursorStringByIndex(cursor, "_display_name");
                String data = getCursorStringByIndex(cursor, "_data");
                long duration = getCursorLongByIndex(cursor, "duration");

                if (artistName == null || artistName.isEmpty())
                    artistName = "<unknown>";

                if (displayName.contains("AUD-") && !title.isEmpty())
                    displayName = title;

                Uri songUri = Uri.parse(data);

                songs.add(new LocalSong(
                        id,
                        title,
                        duration,
                        data,
                        albumName,
                        artistName,
                        displayName,
                        songUri));

                LogUtils.ApplicationLogI("Local Music title: " + title + " artist: " + artistName + " songUri: " + songUri + " data: " + data);
            }
            cursor.close(); // Ensure the cursor is closed after use
        }

        return songs;
    }

    private String getCursorStringByIndex(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return (index > -1) ? cursor.getString(index) : "";
    }

    private static long getCursorLongByIndex(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return (index > -1) ? cursor.getLong(index) : -1L;
    }
}
