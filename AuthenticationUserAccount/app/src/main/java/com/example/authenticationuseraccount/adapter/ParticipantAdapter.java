package com.example.authenticationuseraccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.example.authenticationuseraccount.model.SocketUser;
import com.example.authenticationuseraccount.utils.ChillCornerRoomManager;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ViewHolder> {

    private List<SocketUser> userNames;
    private Context mContext;

    public ParticipantAdapter(Context context, List<SocketUser> userNames) {
        this.mContext = context;
        this.userNames = userNames != null ? userNames : new ArrayList<>();
    }

    public void setData(List<SocketUser> userNames) {
        this.userNames = userNames != null ? userNames : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String username = userNames.get(position).getUserName();
        LogUtils.ApplicationLogE("ParticipantAdapter " + "Binding user at position: " + position + " - " + username);
        holder.tvUserName.setText(username);
        if(ChillCornerRoomManager.getInstance().isCurrentUserHost()){
            holder.btnKick.setOnClickListener(v -> {
                ErrorUtils.showError(mContext, "User " + username + " kicked");
            });
        }else{
            holder.btnKick.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return userNames.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUserName;
        private final Button btnKick;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_participant_name);
            btnKick = itemView.findViewById(R.id.btn_kick);
        }
    }
}
