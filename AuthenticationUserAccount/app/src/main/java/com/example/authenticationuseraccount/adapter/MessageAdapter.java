package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> mListMessage;
    private Context mContext;

    public MessageAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Message> list) {
        this.mListMessage = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = mListMessage.get(position);
        if (message == null) {
            return;
        }
        holder.tvMessage.setText(message.getMessage());
        holder.tvMessageTimeStamp.setText(message.getMesssageTimeStamp());
        holder.tvUserName.setText(message.getUserName());
        Glide.with(mContext)
                .load(message.getImgUrl())
                .into(holder.imgUserAva);
    }

    @Override
    public int getItemCount() {
        if (mListMessage != null) {
            return mListMessage.size();
        }
        return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage;
        private TextView tvMessageTimeStamp;
        private TextView tvUserName;
        private ImageView imgUserAva;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvUserName = itemView.findViewById(R.id.tv_chat_username);
            tvMessageTimeStamp = itemView.findViewById(R.id.tv_message_timestamp);
            imgUserAva = itemView.findViewById(R.id.img_message_ava);
        }
    }
}
