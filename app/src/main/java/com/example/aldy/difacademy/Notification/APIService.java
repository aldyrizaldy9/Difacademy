package com.example.aldy.difacademy.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-type:application/json",
                    "Authorization:key=AAAA2701aTo:APA91bGbxn-emWhd-jA_q6-EJoL1hZvjbke_iiLf2XPdw2g3ZNHtqb9fNWTqtEbfT7P6onvbb7pGjvzFzGMzaXMSIUC4DOYRBgNgE5DcI-64j3Kz1dAitchC8s2LYJs4DP-nxQfD0_Cp"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
