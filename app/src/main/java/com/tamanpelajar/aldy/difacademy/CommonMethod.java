package com.tamanpelajar.aldy.difacademy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.Timestamp;

public class CommonMethod {

    public final static String refUser = "User";
    public final static String refKelasBlended = "KelasBlended";
    public final static String refKelasOnline = "KelasOnline";
    public final static String refBannerPhotoUrl = "BannerPhotoUrl";
    public final static String refPaymentKelasBlended = "PaymentBlended";
    public final static String refPaymentKelasOnline = "PaymentOnline";
    public final static String refTags = "Tags";
    public final static String refTokens = "Tokens";
    public final static String refOngoingKelasBlended = "OngoingKelasBlended";
    public final static String refOngoingKelasOnline = "OngoingKelasOnline";
    public final static String refMember = "Member";
    public final static String refMateriBlended = "MateriBlended";
    public final static String refMateriOnline = "MateriOnline";
    public final static String refVideoBlended = "VideoBlended";
    public final static String refVideoOnline = "VideoOnline";

    public static long getTimeStamp(){
        return Timestamp.now().getSeconds();
    }

    public static boolean isInternetAvailable(Context context){
        try {
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e){
            return false;
        }
    }
}
