package com.example.authenticationuseraccount.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;

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

    private Button buttonConnect;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonConnect = view.findViewById(R.id.btn_connect);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectIO();
                ErrorUtils.showError(getContext(),"Clicke!!!");
            }
        });
    }
    private Socket socket;
    private void connectIO() {
        try {
            socket = IO.socket("https://mobilebackendtestupload.onrender.com/peerjs");
            socket.connect();
            LogUtils.ApplicationLogI("Trying to connect to Server");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                LogUtils.ApplicationLogI("Connected to Server");
                socket.emit("joinRoom", "roomName");
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                LogUtils.ApplicationLogI("Connection Error");
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                LogUtils.ApplicationLogI("Disconnected from Server");
            }
        });

/*        socket.on("songAdded", args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String songName = data.getString("name");
                LogUtils.ApplicationLogI("SongAdded: " + songName);
                // Handle the newly added song
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });*/

        // Other event listeners for playlist updates, playback controls, etc.
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
