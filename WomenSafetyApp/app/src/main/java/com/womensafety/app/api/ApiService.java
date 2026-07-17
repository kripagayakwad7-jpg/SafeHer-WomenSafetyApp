package com.womensafety.app.api;

import com.womensafety.app.models.ApiResponse;
import com.womensafety.app.models.AuthResponse;
import com.womensafety.app.models.Contact;
import com.womensafety.app.models.SOS;
import com.womensafety.app.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/register")
    Call<ApiResponse<AuthResponse>> register(@Body Map<String, String> body);

    @POST("api/login")
    Call<ApiResponse<AuthResponse>> login(@Body Map<String, String> body);

    @GET("api/profile/{userId}")
    Call<ApiResponse<User>> getProfile(@Path("userId") Long userId);

    @PUT("api/profile/{userId}")
    Call<ApiResponse<User>> updateProfile(@Path("userId") Long userId, @Body Map<String, String> body);

    @POST("api/contacts")
    Call<ApiResponse<Contact>> addContact(@Body Map<String, Object> body);

    @GET("api/contacts/{userId}")
    Call<ApiResponse<List<Contact>>> getContacts(@Path("userId") Long userId);

    @DELETE("api/contacts/{contactId}")
    Call<ApiResponse<Object>> deleteContact(@Path("contactId") Long contactId);

    @POST("api/sos")
    Call<ApiResponse<SOS>> triggerSOS(@Body Map<String, Object> body);

    @GET("api/sos/history/{userId}")
    Call<ApiResponse<List<SOS>>> getSOSHistory(@Path("userId") Long userId);
}
