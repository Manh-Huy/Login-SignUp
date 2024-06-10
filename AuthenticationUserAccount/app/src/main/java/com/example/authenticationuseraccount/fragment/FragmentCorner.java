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

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.service.SocketIoManager;
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
        mSocketIoManager.listenForRoomEvent();
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

}
