package com.example.authenticationuseraccount.utils;

import android.content.Context;

import com.example.authenticationuseraccount.model.ListenHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DataLocalManager {
    private static final String PREF_REMEMBER_ME_ACCOUNT = "PREF_REMEMBER_ME_ACCOUNT";
    private static final String PREF_HISTORY_SEARCH = "PREF_HISTORY_SEARCH";
    private static final String PREF_LISTEN_HISTORY = "PREF_LISTEN_HISTORY";
    private static final String PREF_NAME_ALL_INFO_SONG = "PREF_NAME_ALL_INFO_SONG";
    private static DataLocalManager instance;
    private MySharedPreferences mySharedPreferences;

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
    public static void setRememberMeAccount(boolean isRemember) {
        DataLocalManager.getInstance().mySharedPreferences.putBooleanValue(PREF_REMEMBER_ME_ACCOUNT, isRemember);
    }
    public static boolean getRememberMeAccount() {
        return DataLocalManager.getInstance().mySharedPreferences.getBooleanValue(PREF_REMEMBER_ME_ACCOUNT);
    }
    public static void setHistorySearch(String query) {
        // Lấy danh sách các mục tìm kiếm hiện có và GÁN CHO NÓ VÀO 1 BIẾN MỚI
        Set<String> searchItems = instance.mySharedPreferences.getStringSetValue(PREF_HISTORY_SEARCH);
        Set<String> updatedSearchItems = new HashSet<>(searchItems);
        updatedSearchItems.add(query); // Thêm mục mới vào danh sách
        instance.mySharedPreferences.putStringSetValue(PREF_HISTORY_SEARCH, updatedSearchItems); // Lưu lại danh sách đã cập nhật
    }
    public static Set<String> getHistorySearch() {
        return instance.mySharedPreferences.getStringSetValue(PREF_HISTORY_SEARCH);
    }

    public static void setListenHistory(ListenHistory listenHistory) {
        // Lấy list ListenHistory hiện tại
        List<ListenHistory> listListenHistory = DataLocalManager.getListenHistory();
        List<ListenHistory> updatedListListenHistory = new ArrayList<>(listListenHistory);

        boolean isExisting = false;
        // Duyệt qua danh sách để kiểm tra xem listenHistory đã tồn tại chưa
        for (ListenHistory history : updatedListListenHistory) {
            if (history.getSongID().equals(listenHistory.getSongID())) {
                // Nếu tồn tại, cập nhật giá trị count và lastListen
                history.setCount(history.getCount() + 1);
                history.setLove(listenHistory.isLove());
                history.setLastListen(listenHistory.getLastListen());
                isExisting = true;
                break;
            }
        }

        if (!isExisting) {
            // Nếu không tồn tại, thêm vào danh sách
            updatedListListenHistory.add(listenHistory); // Thêm lịch sử mới vào danh sách
        }

        Gson gson = new Gson();
        JsonArray jsonArray = gson.toJsonTree(updatedListListenHistory).getAsJsonArray();
        String strJsonArray = jsonArray.toString();
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_LISTEN_HISTORY, strJsonArray);
    }

    public static List<ListenHistory> getListenHistory() {
        String strJsonArray = DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_LISTEN_HISTORY);
        List<ListenHistory> listListenHistory = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(strJsonArray);
            JSONObject jsonObject;
            ListenHistory listenHistory;
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                listenHistory = gson.fromJson(jsonObject.toString(), ListenHistory.class);
                listListenHistory.add(listenHistory);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return listListenHistory;
    }

    public static void setNameAllInfoSong(Set<String> songInfo) {
        DataLocalManager.getInstance().mySharedPreferences.putStringSetValue(PREF_NAME_ALL_INFO_SONG, songInfo);
    }
    public static Set<String> getNameAllInfoSong() {
        return DataLocalManager.getInstance().mySharedPreferences.getStringSetValue(PREF_NAME_ALL_INFO_SONG);
    }
}
