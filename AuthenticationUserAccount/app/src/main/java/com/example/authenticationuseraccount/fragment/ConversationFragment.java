package com.example.authenticationuseraccount.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.MessageAdapter;
import com.example.authenticationuseraccount.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ConversationFragment extends Fragment {

    private EditText edtMessage;
    private Button btnSend;
    private RecyclerView rcvMessage;
    private MessageAdapter messageAdapter;
    private List<Message> mListMessage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        edtMessage = view.findViewById(R.id.edt_message);
        btnSend = view.findViewById(R.id.btn_send);
        rcvMessage = view.findViewById(R.id.rcv_message);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvMessage.setLayoutManager(linearLayoutManager);

        mListMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter();
        messageAdapter.setData(mListMessage);

        rcvMessage.setAdapter(messageAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        edtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkKeyBoard();
            }
        });
    }
    private void sendMessage() {
        String strMessage = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }
        mListMessage.add(new Message(strMessage));
        messageAdapter.notifyDataSetChanged();
        rcvMessage.scrollToPosition(mListMessage.size() - 1);

        edtMessage.setText("");
    }

    private void checkKeyBoard() {
        final View activityRootView = getActivity().findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                // r will be populated with the coordinates of your view that still visible
                activityRootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = activityRootView.getRootView().getHeight() - r.height();
                if (heightDiff > 0.25*activityRootView.getRootView().getHeight()) {
                    // key board display
                    if (mListMessage.size() > 0) {
                        rcvMessage.scrollToPosition(mListMessage.size() - 1);
                        activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }
}