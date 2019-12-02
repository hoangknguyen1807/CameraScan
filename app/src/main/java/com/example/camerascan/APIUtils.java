package com.example.camerascan;

public class APIUtils{
    public APIUtils() {
    }

    private static final String API_URL = "http://172.16.4.79:8080/api/";
    public static UploadAPIs getFileService(){
        return NetworkClient.getRetrofitClient(API_URL).create(UploadAPIs.class);

    }
}
