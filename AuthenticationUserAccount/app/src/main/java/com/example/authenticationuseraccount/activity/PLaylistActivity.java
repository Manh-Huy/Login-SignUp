package com.example.authenticationuseraccount.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SongAlbumAdapter;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Playlist;
import com.example.authenticationuseraccount.model.business.Song;

import java.util.ArrayList;
import java.util.List;

public class PLaylistActivity extends AppCompatActivity {
    private TextView playlistTitle, songCount;
    private ImageButton btnMore;
    private RecyclerView songRecyclerView;
    private SongAlbumAdapter songAlbumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        songCount = findViewById(R.id.song_count);
        playlistTitle = findViewById(R.id.playlist_title);
        songRecyclerView = findViewById(R.id.rcv_song_list);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        btnMore = findViewById(R.id.more_button);

        Intent intent = getIntent();
        Playlist playlist = (Playlist) intent.getSerializableExtra("playlist");

        if (playlist != null) {
            String playlistName = playlist.getPlaylistName();
            List<Song> playListSong = playlist.getListSong();

            playlistTitle.setText(playlistName.toUpperCase());
            String count = playListSong.size() + " bài hát";
            songCount.setText(count);

            FragmentActivity fragmentActivity = PLaylistActivity.this;
            songAlbumAdapter = new SongAlbumAdapter(getApplicationContext(), fragmentActivity, playListSong);
            songRecyclerView.setAdapter(songAlbumAdapter);
        }

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenOptionBottomSheet();
            }
        });

    }
    private void clickOpenOptionBottomSheet() {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_playlist, "Thêm vào danh sách phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_arrow, "Phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_heart, "Thích"));
        itemSearchOptionList.add(new ItemSearchOption(leveldown.kyle.icon_packs.R.drawable.shuffle_24px, "Phát ngẫu nhiên"));

        FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case "Thêm vào danh sách phát":
                        Toast.makeText(PLaylistActivity.this, "Thêm vào danh sách phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát":
                        Toast.makeText(PLaylistActivity.this, "Phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Thích":
                        Toast.makeText(PLaylistActivity.this, "Thích clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát ngẫu nhiên":
                        Toast.makeText(PLaylistActivity.this, "Phát ngẫu nhiên clicked", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(PLaylistActivity.this, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }
}