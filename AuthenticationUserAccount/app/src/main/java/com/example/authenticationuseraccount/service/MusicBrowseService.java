package com.example.authenticationuseraccount.service;

import androidx.annotation.Nullable;
import androidx.media3.session.MediaLibraryService;
import androidx.media3.session.MediaSession;

public class MusicBrowseService extends MediaLibraryService {
    @Nullable
    @Override
    public MediaLibrarySession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return null;
    }

}
