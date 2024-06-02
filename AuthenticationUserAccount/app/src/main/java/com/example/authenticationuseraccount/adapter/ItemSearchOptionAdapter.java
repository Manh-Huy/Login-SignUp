package com.example.authenticationuseraccount.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.model.IClickSearchOptionItemListener;
import com.example.authenticationuseraccount.model.ItemSearchOption;

import java.util.List;

public class ItemSearchOptionAdapter extends RecyclerView.Adapter<ItemSearchOptionAdapter.ItemSearchOptionViewHolder> {
    private List<ItemSearchOption> mListItems;
    private IClickSearchOptionItemListener iClickSearchOptionItemListener;

    public ItemSearchOptionAdapter(List<ItemSearchOption> mListItems, IClickSearchOptionItemListener iClickSearchOptionItemListener) {
        this.mListItems = mListItems;
        this.iClickSearchOptionItemListener = iClickSearchOptionItemListener;
    }

    @NonNull
    @Override
    public ItemSearchOptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_option, parent, false);
        return new ItemSearchOptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemSearchOptionViewHolder holder, int position) {
        ItemSearchOption itemSearchOption = mListItems.get(position);
        if (itemSearchOption == null) {
            return;
        }
        holder.icon.setImageResource(itemSearchOption.getIconResId());
        holder.text.setText(itemSearchOption.getText());
        holder.itemSearchOptionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickSearchOptionItemListener.clickSearchOptionItem(itemSearchOption);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mListItems != null) {
            return mListItems.size();
        }
        return 0;
    }

    public class ItemSearchOptionViewHolder extends RecyclerView.ViewHolder {
    private ImageView icon;
    private TextView text;
    private LinearLayout itemSearchOptionLayout;
    public ItemSearchOptionViewHolder(@NonNull View itemView) {
        super(itemView);
        icon = itemView.findViewById(R.id.item_icon);
        text = itemView.findViewById(R.id.item_text);
        itemSearchOptionLayout = itemView.findViewById(R.id.layout_search_item_option);
    }
}
}
