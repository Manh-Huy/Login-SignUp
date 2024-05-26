package com.example.authenticationuseraccount.utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DataLocalManager {
    private static final String PREF_REMEMBER_ME_ACCOUNT = "PREF_REMEMBER_ME_ACCOUNT";
    private static final String PREF_HISTORY_SEARCH = "PREF_HISTORY_SEARCH";
    private static DataLocalManager instance;
    private MySharedPreferences mySharedPreferences;

    private String historySearchKey;
    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.mySharedPreferences = new MySharedPreferences(context);
    }
    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    private void updateHistorySearchKey() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            historySearchKey = PREF_HISTORY_SEARCH + user.getUid();
        } else {
            historySearchKey = PREF_HISTORY_SEARCH;
        }
    }
    public static void setRememberMeAccount(boolean isRemember) {
        DataLocalManager.getInstance().mySharedPreferences.putBooleanValue(PREF_REMEMBER_ME_ACCOUNT, isRemember);
    }
    public static boolean getRememberMeAccount() {
        return DataLocalManager.getInstance().mySharedPreferences.getBooleanValue(PREF_REMEMBER_ME_ACCOUNT);
    }
    public static void setHistorySearch(String query) {
        DataLocalManager instance = DataLocalManager.getInstance();
        instance.updateHistorySearchKey();

        // Lấy danh sách các mục tìm kiếm hiện có và GÁN CHO NÓ VÀO 1 BIẾN MỚI
        Set<String> searchItems = instance.mySharedPreferences.getStringSetValue(instance.historySearchKey);
        Set<String> updatedSearchItems = new HashSet<>(searchItems);
        updatedSearchItems.add(query); // Thêm mục mới vào danh sách
        instance.mySharedPreferences.putStringSetValue(instance.historySearchKey, updatedSearchItems); // Lưu lại danh sách đã cập nhật
    }
    public static Set<String> getHistorySearch() {
        DataLocalManager instance = DataLocalManager.getInstance();
        instance.updateHistorySearchKey();

        return instance.mySharedPreferences.getStringSetValue(instance.historySearchKey);
    }
    public static void mergeLocalWithAccountHistorySearch() {
        DataLocalManager instance = DataLocalManager.getInstance();
        instance.updateHistorySearchKey();

        if (!Objects.equals(instance.historySearchKey, PREF_HISTORY_SEARCH))
        {
            Set<String> searchLocalItems = instance.mySharedPreferences.getStringSetValue(PREF_HISTORY_SEARCH);
            Set<String> searchItems = instance.mySharedPreferences.getStringSetValue(instance.historySearchKey);

            if (searchLocalItems != null && !searchLocalItems.isEmpty()) {
                if (searchItems == null) {
                    searchItems = new HashSet<>();
                }
                searchItems.addAll(searchLocalItems);
                instance.mySharedPreferences.putStringSetValue(instance.historySearchKey, searchItems);

                // Clear local history after merging
                //instance.mySharedPreferences.putStringSetValue(PREF_HISTORY_SEARCH, new HashSet<>());
            }
        }
    }
}
