package com.example.camerascan;

public class APIUtils{
    public APIUtils() {
    }

    private static final String API_URL = "http://192.168.1.11:8080/api/file/";
    public static UploadAPIs getFileService(){
        return NetworkClient.getRetrofitClient(API_URL).create(UploadAPIs.class);

    }
}
