package com.example.authenticationuseraccount.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;
import com.example.authenticationuseraccount.utils.SocketIoManager;
import com.google.firebase.auth.FirebaseAuth;


public class FragmentCorner extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_corner, container, false);
    }

    private Button btnCreatRoom, btnJoinRoom, btnCopyId;
    private FirebaseAuth mAuth;
    private EditText edtUserId;
    private TextView tvUserId;
    private String userID, userName;
    private Context mContext;

    private FrameLayout frameLayout;

    private FragmentRoom fragmentRoom;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtUserId = view.findViewById(R.id.edt_enter_room);
        tvUserId = view.findViewById(R.id.tv_user_id);
        btnCreatRoom = view.findViewById(R.id.btn_create_room);
        btnJoinRoom = view.findViewById(R.id.btn_connet_room);
        btnCopyId = view.findViewById(R.id.btn_copy_id);
        frameLayout = view.findViewById(R.id.fragment_container);
        mContext = getContext();
        mAuth = FirebaseAuth.getInstance();

        fragmentRoom = new FragmentRoom();

        frameLayout.setVisibility(View.GONE);

        User userSingleTon = User.getInstance();
        if (userSingleTon != null) {
            userID = userSingleTon.getUserID();
            userName = userSingleTon.getUsername();
            tvUserId.setText(userSingleTon.getUserID());
        }

        btnCreatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    if (userSingleTon.getRole().equals(Constants.PREMIUM_USER)) {

                        frameLayout.setVisibility(View.VISIBLE);
                        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragmentRoom);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        SocketIoManager.getInstance().createRoom(userID, userName);

                    } else {
                        ErrorUtils.showError(getContext(), "Please Upgrad To Premium To Continue");
                    }
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
                    if (mAuth.getCurrentUser() != null) {
                        SocketIoManager.getInstance().joinRoom(roomId, userID,userSingleTon.getUsername());
                        //edtUserId.setText("");
                    } else {
                        ErrorUtils.showError(getContext(), "Please Login To Join Room");
                    }

                }
            }
        });

        btnCopyId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToCopy = tvUserId.getText().toString();

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("UserId", textToCopy);
                clipboard.setPrimaryClip(clip);

                ErrorUtils.showError(mContext, "Copied to clipboard");
            }
        });
    }

    public void onRoomJoined(Context context){
        frameLayout.setVisibility(View.VISIBLE);
        // Replace fragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragmentRoom);
        transaction.addToBackStack(null);
        transaction.commit();
        fragmentRoom.onRoomJoined(context);
    }

    private void setMediaConrner() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.getM_vThread().onRoomCreate();*/
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
