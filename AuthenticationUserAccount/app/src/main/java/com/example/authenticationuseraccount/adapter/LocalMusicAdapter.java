package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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
import androidx.fragment.app.FragmentActivity;
import androidx.media3.common.MediaItem;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.fragment.FragmentSearchOptionBottomSheet;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;
import com.example.authenticationuseraccount.model.business.LocalSong;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;
import com.example.authenticationuseraccount.utils.SocketIoManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.ViewHolder> {

    private List<LocalSong> musicList;
    private FragmentActivity mFragmentActivity;
    private Context mContext;
    MediaItem mediaItem = null;

    public LocalMusicAdapter(Context context, FragmentActivity fragmentActivity, List<LocalSong> musicList) {
        this.mContext = context;
        this.musicList = musicList;
        this.mFragmentActivity = fragmentActivity;
    }

    public void setData(List<LocalSong> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searched_song_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalSong song = musicList.get(position);

        Song songLocalTest = new Song();
        songLocalTest.setName(song.getTitle());
        songLocalTest.setArtist(song.getArtistName());
        songLocalTest.setSongURL(song.getData());

        holder.tvSongName.setText(song.getTitle());
        holder.tvArtistName.setText(song.getArtistName());
        holder.tvAlbumName.setText(song.getAlbumName());
        holder.tvSongName.setSelected(true);
        holder.tvArtistName.setSelected(true);
        holder.tvAlbumName.setSelected(true);

        byte[] image = null;
        try {
            image = getAlbumArt(song.getData());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null) {
            songLocalTest.setImageData(image);
            Glide.with(mContext)
                    .load(image)
                    .into(holder.imgSong);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.logo)
                    .into(holder.imgSong);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorUtils.showError(mContext, "Clicked");

                if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
                    MediaItemHolder.getInstance().setMediaItem(songLocalTest);
                } else {
                    ErrorUtils.showError(mContext, "Can not Play Local Song!");
                }

            }
        });

        holder.tvOverflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ErrorUtils.showError(mContext, "3 dot clicked");
                clickOpenOptionBottomSheetFragment(songLocalTest);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (musicList != null) {
            return musicList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSongName, tvArtistName, tvAlbumName, tvOverflowMenu;
        private ImageView imgSong;
        private ConstraintLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout_container);
            tvSongName = itemView.findViewById(R.id.tv_nameSong);
            tvArtistName = itemView.findViewById(R.id.tv_name_artist);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            imgSong = itemView.findViewById(R.id.imageview_song);
            tvOverflowMenu = itemView.findViewById(R.id.overflow_menu);
        }
    }

    private byte[] getAlbumArt(String path) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mContext, Uri.parse(path));
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    FragmentSearchOptionBottomSheet fragmentSearchOptionBottomSheet;

    private void clickOpenOptionBottomSheetFragment(Song song) {
        List<ItemSearchOption> itemSearchOptionList = new ArrayList<>();
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_add_to_queue, Constants.ACTION_ADD_TO_QUEUE));
        itemSearchOptionList.add(new ItemSearchOption(R.drawable.ic_play_next, Constants.ACTION_PLAY_NEXT));

        fragmentSearchOptionBottomSheet = new FragmentSearchOptionBottomSheet(itemSearchOptionList, new IClickSearchOptionItemListener() {
            @Override
            public void clickSearchOptionItem(ItemSearchOption itemSearchOption) {
                switch (itemSearchOption.getText()) {
                    case Constants.ACTION_PLAY_NEXT:
                        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
                            playNext(song);
                        } else {
                            ErrorUtils.showError(mContext, "Can not Play Local Song!");
                        }
                        break;
                    case Constants.ACTION_ADD_TO_QUEUE:
                        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
                            addToQueue(song);
                        } else {
                            ErrorUtils.showError(mContext, "Can not Play Local Song!");
                        }
                        break;
                    default:
                        // Handle default action
                        Toast.makeText(mContext, "Unknown option clicked", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });
        fragmentSearchOptionBottomSheet.show(mFragmentActivity.getSupportFragmentManager(), fragmentSearchOptionBottomSheet.getTag());
    }

    private void addToQueue(Song song) {
        MediaItemHolder.getInstance().getListSongs().add(song);
        mediaItem = MediaItem.fromUri(song.getSongURL());
        MediaItemHolder.getInstance().getMediaController().addMediaItem(mediaItem);
        fragmentSearchOptionBottomSheet.dismiss();
        Toast.makeText(mContext, "Song Added : " + song.getName(), Toast.LENGTH_SHORT).show();
    }

    private void playNext(Song song) {
        if (MediaItemHolder.getInstance().getListSongs().isEmpty()) {
            MediaItemHolder.getInstance().getListSongs().add(song);
            mediaItem = MediaItem.fromUri(song.getSongURL());
            MediaItemHolder.getInstance().getMediaController().addMediaItem(mediaItem);
        } else {
            int currentSongIndex = MediaItemHolder.getInstance().getMediaController().getCurrentMediaItemIndex();
            MediaItemHolder.getInstance().getListSongs().add(currentSongIndex + 1, song);
            mediaItem = MediaItem.fromUri(song.getSongURL());
            MediaItemHolder.getInstance().getMediaController().addMediaItem(currentSongIndex + 1, mediaItem);
        }
        fragmentSearchOptionBottomSheet.dismiss();
        Toast.makeText(mContext, "Song Added To Top : " + song.getName(), Toast.LENGTH_SHORT).show();
    }
}

