package com.tamanpelajar.aldy.difacademy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.firebase.Timestamp;

public class CommonMethod {

    public final static String refUser = "User";
    public final static String refBannerPhotoUrl = "BannerPhotoUrl";
    public final static String refPaymentKelasBlended = "PaymentBlended-dev";
    public final static String refPaymentMateriOnline = "PaymentOnline-dev";
    public final static String refGraduationOnline = "GraduationOnline-dev";
    public final static String refGraduationBlended = "GraduationBlended-dev";
    public final static String refTags = "Tags-dev";
    public final static String refTokens = "Tokens";
    public final static String refOngoingKelasBlended = "OngoingKelasBlended-dev";
    public final static String refOngoingMateriOnline = "OngoingMateriOnline-dev";
    public final static String refAnggota = "Anggota-dev";

    public final static String refKelasBlended = "KelasBlended-dev";
    public final static String refMateriBlended = "MateriBlended-dev";
    public final static String refSoalBlended = "SoalBlended-dev";
    public final static String refVideoBlended = "VideoBlended-dev";

    public final static String refKelasOnline = "KelasOnline-dev";
    public final static String refMateriOnline = "MateriOnline-dev";
    public final static String refVideoOnline = "VideoOnline-dev";
    public final static String refSoalOnline = "SoalOnline-dev";

    public final static String refNews = "News-dev";

    public final static String intentUserDocId = "UserDocId";

    public final static String intentKelasBlendedModel = "KelasBlendedModel";
    public final static String intentMateriBlendedModel = "MateriBlendedModel";
    public final static String intentVideoBlendedModel = "VideoBlendedModel";
    public final static String intentSoalBlendedModel = "SoalBlendedModel";

    public final static String intentKelasOnlineModel = "KelasOnlineModel";
    public final static String intentMateriOnlineModel = "MateriOnlineModel";
    public final static String intentVideoOnlineModel = "VideoOnlineModel";
    public final static String intentSoalOnlineModel = "SoalOnlineModel";

    public final static String intentVideoFreeModel = "VideoFreeModel";
    public final static String intentIndex = "Index";
    public final static String intentNewsModel = "NewsModel";
    public final static String intentVideoModel = "VideoModel";
    public final static String intentPaymentModel = "PaymentModel";
    public final static String intentGraduationModel = "GraduationModel";
    public final static String intentFromNotification = "FromNotification";

    public final static String fieldDateCreated = "dateCreated";
    public final static String fieldUserId = "userId";
    public final static String fieldKelasId = "kelasId";
    public final static String fieldMateriId = "materiId";

    public final static String storageBlendedKelas = "BlendedKelas-dev/";
    public final static String storageBlendedMateri = "BlendedMateri-dev/";
    public final static String storageBlendedVideo = "BlendedVideo-dev/";
    public final static String storageOnlineKelas = "OnlineKelas-dev/";
    public final static String storageOnlineMateri = "OnlineMateri-dev/";
    public final static String storageOnlineVideo = "OnlineVideo-dev/";
    public final static String storageBannerPhoto = "BannerPhoto-dev/";
    public final static String storageNews = "NewsPhoto-dev/";

    public final static int paginationMaxLoad = 30;
    public final static int paginationLoadNewData = 15;

    public static long getTimeStamp() {
        return Timestamp.now().getSeconds();
    }

    public static boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                return true;
            } else {
                Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
