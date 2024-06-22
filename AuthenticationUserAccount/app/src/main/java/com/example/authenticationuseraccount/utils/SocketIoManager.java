package com.example.authenticationuseraccount.utils;

import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.Message;
import com.example.authenticationuseraccount.model.SocketUser;
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
                    LogUtils.ApplicationLogI("SocketID: " + mSocket.id());
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
            listenForChatEvent();

            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void listenForRoomEvent() {
        mSocket.on("on-create-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //args = roomID
                onCreateRoom(args);
            }
        }).on("on-join-room", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //args = userName
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
                //args = userName
                onUserJoinRoomBroadCast(args);
            }
        }).on("on-song-added", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onSongAdded(args);
            }
        }).on("on-user-disconnect", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //args = socketID
                onUserLeaveRoom(args);
            }
        });
    }


    private void listenForChatEvent() {
        mSocket.on("on-user-message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //args = message
                onMessageReceived(args);
            }
        });
    }

    public void sendMessage(String roomId, Message message) {
        String messageJson = gson.toJson(message);
        JSONObject data = new JSONObject();
        try {
            data.put("roomID", roomId);
            data.put("message", messageJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        LogUtils.ApplicationLogI("sendMessage: " + data.toString());
        mSocket.emit("user-message", data);
    }

    private void onMessageReceived(Object[] args) {
        try {
            LogUtils.ApplicationLogI("on-user-message " + (String) args[0]);
            String data = (String) args[0];
            JSONObject jsonData = new JSONObject(data);
            Message message = gson.fromJson(jsonData.toString(), Message.class);

            //LogUtils.ApplicationLogI("on-user-message Deserialise name: " + message.getUserName() + " imgUrl: " + message.getImgUrl() + " message: " +message.getMessage() + " time: " + message.getMesssageTimeStamp());

            UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UIThread.getInstance().onMessageReceived(message);
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createRoom(String username) {
        JSONObject data = new JSONObject();
        try {
            data.put("roomID", mSocket.id());
            data.put("userName", username);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        LogUtils.ApplicationLogI("SocketIOManager | createRoom: " + data.toString());
        mSocket.emit("create-room", data);
    }

    private void onCreateRoom(Object[] args) {
        //args = roomID

        //Only host receive this event
        ChillCornerRoomManager.getInstance().setRoomId(args[0].toString());
        LogUtils.ApplicationLogI("SocketIOManager | on-create-room ChillCornerRoomManager.setRoomId: " + args[0].toString());
        ChillCornerRoomManager.getInstance().setCurrentUserId(args[0].toString());
        LogUtils.ApplicationLogI("SocketIOManager | on-create-room ChillCornerRoomManager.setCurrentUserId: " + args[0].toString());
        ChillCornerRoomManager.getInstance().setCreated(true);
        LogUtils.ApplicationLogI("SocketIOManager | on-create-room ChillCornerRoomManager.setCreated(true)");

        UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIThread.getInstance().onRoomCreate(args[0].toString());
            }
        });
    }

    private void onUserJoinRoom(Object[] args) {
        //args = userName

        //Add userName from Server to list
        SocketUser socketUser = new SocketUser(mSocket.id(), args[0].toString());
        ChillCornerRoomManager.getInstance().getListUser().add(socketUser);
        LogUtils.ApplicationLogI("SocketIOManager | on-join-room: this user has joined room: " + (String) args[0]);

        UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIThread.getInstance().onRoomJoined(ChillCornerRoomManager.getInstance().getRoomId());
                ErrorUtils.showError(UIThread.getInstance().getM_vMainActivity(), args[0].toString() + " Has Jumped In! Let's listen together!");
            }
        });
    }

    public void joinRoom(String userHostId, String userName) {
        JSONObject data = new JSONObject();
        try {
            data.put("roomID", userHostId);
            data.put("userName", userName);
            data.put("userID", mSocket.id());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        LogUtils.ApplicationLogI("SocketIOManager | joinRoom: " + data.toString());
        mSocket.emit("join-room", data);

    }

    private void onGetRoomInfo(Object[] args) {
        //args = UserName

        //Chỉ Host mới response
        if (!ChillCornerRoomManager.getInstance().isCurrentUserHost()) {
            LogUtils.ApplicationLogI("SocketIOManager | on-get-room-info: Not host => Not Responding");
            return;
        }

        LogUtils.ApplicationLogI("SocketIOManager | on-get-room-info: Host is Responding: " + (String) args[0] + " " + (String) args[1]);

        String userName = (String) args[0];
        String userID = (String) args[1];
        LogUtils.ApplicationLogI("SocketIOManager | on-get-room-info: User Name: " + userName + ", User ID: " + userID);

        SocketUser socketUser = new SocketUser(userID, userName);
        ChillCornerRoomManager.getInstance().getListUser().add(socketUser);
        String roomInfoJson = gson.toJson(ChillCornerRoomManager.getInstance());

        //data = roomId + ChillCornerRoomManager( List<SocketUser>, List<Song>, CurrentIndex )
        JSONObject data = new JSONObject();
        try {
            data.put("roomID", ChillCornerRoomManager.getInstance().getRoomId());
            data.put("roomInfo", roomInfoJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        LogUtils.ApplicationLogI("SokcetIOManager | on-get-room-info: data: " + data.toString());
        mSocket.emit("respone-room-info", data);

    }

    private void onResponseRoomInfo(Object[] args) {
        //args = ChillCornerRoomManager
        //ChillCornerRoomManager( List<SocketUser>, List<Song>, CurrentIndex, roomID )

        LogUtils.ApplicationLogI("SokcetIOManager | on-response-room-info: " + (String) args[0]);
        try {
            String data = (String) args[0];
            JSONObject jsonData = new JSONObject(data);
            ChillCornerRoomManager roomManager = gson.fromJson(jsonData.toString(), ChillCornerRoomManager.class);

            SocketUser lastSocketUser = roomManager.getListUser().get(roomManager.getListUser().size() - 1);
            LogUtils.ApplicationLogI("SokcetIOManager | on-response-room-info: lastSocketUser: " + lastSocketUser.getSocketID() + " this socketID: " + mSocket.id());
            if (!lastSocketUser.getSocketID().equals(mSocket.id())) {
                LogUtils.ApplicationLogI("SokcetIOManager | on-response-room-info: this user has joined room => just update new user");
                ChillCornerRoomManager.getInstance().setListUser(roomManager.getListUser());
                LogUtils.ApplicationLogI("SocketIOManager | on-response-room-info: setListUser: " + roomManager.getListUser());
                return;
            }

            if (ChillCornerRoomManager.getInstance().getCurrentUserId() == null) {
                ChillCornerRoomManager.getInstance().setCurrentUserId(mSocket.id());
                LogUtils.ApplicationLogI("SocketIOManager | on-response-room-info: setCurrentUserId: " + mSocket.id());
            }

            ChillCornerRoomManager.getInstance().setListUser(roomManager.getListUser());
            LogUtils.ApplicationLogI("SocketIOManager | on-response-room-info: setListUser: " + roomManager.getListUser());
            ChillCornerRoomManager.getInstance().setRoomId(roomManager.getRoomId());
            LogUtils.ApplicationLogI("SocketIOManager | on-response-room-info: setRoomId: " + roomManager.getRoomId());
            ChillCornerRoomManager.getInstance().setCreated(true);
            LogUtils.ApplicationLogI("SocketIOManager | on-response-room-info: ChillCornerRoomManager.setCreated(true)");

            if (roomManager.getListSongs() != null && !roomManager.getListSongs().isEmpty()) {
                ChillCornerRoomManager.getInstance().setListSongs(roomManager.getListSongs());
                LogUtils.ApplicationLogI("SocketIOManager | on-response-room-info: setListSongs: " + roomManager.getListSongs());
            }

            if (roomManager.getCurrentPlaySongIndex() != -1) {
                ChillCornerRoomManager.getInstance().setCurrentPlaySongIndex(roomManager.getCurrentPlaySongIndex());
            }

            LogUtils.ApplicationLogI("SocketIOManager | on-response-room-info Deserialise roomID: " + roomManager.getRoomId() + " NumUsers: " + roomManager.getListUser().size());

            JSONObject data2 = new JSONObject();
            try {
                data2.put("roomID", roomManager.getRoomId());
                data2.put("userName", roomManager.getListUser().get(roomManager.getListUser().size() - 1).getUserName());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            LogUtils.ApplicationLogI("SocketIOManager | user-join-room: " + data2.toString());
            mSocket.emit("user-join-room", data2);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onUserJoinRoomBroadCast(Object[] args) {
        //args = userName

        LogUtils.ApplicationLogI("SocketIOManger | onUserJoinRoomBroadCast: user " + args[0].toString() + " has joined room: " + ChillCornerRoomManager.getInstance().getRoomId());
        UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIThread.getInstance().onRoomJoined(ChillCornerRoomManager.getInstance().getRoomId());
                ErrorUtils.showError(UIThread.getInstance().getM_vMainActivity(), args[0].toString() + " Has Jumped In! Let's listen together!");
            }
        });
    }

    private void onUserLeaveRoom(Object[] args) {
        //args = socketID

        LogUtils.ApplicationLogI("SocketIOManger | on-user-disconnect : user " + args[0].toString() + " has leave room: " + ChillCornerRoomManager.getInstance().getRoomId());
        String userSocketId = (String) args[0];
        String username = "";
        for (SocketUser user : ChillCornerRoomManager.getInstance().getListUser()) {
            if (user.getSocketID().equals(userSocketId)) {
                username = user.getUserName();
                ChillCornerRoomManager.getInstance().getListUser().remove(user);
                break;
            }
        }
        String finalUsername = username;
        UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIThread.getInstance().onRoomJoined(ChillCornerRoomManager.getInstance().getRoomId());
                ErrorUtils.showError(UIThread.getInstance().getM_vMainActivity(), finalUsername + " Has Left The Party! Boooo!");
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
            data.put("roomID", roomId);
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

    public void disconnect() {
        if (mSocket != null) {
            UIThread.getInstance().getM_vMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UIThread.getInstance().onOutRoom();
                }
            });
            mSocket.disconnect();
            mSocket.off();
            mSocket.close();
        }
    }
    public static synchronized void release(){
        instance = null;
    }

}
