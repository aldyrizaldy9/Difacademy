package com.tamanpelajar.aldy.difacademy.Notification;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.tamanpelajar.aldy.difacademy.CommonMethod;

public class MyFirebaseIdService extends FirebaseMessagingService {
    private static final String TAG = "ganteng";
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Log.d(TAG, "MyFirebaseIdService onNewToken: jalan");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser != null) {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        Log.d(TAG, "MyFirebaseIdService updateToken: jalan");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //set tokennya di database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection(CommonMethod.refTokens).document(firebaseUser.getUid());
        Token token = new Token(refreshToken);
        reference.set(token);
    }
}
