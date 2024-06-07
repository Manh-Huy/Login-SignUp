package com.example.authenticationuseraccount.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.SearchFilterAdapter;
import com.example.authenticationuseraccount.adapter.SearchedItemAdapter;
import com.example.authenticationuseraccount.adapter.SongAlbumAdapter;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {
    private ImageButton btnMore;
    private RecyclerView songRecyclerView;
    private SongAlbumAdapter songAlbumAdapter;
    private List<Song> listSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        songRecyclerView = findViewById(R.id.rcv_song_list);
        FragmentActivity fragmentActivity = AlbumActivity.this;
        songAlbumAdapter = new SongAlbumAdapter(getApplicationContext(), fragmentActivity, listSong);
        songRecyclerView.setAdapter(songAlbumAdapter);

        btnMore = findViewById(R.id.more_button);
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
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play, "Phát"));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_heart, "Thích"));
        itemSearchOptionList.add(new ItemSearchOption(leveldown.kyle.icon_packs.R.drawable.shuffle_24px, "Phát ngẫu nhiên"));

        FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case "Thêm vào danh sách phát":
                        // Handle "Thích" action
                        Toast.makeText(AlbumActivity.this, "Thêm vào danh sách phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát":
                        // Handle "Tải xuống" action
                        Toast.makeText(AlbumActivity.this, "Phát clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Thích":
                        Toast.makeText(AlbumActivity.this, "Thích clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case "Phát ngẫu nhiên":
                        Toast.makeText(AlbumActivity.this, "Phát ngẫu nhiên clicked", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        // Handle default action
                        Toast.makeText(AlbumActivity.this, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }
}