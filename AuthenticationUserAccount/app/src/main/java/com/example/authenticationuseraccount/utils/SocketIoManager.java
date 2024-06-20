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

    public void listenForRoomEvent() {
        mSocket.on("on-create-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onCreateRoom(args);
            }
        }).on("on-join-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onUserJoinRoom(args);
            }
        }).on("on-get-room-info", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //args = client UserName
                onGetRoomInfo(args);
            }
        }).on("on-respone-room-info", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //args = ChillCornerRoomManager
                onResponseRoomInfo(args);
            }
        }).on("on-user-join-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onUserJoinRoomBroadCast(args);
            }
        }).on("on-song-added", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onSongAdded(args);
            }
        });
    }

    public void createRoom(String userId, String username) {
        JSONObject data = new JSONObject();
        try {
            data.put("roomID", userId);
            data.put("userName", username);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        mSocket.emit("create-room", data);
    }

    public void joinRoom(String userHostId, String currentUserID, String userName) {
        JSONObject data = new JSONObject();
        try {
            data.put("roomID", userHostId);
            data.put("userName", userName);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        mSocket.emit("join-room", data);

        if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
            ChillCornerRoomManager.getInstance().setCurrentUserId(currentUserID);
            LogUtils.ApplicationLogI("joinRoom setCurrentUserId: " + currentUserID);
        }
        ChillCornerRoomManager.getInstance().setRoomId(userHostId);
        LogUtils.ApplicationLogI("joinRoom setRoomId: " + userHostId);

    }

    private void onCreateRoom(Object[] args) {
        //Only host receive this event
        ChillCornerRoomManager.getInstance().setRoomId(args[0].toString());
        LogUtils.ApplicationLogI("on-create-room setRoomId: " + args[0].toString());
        ChillCornerRoomManager.getInstance().setCurrentUserId(args[0].toString());
        LogUtils.ApplicationLogI("on-create-room setCurrentUserId: " + args[0].toString());
        ChillCornerRoomManager.getInstance().setCreated(true);
        LogUtils.ApplicationLogI("on-create-room: roomId has been created: " + (String) args[0]);

        UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIThread.getInstance().onRoomCreate();
            }
        });
    }

    private void onGetRoomInfo(Object[] args) {
        LogUtils.ApplicationLogI("onGetRoomInfo called");
        //Chỉ Host mới response
        if (!ChillCornerRoomManager.getInstance().isCurrentUserHost())
            return;

        //args = client UserName
        ChillCornerRoomManager.getInstance().getListUser().add(args[0].toString());
        String roomInfoJson = gson.toJson(ChillCornerRoomManager.getInstance());

        //data = roomId + ChillCornerRoomManager( List<User>, List<Song>, CurrentIndex )
        JSONObject data = new JSONObject();
        try {
            data.put("roomID", ChillCornerRoomManager.getInstance().getRoomId());
            data.put("roomInfo", roomInfoJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        LogUtils.ApplicationLogI("onGetRoomInfo: " + data.toString());
        mSocket.emit("respone-room-info", data);
    }

    private void onResponseRoomInfo(Object[] args) {
        //args = ChillCornerRoomManager
        try {
            LogUtils.ApplicationLogI("onResponseRoomInfo " + (String) args[0]);
            String data = (String) args[0];
            JSONObject jsonData = new JSONObject(data);
            ChillCornerRoomManager roomManager = gson.fromJson(jsonData.toString(), ChillCornerRoomManager.class);
            //ChillCornerRoomManager( List<User>, List<Song>, CurrentIndex, roomID )

            ChillCornerRoomManager.getInstance().setListUser(roomManager.getListUser());
            ChillCornerRoomManager.getInstance().setRoomId(roomManager.getRoomId());
            ChillCornerRoomManager.getInstance().setCreated(true);
            if (roomManager.getListSongs() != null && !roomManager.getListSongs().isEmpty()) {
                ChillCornerRoomManager.getInstance().setListSongs(roomManager.getListSongs());
            }

            LogUtils.ApplicationLogI("onResponseRoomInfo Deserialise roomID: " + roomManager.getRoomId() + " NumUsers: " + roomManager.getListUser().size());

            JSONObject data2 = new JSONObject();
            try {
                data2.put("roomID", roomManager.getRoomId());
                data2.put("userName", roomManager.getListUser().get(roomManager.getListUser().size() - 1));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            mSocket.emit("user-join-room", data2);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onUserJoinRoom(Object[] args) {
        //Add userName from Server to list
        ChillCornerRoomManager.getInstance().getListUser().add(args[0].toString());
        LogUtils.ApplicationLogI("on-join-room: this user has joined room: " + (String) args[0]);

        UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIThread.getInstance().onRoomJoined();
                ErrorUtils.showError(UIThread.getInstance().getM_vMainActivity(), args[0].toString() + " Has Jumped In! Let's listen together!");
            }
        });
    }

    private void onUserJoinRoomBroadCast(Object[] args){

        LogUtils.ApplicationLogI("onUserJoinRoomBroadCast: user " + args[0].toString() +" has joined room: " + ChillCornerRoomManager.getInstance().getRoomId());
        UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIThread.getInstance().onRoomJoined();
                ErrorUtils.showError(UIThread.getInstance().getM_vMainActivity(), args[0].toString() + " Has Jumped In! Let's listen together!");
            }
        });
    }

    private void onSongAdded(Object[] args) {
        try {
            LogUtils.ApplicationLogI("on-song-added " + (String) args[0]);
            String data = (String) args[0];
            JSONObject jsonData = new JSONObject(data);
            Song song = gson.fromJson(jsonData.toString(), Song.class);

            LogUtils.ApplicationLogI("on-song-added Deserialise name: " + song.getName() + " artist: " + song.getArtist());
            //ChillCornerRoomManager.getInstance().setCurrentPlaySong(song);

            UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MediaItemHolder.getInstance().setMediaItem(song);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
    }

}
