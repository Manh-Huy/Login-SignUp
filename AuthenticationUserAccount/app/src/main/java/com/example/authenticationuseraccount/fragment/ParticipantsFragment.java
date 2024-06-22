package com.example.authenticationuseraccount.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.ParticipantAdapter;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.SocketUser;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;

import java.util.List;

public class ParticipantsFragment extends Fragment {

    private RecyclerView recyclerViewParticipant;
    private ParticipantAdapter participantAdapter;

    private TextView idInput;
    private Button copyButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_participants,container,false);

        recyclerViewParticipant = view.findViewById(R.id.rcv_participant);
        recyclerViewParticipant.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        participantAdapter = new ParticipantAdapter(getContext(), ChillCornerRoomManager.getInstance().getListUser());
        List<SocketUser> userList = ChillCornerRoomManager.getInstance().getListUser();
        participantAdapter.setData(userList);

        recyclerViewParticipant.setAdapter(participantAdapter);

        idInput = view.findViewById(R.id.id_input);

        copyButton = view.findViewById(R.id.copy_button);
        copyButton.setOnClickListener(v -> {
            String textToCopy = idInput.getText().toString();
            if (!textToCopy.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", textToCopy);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Nothing to copy", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }


    public void onRoomJoined(Context context, String roomID) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                List<SocketUser> userList = ChillCornerRoomManager.getInstance().getListUser();
                if (userList != null && !userList.isEmpty()) {
                    participantAdapter.setData(userList);
                    idInput.setText(roomID);
                    LogUtils.ApplicationLogE("users: " + userList.size() + " roomId: " + roomID);
                } else {
                    LogUtils.ApplicationLogE("User list is empty or null");
                }
            }
        }, 300); // 2000 milliseconds = 2 seconds
    }

    public void onRoomCreate(Context context, String roomID) {
        LogUtils.ApplicationLogI("Fragment Participant onRoomCreate: " + roomID);
        idInput.setText(roomID);
    }
}
