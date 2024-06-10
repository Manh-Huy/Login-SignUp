package com.example.authenticationuseraccount.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.activity.MainActivity;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.SocketIoManager;
import com.example.authenticationuseraccount.service.UIThread;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class FragmentCorner extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_corner, container, false);
    }

    private Button btnCreatRoom, btnSendSong, btnJoinRoom;
    private FirebaseAuth mAuth;
    private EditText edtUserId;
    private TextView tvUserId;
    private String userID, userName;
    private Context mContext;
    private SocketIoManager mSocketIoManager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtUserId = view.findViewById(R.id.edt_enter_room);
        tvUserId = view.findViewById(R.id.tv_user_id);
        btnSendSong = view.findViewById(R.id.btn_send_song);
        btnCreatRoom = view.findViewById(R.id.btn_create_room);
        btnJoinRoom = view.findViewById(R.id.btn_connet_room);

        mContext = getContext();
        mSocketIoManager = SocketIoManager.getInstance();
        listenForRoomEvent(mSocketIoManager.getmSocket());

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            userName = user.getDisplayName();
            tvUserId.setText(userID);
        } else {
            tvUserId.setText("");
        }

        btnCreatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    userID = user.getUid();
                    mSocketIoManager.createRoom(userID);
                    tvUserId.setText(userID);
                } else {
                    ErrorUtils.showError(getContext(), "Please Login To Create Room");
                }
            }
        });

        btnJoinRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = String.valueOf(edtUserId.getText());
                if (roomId.isEmpty()) {
                    ErrorUtils.showError(mContext, "Room ID cannot be empty");
                } else {
                    mSocketIoManager.joinRoom(roomId, userID);
                }
            }
        });

        btnSendSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Song song = new Song();
                song.setName("KhaiTran");
                song.setArtist("TinNguyen");
                mSocketIoManager.onAddSong(userID, song);
            }
        });

    }

    public void listenForRoomEvent(Socket socket) {
        socket.on("on-create-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                LogUtils.ApplicationLogI("on-create-room: roomId has been created: " + (String) args[0]);
            }
        }).on("on-join-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                setMediaConrner();

                LogUtils.ApplicationLogI("on-join-room: this user has joined room: " + (String) args[0]);
            }
        }).on("on-song-added", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                LogUtils.ApplicationLogI("on-song-added " + (String) args[0]);
            }
        });
    }

    private void setMediaConrner() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.getM_vThread().onRoomCreate();
                /*Song song = new Song();
                song.setAlbum("Đánh Đổi");
                song.setArtist("Obito");
                song.setCreatedAt("2024-06-08");
                song.setGenre("Rap");
                song.setImageURL("https://storage.googleapis.com/nodejsapp-89f00.appspot.com/images/%C4%90%E1%BA%A7u%20%C4%90%C6%B0%E1%BB%9Dng%20X%C3%B3%20Ch%E1%BB%A3_thumbnail.jpg?GoogleAccessId=firebase-adminsdk-pmygm%40nodejsapp-89f00.iam.gserviceaccount.com&Expires=32503680000&Signature=LbyxSXV4AtiTXp8baLGkG4Z8NZA5I9jMhYsH8Leqdi6A4V7eCzWHB8p9LaiA7wwO%2F4IQTJaYGb85Go2ITyPB15vBKBqHzmQmT77cT3D46fYZ%2BWlyttDWfJhjjX9PdjZF93hsGGrEKVorypotuTXJ1Oc3AV46bUEQGUvs5HPTsYhiWK60CJ2bdss0WD1dxYe1vAFpb3Szu3DLQXB%2BT8MN9ZlB%2Bh8ko9lF76JxiJycNjlLGD5V0AAlXoJgpgrUf5CZjKi14gYF2yEDJRlKdz6ZnifKZsz74LC0ZNJt3VbjGE6grj8em%2FTF5jbzwnzTMJI8lYXxkkfE75atdCUezL%2BakA%3D%3D");
                song.setName("Đầu Đường Xó Chợ");
                song.setSongID("1_Z8hghccx7DZV4pWc0FEzoM8nn6g6T26");
                song.setSongURL("https://drive.google.com/uc?id=1_Z8hghccx7DZV4pWc0FEzoM8nn6g6T26&export=download");
                song.setViews("3249283");
                MediaItemHolder.getInstance().getListSongs().clear();
                MediaItemHolder.getInstance().getListSongs().add(song);
                MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
                MediaItemHolder.getInstance().getMediaController().setMediaItem(mediaItem);*/
            }
        });
    }

}
