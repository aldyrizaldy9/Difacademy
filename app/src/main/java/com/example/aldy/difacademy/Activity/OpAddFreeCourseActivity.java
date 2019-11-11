package com.example.aldy.difacademy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldy.difacademy.JsonApiRetrofit;
import com.example.aldy.difacademy.Model.TagModel;
import com.example.aldy.difacademy.Model.VideoFreeModel;
import com.example.aldy.difacademy.ModelForYoutube.YResponse;
import com.example.aldy.difacademy.YoutubeApiKeyConfig;
import com.example.aldy.difacademy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

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
    Spinner spnTag;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference videoFreeRef = db.collection("VideoFree");

    String tagVideo = "";
    String tagVideoId = "";

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

        spnTag = findViewById(R.id.spn_op_add_free_tags);
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
                    if (!tagVideoId.equals("")){
                        simpan(url);
                    } else {
                        Toast.makeText(OpAddFreeCourseActivity.this, "Mohon Pilih Tag", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadTags() {
        final List<String> tagList = new ArrayList<>();
        final List<TagModel> tagModels = new ArrayList<>();

        tagList.add("Tag");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tagRef = db.collection("Tags");
        tagRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            TagModel tagModel = documentSnapshot.toObject(TagModel.class);
                            tagModel.setTagid(documentSnapshot.getId());

                            tagModels.add(new TagModel(tagModel.getTag(), tagModel.getTagid()));
                            tagList.add(tagModel.getTag());
                        }
                    }
                });

        ArrayAdapter<String> spnArrayAdapterTag = new ArrayAdapter<String>(OpAddFreeCourseActivity.this,
                R.layout.support_simple_spinner_dropdown_item, tagList) {
            @Override
            public boolean isEnabled(int position) {
//                return super.isEnabled(position);
                return true;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                return view;
            }
        };

        spnArrayAdapterTag.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        spnTag.setAdapter(spnArrayAdapterTag);

        spnTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0) {
                    tagVideo = tagModels.get(position).getTag();
                    tagVideoId = tagModels.get(position).getTagid();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void hapus() {

    }

    private void simpan(String url) {
        final String youtubeVideoId = YoutubeApiKeyConfig.getYoutubeVideoId(url);
        if (!youtubeVideoId.equals("")) {
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
                    if (!response.isSuccessful()) {
                        return;
                    }

                    YResponse yResponse = response.body();
//                    Log.d(TAG, "onResponse: " + new GsonBuilder().setPrettyPrinting().create().toJson(response));

                    String title = yResponse.getItems().get(0).getSnippet().getTitle();
                    String description = yResponse.getItems().get(0).getSnippet().getDescription();
                    String thumbDefault = yResponse.getItems().get(0).getSnippet().getThumbnails().getNormal().getUrl();
//                    String thumbMedium = yResponse.getItems().get(0).getSnippet().getThumbnails().getMedium().getUrl();
                    String thumbStandard = yResponse.getItems().get(0).getSnippet().getThumbnails().getStandard().getUrl();
//                    String thumbHigh = yResponse.getItems().get(0).getSnippet().getThumbnails().getHigh().getUrl();
//                    String thumbMaxres = yResponse.getItems().get(0).getSnippet().getThumbnails().getMaxres().getUrl();
                    String tagId = tagVideoId;

                    VideoFreeModel videoFreeModel = new VideoFreeModel(thumbStandard, youtubeVideoId,
                            title, description, tagId);
                    videoFreeRef.add(videoFreeModel)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    onBackPressed();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(OpAddFreeCourseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
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