package com.tamanpelajar.aldy.difacademy.Notification;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        if (firebaseUser != null) {
            updateToken(refreshToken);
        }
    }

    private void updateToken(String refreshToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //set tokennya di database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("Tokens").document(firebaseUser.getUid());
        Token token = new Token(refreshToken);
        reference.set(token);

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token = new Token(refreshToken);
//        reference.child(firebaseUser.getUid()).setValue(token);
    }
}
