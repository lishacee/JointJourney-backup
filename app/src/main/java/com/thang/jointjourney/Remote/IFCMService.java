package com.thang.jointjourney.Remote;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import com.thang.jointjourney.models.FCMResponse;
import com.thang.jointjourney.models.Sender;


public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=YOUR_CLOUD_MESSAGING_KEY"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body Sender body);
}
