package com.tamanpelajar.aldy.difacademy.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-type:application/json",
                    "Authorization:key=AAAAwo3Q99A:APA91bFnm9i6Ls9IgzOS7bGEiq58BLU8mPgVX5lNncIAfoxjXDhIDkh_Nbzjj09n01LqzCPNijhApfyXck9jefhfb-4IkOJD83KBdNgPTOeEKbsRRLMiz7lV4g3zeqTsmGEHM0We7qq8"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
