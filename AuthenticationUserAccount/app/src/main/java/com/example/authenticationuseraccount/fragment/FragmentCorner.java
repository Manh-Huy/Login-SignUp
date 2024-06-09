package com.example.authenticationuseraccount.fragment;

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

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
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

    private Button buttonConnect, buttonEmit, btnCreatRoom, btnSendSong;
    private FirebaseAuth mAuth;

    private EditText edtUserId;

    private TextView tvUserId;
    private String userID;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonConnect = view.findViewById(R.id.btn_connect);
        buttonEmit = view.findViewById(R.id.btn_emmit);
        edtUserId = view.findViewById(R.id.edt_enter_room);
        tvUserId = view.findViewById(R.id.tv_user_id);
        btnSendSong = view.findViewById(R.id.btn_send_song);
        btnCreatRoom = view.findViewById(R.id.btn_create_room);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userID = user.getUid();
            tvUserId.setText(userID);
        }

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = String.valueOf(edtUserId.getText());
                socket.emit("joinRoom", roomId);
            }
        });
        btnSendSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Song song = new Song();
                song.setName("KhaiTran");
                song.setArtist("TinNguyen");

                Gson gson = new Gson();
                String songJson = gson.toJson(song);
                JSONObject data = new JSONObject();
                try {
                    data.put("room", userID);
                    data.put("message", songJson);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                LogUtils.ApplicationLogI("on-chat data: " + data.toString());
                socket.emit("on-chat", data);
            }
        });

        btnCreatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    userID = user.getUid();
                    socket.emit("joinRoom", userID);
                    tvUserId.setText(userID);
                } else {
                    ErrorUtils.showError(getContext(), "Please Login To Create Room");
                }
            }
        });


        buttonEmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (socket.connected()) {
                    Gson gson = new Gson();
                    Song song = new Song();
                    song.setSongURL("example song url");
                    song.setSongID("example id");
                    song.setImageURL("example image url");
                    song.setArtist("Khai Nguyen");
                    song.setName("Tin Tran");

                    String songJson = gson.toJson(song);

                    socket.emit("on-chat", songJson);
                } else {

                }
            }
        });
        connectIO();
    }

    private Socket socket;


    private void connectIO() {
        try {
            socket = IO.socket("https://mobilebackendtestupload.onrender.com/");
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LogUtils.ApplicationLogI("Connected to Server");
                    //socket.emit("on-chat", userID);
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LogUtils.ApplicationLogI("Disconnected from Server");
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LogUtils.ApplicationLogI("Connection Error");
                }
            }).on("user-chat", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    /*Gson gson = new Gson();
                    String responseJson = (String) args[0];
                    Song song = gson.fromJson(responseJson, Song.class);
                    LogUtils.ApplicationLogI("user-chat: Server bao TinTran ngoovlz? name: " + song.getName() + " artist: " + song.getArtist());*/
                    LogUtils.ApplicationLogI("user-chat: " + (String) args[0]);
                }
            });


            LogUtils.ApplicationLogI("Trying to connect to Server");
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (socket != null) {
            LogUtils.ApplicationLogI("onPause: Disconnected");
            socket.disconnect();
            socket.off();
        }
    }

    @Override
    public void onStop() {
        if (socket != null) {
            LogUtils.ApplicationLogI("onStop: Disconnected");
            socket.disconnect();
            socket.off();
        }
        super.onStop();
    }
}
