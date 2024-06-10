package com.example.authenticationuseraccount.service;

import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.interfaces.SocketEventListener;
import com.example.authenticationuseraccount.model.business.Song;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIoManager {
    private SocketEventListener listener;

    public Socket getmSocket() {
        return mSocket;
    }

    public void setmSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }

    private Socket mSocket;
    private static SocketIoManager instance;
    private Gson gson;

    private SocketIoManager() {
        gson = new Gson();
        try {
            mSocket = IO.socket("https://mobilebackendtestupload.onrender.com/");
            LogUtils.ApplicationLogI("Trying to connect to Server");

            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LogUtils.ApplicationLogI("Connected to server");
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LogUtils.ApplicationLogI("Disconnected from server");
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    LogUtils.ApplicationLogI("Connection Error!");
                }
            });
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static SocketIoManager getInstance() {
        if (instance == null) {
            synchronized (SocketIoManager.class) {
                if (instance == null) {
                    instance = new SocketIoManager();
                }
            }
        }
        return instance;
    }

    public void listenForChatResponse() {
        mSocket.on("user-chat", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                LogUtils.ApplicationLogI("user-chat: " + (String) args[0]);
            }
        });
    }

    public void createRoom(String userId) {
        mSocket.emit("create-room", userId);
    }

    public void joinRoom(String userHostId, String currentUserID) {
        JSONObject data = new JSONObject();
        try {
            data.put("roomId", userHostId);
            data.put("userId", currentUserID);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        mSocket.emit("join-room", data);
    }

    public void onAddSong(String roomId, Song song) {
        String songJson = gson.toJson(song);
        JSONObject data = new JSONObject();
        try {
            data.put("room", roomId);
            data.put("song", songJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //LogUtils.ApplicationLogI("on-add-song: " + data.toString());
        mSocket.emit("on-add-song", data);
    }


    private void handleSocketEvent(String eventName, Object... args) {
        // Handle the socket event
        if (listener != null) {
            switch (eventName) {
                case "on-create-room":
                    if (args.length > 0 && args[0] instanceof String) {
                        listener.onCreateRoom((String) args[0]);
                    }
                    break;
                case "on-join-room":
                    if (args.length > 0 && args[0] instanceof String) {
                        listener.onJoinRoom((String) args[0]);
                    }
                    break;
                case "on-song-added":
                    if (args.length > 0 && args[0] instanceof String) {
                        listener.onSongAdded((String) args[0]);
                    }
                    break;
                // Handle other events if needed
            }
        }
    }

    public void setSocketEventListener(SocketEventListener listener) {
        this.listener = listener;
    }



    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
    }

}
