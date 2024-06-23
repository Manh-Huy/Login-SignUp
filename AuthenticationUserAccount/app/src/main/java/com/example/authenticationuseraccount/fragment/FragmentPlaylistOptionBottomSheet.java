package com.example.authenticationuseraccount.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.adapter.ItemPlaylistOptionAdapter;
import com.example.authenticationuseraccount.model.business.Playlist;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class FragmentPlaylistOptionBottomSheet extends BottomSheetDialogFragment {
    private List<Playlist> mListItems;

    public FragmentPlaylistOptionBottomSheet(List<Playlist> mListItems) {
        this.mListItems = mListItems;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_search_option_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rcvSearchOption =view.findViewById(R.id.rcv_search_option);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rcvSearchOption.setLayoutManager(linearLayoutManager);

        ItemPlaylistOptionAdapter itemPlaylistOptionAdapter = new ItemPlaylistOptionAdapter(mListItems);

        rcvSearchOption.setAdapter(itemPlaylistOptionAdapter);

        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rcvSearchOption.addItemDecoration(itemDecoration);

        return bottomSheetDialog;
    }
}
