package com.example.authenticationuseraccount.adapter;

import static com.example.authenticationuseraccount.common.Constants.PERMISSION_REQUEST_CODE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.AlbumActivity;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.fragment.FragmentAddPlaylistBottomSheet;
import com.example.authenticationuseraccount.fragment.FragmentPlaylistOptionBottomSheet;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.Playlist;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;
import com.example.authenticationuseraccount.utils.CustomDownloadManager;
import com.example.authenticationuseraccount.utils.SocketIoManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SongAlbumAdapter extends RecyclerView.Adapter<SongAlbumAdapter.SongAlbumViewHolder> {

    private Context mContext;
    private FragmentActivity fragmentActivity;
    private List<Song> listSongs;
    MediaItem mediaItem = null;
    private List<Playlist> listPlaylist;

    public SongAlbumAdapter(Context mContext, FragmentActivity fragmentActivity, List<Song> listSongs, List<Playlist> listPlaylist) {
        this.mContext = mContext;
        this.fragmentActivity = fragmentActivity;
        this.listSongs = listSongs;
        this.listPlaylist = listPlaylist;
    }

    @NonNull
    @Override
    public SongAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View songView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searched_song_result, parent, false);
        return new SongAlbumAdapter.SongAlbumViewHolder(songView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAlbumViewHolder holder, int position) {
        Song song = listSongs.get(position);
        holder.tvSongName.setText(song.getName());
        holder.tvArtistName.setText(song.getArtist());
        holder.tvAlbumName.setText(song.getAlbum());
        holder.tvSongName.setSelected(true);
        holder.tvArtistName.setSelected(true);
        holder.tvAlbumName.setSelected(true);
        Glide.with(mContext)
                .load(song.getImageURL())
                .into(holder.imgSong);
        holder.tvOverflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenOptionBottomSheetFragment(song);
            }
        });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
                    ErrorUtils.showError(mContext, "Clicked");
                    MediaItemHolder.getInstance().setMediaItem(song);
                    fragmentActivity.finish();
                } else {
                    //Host Room
                    if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                        String userID = ChillCornerRoomManager.getInstance().getRoomId();
                        SocketIoManager.getInstance().setSong(userID, song);
                        fragmentActivity.finish();
                    } else {
                        //Guest Room
                        ErrorUtils.showError(mContext, "Only Host Can Change The Playlist!");
                    }
                }

            }
        });

    }

    FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet;

    private void clickOpenOptionBottomSheetFragment(Song song) {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.baseline_favorite_border_24, Constants.ACTION_LOVE));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_download, Constants.ACTION_DOWNLOAD));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.library_add_24px, Constants.ACTION_ADD_TO_PLAYLIST));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_next, Constants.ACTION_PLAY_NEXT));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_queue, Constants.ACTION_ADD_TO_QUEUE));

        fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case Constants.ACTION_LOVE:
                        fragmentSearchOptionBottomSheet.dismiss();
                        Toast.makeText(mContext, "Thích clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case Constants.ACTION_DOWNLOAD:
                        HandleDownload(song.getSongURL(), song.getName());
                        fragmentSearchOptionBottomSheet.dismiss();
                        break;
                    case Constants.ACTION_ADD_TO_PLAYLIST:
                        //fragmentSearchOptionBottomSheet.dismiss();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) {
                            Toast.makeText(mContext, "Please login to add song to playlist", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            clickOpenPlaylistOptionBottomSheetFragment(user.getUid(), song);
                        }
                        break;
                    case Constants.ACTION_PLAY_NEXT:
                        playNext(song);
                        break;
                    case Constants.ACTION_ADD_TO_QUEUE:
                        addToQueue(song);
                        break;
                    default:
                        // Handle default action
                        Toast.makeText(mContext, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(fragmentActivity.getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }

    private void clickOpenPlaylistOptionBottomSheetFragment(String userID, Song song) {
        FragmentPlaylistOptionBottomSheet fragmentPlaylistOptionBottomSheet = new FragmentPlaylistOptionBottomSheet(listPlaylist,userID, song);
        fragmentPlaylistOptionBottomSheet.show(fragmentActivity.getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }

    private void addToQueue(Song song) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().addMediaItemToQueue(song);
            fragmentSearchOptionBottomSheet.dismiss();
            fragmentActivity.finish();
            Toast.makeText(mContext, song.getName() + " Added to queue", Toast.LENGTH_SHORT).show();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String roomID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().addSong(roomID, song);
                fragmentActivity.finish();
            } else {
                //Guest Room
                ErrorUtils.showError(mContext, "Only Host Can Change The Playlist!");
            }
        }
    }

    private void playNext(Song song) {
        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            MediaItemHolder.getInstance().playMediaItemNext(song);
            fragmentSearchOptionBottomSheet.dismiss();
            fragmentActivity.finish();
            Toast.makeText(mContext, song.getName() + " will play next", Toast.LENGTH_SHORT).show();
        } else {
            //Host Room
            if (ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
                String userID = ChillCornerRoomManager.getInstance().getRoomId();
                SocketIoManager.getInstance().addSongPlayNext(userID, song);
                fragmentActivity.finish();
            } else {
                //Guest Room
                ErrorUtils.showError(mContext, "Only Host Can Change The Playlist!");
            }
        }
    }

    private void HandleDownload(String fileUrl, String filename) {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(fragmentActivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed with downloading
            CustomDownloadManager.getInstance(mContext).downloadFile(fileUrl, filename);

        }
    }


    @Override
    public int getItemCount() {
        if (listSongs != null) {
            return listSongs.size();
        }
        return 0;
    }

    public class SongAlbumViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSongName, tvArtistName, tvAlbumName, tvOverflowMenu;
        private ImageView imgSong;
        private ConstraintLayout layout;


        public SongAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_container);
            tvSongName = itemView.findViewById(R.id.tv_nameSong);
            tvArtistName = itemView.findViewById(R.id.tv_name_artist);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            imgSong = itemView.findViewById(R.id.imageview_song);
            tvOverflowMenu = itemView.findViewById(R.id.overflow_menu);
        }
    }
}
