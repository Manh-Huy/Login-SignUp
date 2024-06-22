package com.example.authenticationuseraccount.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.Constants;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.Message;
import com.example.authenticationuseraccount.model.business.User;
import com.example.authenticationuseraccount.utils.SocketIoManager;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.firebase.auth.FirebaseAuth;


public class FragmentCorner extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_corner, container, false);
        mProgressBar = view.findViewById(R.id.corner_progress_bar);
        mProgressBar.setIndeterminateDrawable(new Wave());
        mProgressBar.setVisibility(View.INVISIBLE);
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
                        mProgressBar.setVisibility(View.VISIBLE);
                        Handler handler = new Handler();
                        Runnable checkConnectionRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (SocketIoManager.getInstance().getmSocket().id() == null) {
                                    LogUtils.ApplicationLogI("Waiting for connection!");
                                    handler.postDelayed(this, 100); // Check again after 100 milliseconds
                                } else {
                                    // Hide the ProgressBar and create the room on the UI thread
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            SocketIoManager.getInstance().createRoom(userName);
                                        }
                                    });
                                }
                            }
                        };
                        handler.post(checkConnectionRunnable);
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

                        mProgressBar.setVisibility(View.VISIBLE);
                        Handler handler = new Handler();
                        Runnable checkConnectionRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (SocketIoManager.getInstance().getmSocket().id() == null) {
                                    LogUtils.ApplicationLogI("Waiting for connection!");
                                    handler.postDelayed(this, 100); // Check again after 100 milliseconds
                                } else {
                                    // Hide the ProgressBar and create the room on the UI thread
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                            SocketIoManager.getInstance().joinRoom(roomId,userName);
                                        }
                                    });
                                }
                            }
                        };
                        handler.post(checkConnectionRunnable);


                        //SocketIoManager.getInstance().joinRoom(roomId, userID, userSingleTon.getUsername());
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

        return view;
    }

    private Button btnCreatRoom, btnJoinRoom, btnCopyId;
    private FirebaseAuth mAuth;
    private EditText edtUserId;
    private TextView tvUserId;
    private String userID, userName;
    private Context mContext;

    private FrameLayout frameLayout;

    private FragmentRoom fragmentRoom;
    private ProgressBar mProgressBar;

    public void onRoomJoined(Context context, String roomId) {
        frameLayout.setVisibility(View.VISIBLE);
        // Replace fragment
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragmentRoom);
        transaction.addToBackStack(null);
        transaction.commit();
        fragmentRoom.onRoomJoined(context, roomId);
    }

    public void onMessageReceived(Context mContext, Message message) {
        fragmentRoom.onMessageReceived(mContext, message);
    }

    public void onRoomCreate(Context context, String roomID) {
        LogUtils.ApplicationLogI("FragmentCorner | onRoomCreate");
        frameLayout.setVisibility(View.VISIBLE);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragmentRoom);
        transaction.addToBackStack(null);
        transaction.commit();
        fragmentRoom.onRoomCreate(context, roomID);
    }
}
