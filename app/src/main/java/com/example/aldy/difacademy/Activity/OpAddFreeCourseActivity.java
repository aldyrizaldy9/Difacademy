package com.example.aldy.difacademy.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldy.difacademy.JsonApiRetrofit;
import com.example.aldy.difacademy.ModelForYoutube.YResponse;
import com.example.aldy.difacademy.YoutubeApiKeyConfig;
import com.example.aldy.difacademy.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.aldy.difacademy.YoutubeApiKeyConfig.YOUTUBE_API_BASE_URL;
import static com.example.aldy.difacademy.YoutubeApiKeyConfig.YOUTUBE_API_KEY;

public class OpAddFreeCourseActivity extends AppCompatActivity {
    private static final String TAG = "OpAddFreeCourseActivity";

    TextView tvNavber;
    ConstraintLayout clBack;
    ImageView imgBack;

    Button btnSave, btnHapus;
    EditText edtLink;
    TextView tvTitle, tvDescription;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference videoFreeRef = db.collection("VideoFree");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_free_course);

        initView();
        onClick();
        loadTags();
    }

    private void initView() {
        tvNavber = findViewById(R.id.tv_navbar);
        tvNavber.setText("Video Gratis");
        clBack = findViewById(R.id.cl_icon1);
        clBack.setVisibility(View.VISIBLE);
        imgBack = findViewById(R.id.img_icon1);
        imgBack.setImageResource(R.drawable.ic_arrow_back);
        btnSave = findViewById(R.id.btn_op_add_free_simpan);
        btnHapus = findViewById(R.id.btn_op_add_free_hapus);
        edtLink = findViewById(R.id.edt_op_add_free_link);

        tvTitle = findViewById(R.id.tv_op_add_free_title);
        tvDescription = findViewById(R.id.tv_op_add_free_deskripsi);
    }

    private void onClick() {
        clBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapus();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = edtLink.getText().toString();
                if (url.length() != 0) {
                    simpan(url);
                }
            }
        });
    }

    private void loadTags(){

    }

    private void hapus() {

    }

    private void simpan(String url) {
        final String youtubeVideoId = YoutubeApiKeyConfig.getYoutubeVideoId(url);
        if (!youtubeVideoId.equals("")){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(YOUTUBE_API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            JsonApiRetrofit jsonApiRetrofit = retrofit.create(JsonApiRetrofit.class);

            Call<YResponse> call = jsonApiRetrofit.getYitems("snippet",
                    "items(snippet(title,description,thumbnails))",
                    youtubeVideoId,
                    YOUTUBE_API_KEY);
            call.enqueue(new Callback<YResponse>() {
                @Override
                public void onResponse(Call<YResponse> call, Response<YResponse> response) {
                    if (!response.isSuccessful()){
                        return;
                    }

                    YResponse yResponse = response.body();
                    String title = yResponse.getItems().get(0).getSnippet().getTitle();
                    String description = yResponse.getItems().get(0).getSnippet().getDescription();
                    String thumbDefault = yResponse.getItems().get(0).getSnippet().getThumbnails().getNormal().getUrl();
                    String thumbMedium = yResponse.getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl();
                    String thumbStandard = yResponse.getItems().get(0).getSnippet().getThumbnails().getStandard().getUrl();
                    String thumbHigh = yResponse.getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();
                    String thumbMaxres = yResponse.getItems().get(0).getSnippet().getThumbnails().getMaxres().getUrl();

                    Log.d(TAG, "onResponse: idYoutubeVideo : " + youtubeVideoId);
                    Log.d(TAG, "onResponse: title : " + title);
                    Log.d(TAG, "onResponse: description : " + description);
                    Log.d(TAG, "onResponse: thumbnail default : " + thumbDefault);
                    Log.d(TAG, "onResponse: thumbnail medium : " + thumbMedium);
                    Log.d(TAG, "onResponse: thumbnail standard : " + thumbStandard);
                    Log.d(TAG, "onResponse: thumbnail high : " + thumbHigh);
                    Log.d(TAG, "onResponse: thumbnail maxres : " + thumbMaxres);
                }

                @Override
                public void onFailure(Call<YResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure: " + t.getMessage());
                }
            });
        } else {
            Toast.makeText(this, "Link Tidak Valid", Toast.LENGTH_SHORT).show();
        }
    }
}