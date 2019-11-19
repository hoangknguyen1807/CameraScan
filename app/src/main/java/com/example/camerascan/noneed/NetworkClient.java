package com.example.camerascan.noneed;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    //private static final String BASE_URL = "http://localhost:8080/api/file/";
    private static final String BASE_URL = "http://127.0.0.1:8080/api/file/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitClient(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.header("Content-Type", "application/json");
                    requestBuilder.header("Accept", "application/json");
                    return chain.proceed(requestBuilder.build());
                }
            });
            OkHttpClient httpClient = httpClientBuilder.build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;

    }
}