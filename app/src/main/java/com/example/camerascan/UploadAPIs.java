package com.example.camerascan;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @POST("file/upload")
    Call<Object> upload(@Part MultipartBody.Part file, @Part("email") RequestBody email, @Part("password") RequestBody password);

    @POST("user/add")
    @FormUrlEncoded
    Call<LoginUserDto> register(@Field("emailRegister") String emailRegister, @Field("nameRegister") String nameRegister, @Field("passwordRegister") String passwordRegister );


    @GET("user/{email}")
    Call<LoginUserDto> login(@Path("email") String email);

    @GET("file/download")
    Call<ArrayList<PhotoDto>> download(@Query("email") String email, @Query("password") String password);

}
