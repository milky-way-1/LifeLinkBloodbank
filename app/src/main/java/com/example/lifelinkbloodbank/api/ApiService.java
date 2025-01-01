package com.example.lifelinkbloodbank.api;

import com.example.lifelinkbloodbank.model.JwtResponse;
import com.example.lifelinkbloodbank.model.LoginRequest;
import com.example.lifelinkbloodbank.model.MessageResponse;
import com.example.lifelinkbloodbank.model.SignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/signup")
    Call<MessageResponse> signup(@Body SignupRequest request);

    @POST("api/auth/login")
    Call<JwtResponse> login(@Body LoginRequest request);


}
