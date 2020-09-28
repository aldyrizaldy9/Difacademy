package com.tamanpelajar.aldy.difacademy.ActivityAdmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.tamanpelajar.aldy.difacademy.CommonMethod;
import com.tamanpelajar.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import static com.tamanpelajar.aldy.difacademy.ActivityAdmin.OpMainActivity.PHOTO_PICK_REQUEST_CODE;

public class OpBannerActivity extends AppCompatActivity {

    ImageView imgBanner;
    Button btnFile, btnSimpan;
    TextView tvLoading;

    Uri imageUri;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore db;
    String bannerUrl = "";
    ProgressDialog pd;
    boolean thereIsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_banner);

        initView();
        loadBanner();
        onClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICK_REQUEST_CODE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();

            Glide.with(this)
                    .load(imageUri)
                    .into(imgBanner);

            btnSimpan.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        imgBanner = findViewById(R.id.img_op_banner);
        btnFile = findViewById(R.id.btn_op_banner_file);
        btnSimpan = findViewById(R.id.btn_op_banner_upload);
        tvLoading = findViewById(R.id.tv_op_banner_loading);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseStorage.setMaxUploadRetryTimeMillis(60000);
        db = FirebaseFirestore.getInstance();
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Loading...");
    }

    private void loadBanner() {
        DocumentReference doc = db.collection(CommonMethod.refBannerPhotoUrl)
                .document("bannerphotourl");
        doc.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            thereIsData = true;
                            String coba = documentSnapshot.getString("url");
                            bannerUrl = coba;
                            Glide.with(OpBannerActivity.this)
                                    .load(bannerUrl)
                                    .into(imgBanner);
                            tvLoading.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void onClick() {
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonMethod.isInternetAvailable(OpBannerActivity.this)) {
                    return;
                }

                pd.show();
                uploadImageToFirebase();
            }
        });
    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
    }

    private void deleteCurrentImage() {
        final StorageReference deleteRef = firebaseStorage.getReferenceFromUrl(bannerUrl);
        deleteRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        onBackPressed();
                    }
                });
    }

    private void uploadImageToFirebase() {
        final StorageReference ref = firebaseStorage.getReference().child(CommonMethod.storageBannerPhoto + UUID.randomUUID().toString());
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String newBannerUrl = uri.toString();
                                        updateBannerUrl(newBannerUrl);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpBannerActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateBannerUrl(String bannerUrl) {
        HashMap<String, Object> update = new HashMap<>();
        update.put("url", bannerUrl);

        DocumentReference docRef = db.collection(CommonMethod.refBannerPhotoUrl)
                .document("bannerphotourl");
        docRef.set(update)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (thereIsData)
                            deleteCurrentImage();
                    }
                });
    }
}
