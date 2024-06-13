package com.example.authenticationuseraccount.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;

import java.util.List;

public class SearchFilterAdapter extends RecyclerView.Adapter<SearchFilterAdapter.ViewHolder> {
    private List<String> filters;
    private int selectedPosition = 0; // Mặc định là chọn "All"


    public SearchFilterAdapter(List<String> filters) {
        this.filters = filters;
    }
    public String getSelectedFilter() {
        return filters.get(selectedPosition);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String filter = filters.get(position);
        holder.filterText.setText(filter);

        if (position == selectedPosition) {
            // Đặt màu và gạch chân cho mục được chọn
            holder.filterText.setTextColor(Color.RED);
            holder.filterText.setPaintFlags(holder.filterText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            // Nếu không phải mục được chọn, đặt màu và gạch chân mặc định
            holder.filterText.setTextColor(Color.BLACK);
            holder.filterText.setPaintFlags(holder.filterText.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selectedPosition); // Cập nhật giao diện cho mục trước đó
                selectedPosition = holder.getAdapterPosition(); // Lưu vị trí của mục được chọn mới
                notifyItemChanged(selectedPosition); // Cập nhật giao diện cho mục mới được chọn
            }
        });
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView filterText;

        public ViewHolder(View itemView) {
            super(itemView);
            filterText = itemView.findViewById(R.id.filter_text_view);
        }
    }

}
