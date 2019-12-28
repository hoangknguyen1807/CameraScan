package com.example.camerascan;

public class APIUtils{
    public APIUtils() {
    }
    //private static final String API_URL = "http://192.168.1.6:8080/api/";
    private static final String API_URL = "http://app-spring-camera.herokuapp.com/api/";
    public static UploadAPIs getFileService(){
        return NetworkClient.getRetrofitClient(API_URL).create(UploadAPIs.class);

    }
}
