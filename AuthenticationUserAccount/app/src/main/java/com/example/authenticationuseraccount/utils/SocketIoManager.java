package com.example.authenticationuseraccount.utils;

import android.util.Log;

import androidx.media3.common.MediaItem;

import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.service.MediaItemHolder;
import com.example.authenticationuseraccount.service.UIThread;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIoManager {

    public Socket getmSocket() {
        return mSocket;
    }


    private Socket mSocket;
    private static SocketIoManager instance;
    private Gson gson;

    private SocketIoManager() {
        gson = new Gson();

        ChillCornerRoomManager.getInstance();

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
            listenForRoomEvent();
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

    public void listenForRoomEvent() {
        mSocket.on("on-create-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //Only host receive this event
                LogUtils.ApplicationLogI("setRoomId: " + args[0].toString());
                ChillCornerRoomManager.getInstance().setRoomId(args[0].toString());
                ChillCornerRoomManager.getInstance().setCreated(true);
                UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UIThread.getInstance().onRoomCreate();
                        ErrorUtils.showError(UIThread.getInstance().getM_vMainActivity(),"Room Created! Let's add a song");
                    }
                });
                LogUtils.ApplicationLogI("on-create-room: roomId has been created: " + (String) args[0]);
            }
        }).on("on-join-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //Add useriD from Server
                if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
                    ChillCornerRoomManager.getInstance().setCurrentUserId(args[0].toString());
                    LogUtils.ApplicationLogI("setCurrentUserId: " + args[0].toString());
                }
                //Add useriD from Server to list
                ChillCornerRoomManager.getInstance().getListUser().add(args[0].toString());
                LogUtils.ApplicationLogI("on-join-room: this user has joined room: " + (String) args[0]);

                //Open Ui for guest
                if(!ChillCornerRoomManager.getInstance().isCurrentUserHost()){
                    UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UIThread.getInstance().onRoomCreate();
                            ErrorUtils.showError(UIThread.getInstance().getM_vMainActivity(),"Room Joined! Let's listen together!");
                        }
                    });
                }

            }
        }).on("on-song-added", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    LogUtils.ApplicationLogI("on-song-added " + (String) args[0]);
                    String data = (String) args[0];
                    JSONObject jsonData = new JSONObject(data);
                    Song song = gson.fromJson(jsonData.toString(), Song.class);


                    LogUtils.ApplicationLogI("on-song-added Deserialise name: " + song.getName() + " artist: " + song.getArtist());
                    ChillCornerRoomManager.getInstance().setCurrentPlaySong(song);

                    UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MediaItem mediaItem = MediaItem.fromUri(song.getSongURL());
                            MediaItemHolder.getInstance().getMediaController().setMediaItem(mediaItem);
                            MediaItemHolder.getInstance().getListSongs().clear();
                            MediaItemHolder.getInstance().getListSongs().add(song);
                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
    }

}
