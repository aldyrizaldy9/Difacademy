package com.example.aldy.difacademy;

import com.example.aldy.difacademy.ModelForYoutube.YResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonApiRetrofit {

    @GET(".")
    Call<YResponse> getYitems(@Query("part") String part,
                              @Query("fields") String fields,
                              @Query("id") String id,
                              @Query("key") String key);
}
