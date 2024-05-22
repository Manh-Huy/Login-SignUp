package com.example.authenticationuseraccount.api;

import com.example.authenticationuseraccount.model.Song;
import com.example.authenticationuseraccount.model.User;
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

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    Interceptor interceptor = chain -> {
        try {
            Request request = chain.request();
            Request.Builder builder = request.newBuilder();
            //builder.addHeader("Authorization", "$2y$10$XU3bOvadsf8Ej04v15y8Zf4x6u3uoBR6ijnSnkpZ7yDYIxP9eho2pFVUK");
            builder.addHeader("X-RapidAPI-Key", "da020d06f1msh1114f6a17c51777p1f97a0jsn5dbd05d5fc6f");
            return chain.proceed(builder.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    HttpLoggingInterceptor loggingIntercepter = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
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
}
