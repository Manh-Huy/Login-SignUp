package com.example.authenticationuseraccount.interfaces;

public interface SocketEventListener {
    void onCreateRoom(String roomId);
    void onJoinRoom(String userId);
    void onSongAdded(String song);
}

