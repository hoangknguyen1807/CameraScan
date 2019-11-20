package com.example.camerascan;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadAPIs {
//    @Multipart
//    @POST("/upload")
//    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file, @Part("email") RequestBody email, @Part("password") RequestBody password);
//

    //    @POST("/posts")
//    @FormUrlEncoded
//    Call<POST> savePost(@Field("id") int id,
//                        @Field("email") String email,
//                        @Field("password") String password);
    @Multipart
    @POST("upload")
    Call<FileInfo> upload(@Part MultipartBody.Part file);
}
