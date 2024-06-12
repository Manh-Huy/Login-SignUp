package com.example.authenticationuseraccount.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.LocalMusicAdapter;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.LocalSong;
import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;


public class FragmentLibrary extends Fragment {
    private Context mContext;

    private List<LocalSong> musicList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        mContext = getContext();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        musicList = loadMusic();

        FragmentActivity fragmentActivity = (FragmentActivity) getActivity();

        LocalMusicAdapter adapter = new LocalMusicAdapter(mContext,fragmentActivity, musicList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private List<LocalSong> loadMusic() {
        List<LocalSong> songs = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] CURSOR_PROJECTION = new String[]{"_id", "artist", "album", "title", "duration", "_display_name", "_data", "_size"};
        String selection = "is_music != 0";
        String sortOrder = "_display_name ASC";
        Cursor cursor = mContext.getContentResolver().query(uri, CURSOR_PROJECTION, selection, null, sortOrder);

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
        }

        return songs;
    }

    private Uri getAlbumArtUri(long albumId) {
        Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
        return Uri.withAppendedPath(albumArtUri, String.valueOf(albumId));
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
