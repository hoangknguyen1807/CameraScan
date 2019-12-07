package com.example.camerascan;

public class APIUtils{
    public APIUtils() {
    }

    private static final String API_URL = "http://172.16.6.102:8080/api/";
    public static UploadAPIs getFileService(){
        return NetworkClient.getRetrofitClient(API_URL).create(UploadAPIs.class);

    }
}
