package com.example.authenticationuseraccount.api;

import com.example.authenticationuseraccount.model.ListenHistory;

import com.example.authenticationuseraccount.model.business.Song;
import com.example.authenticationuseraccount.model.business.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    Interceptor interceptor = chain -> {
        try {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            return chain.proceed(builder.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    HttpLoggingInterceptor loggingIntercepter = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
            .readTimeout(7, TimeUnit.SECONDS)
            .connectTimeout(7, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            //.addInterceptor(interceptor)
            .addInterceptor(loggingIntercepter);

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://mobilebackendtestupload.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okBuilder.build())
            .build()
            .create(ApiService.class);

    @GET("users")
    Call<List<User>> callTestAPI();

    @GET("songs")
    Observable<List<Song>> getSongs();

    @POST("users")
    Call<User> addUser(@Body User user);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") String userID);

    @GET("users")
    Observable<List<User>> getUsers();

    @GET("history/{id}")
    Observable<List<ListenHistory>> getUserListenHistory(@Path("id") String userID);
}
