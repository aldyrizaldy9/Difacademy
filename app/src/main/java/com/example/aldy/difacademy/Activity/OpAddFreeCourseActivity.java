package com.example.aldy.difacademy.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.model.value.ServerTimestampValue;
import com.google.firestore.v1.DocumentTransform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    boolean thereIsData = false;
    VideoFreeModel videoFreeModelIntent;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_op_add_free_course);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");

        initView();
        onClick();
        loadTags();

        Intent intent = getIntent();
        videoFreeModelIntent = intent.getParcelableExtra("video_free_model");
        if (videoFreeModelIntent != null) {
            thereIsData = true;
            btnHapus.setVisibility(View.VISIBLE);
            edtLink.setText("https://youtu.be/" + videoFreeModelIntent.getVideoYoutubeId());
        }
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
                showHapusDialog();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = edtLink.getText().toString();
                if (url.length() != 0) {
                    if (!tagVideoId.equals("")) {
                        pd.show();
                        if (thereIsData) {
                            addOrUpdateData(url, "update");
                        } else {
                            addOrUpdateData(url, "add");
                        }
                    } else {
                        Toast.makeText(OpAddFreeCourseActivity.this, "Mohon Pilih Tag", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        pd.dismiss();
        super.onPause();
    }

    private void loadTags() {
        final List<String> tagList = new ArrayList<>();
        final List<TagModel> tagModels = new ArrayList<>();

        tagList.add("Tag");

        final ArrayAdapter<String> spnArrayAdapterTag = new ArrayAdapter<String>(OpAddFreeCourseActivity.this,
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
                    tagVideo = tagModels.get(position - 1).getTag();
                    tagVideoId = tagModels.get(position - 1).getTagid();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference tagRef = db.collection("Tags");
        tagRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        tagList.clear();
                        tagList.add("Tag");
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            TagModel tagModel = documentSnapshot.toObject(TagModel.class);
                            tagModel.setTagid(documentSnapshot.getId());

                            tagModels.add(new TagModel(tagModel.getTag(), tagModel.getTagid()));
                            tagList.add(tagModel.getTag());
                        }
                        spnArrayAdapterTag.notifyDataSetChanged();

                        if (thereIsData) {
                            String tagId = videoFreeModelIntent.getTagId();
                            for (int i = 0; i < tagModels.size(); i++) {
                                if (tagModels.get(i).getTagid().equals(tagId)) {
                                    spnTag.setSelection(i + 1);
                                    break;
                                }
                            }
                        }
                    }
                });
    }

    private void hapus() {
        DocumentReference videoRef = videoFreeRef.document(videoFreeModelIntent.getDocumentId());
        videoRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(OpAddFreeCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addOrUpdateData(String url, final String status) {
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
                        pd.dismiss();
                        return;
                    }

                    YResponse yResponse = response.body();

                    if (yResponse.getItems().size() == 0) {
                        pd.dismiss();
                        Toast.makeText(OpAddFreeCourseActivity.this, getString(R.string.link_not_valid), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String title = yResponse.getItems().get(0).getSnippet().getTitle();
                    String description = yResponse.getItems().get(0).getSnippet().getDescription();
                    String thumbStandard = yResponse.getItems().get(0).getSnippet().getThumbnails().getStandard().getUrl();
                    String tagId = tagVideoId;
                    String tag = tagVideo;
                    long datecreated = 0;

                    try {
                        datecreated = Timestamp.now().getSeconds();
                    }catch (Exception e){
                        Toast.makeText(OpAddFreeCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (datecreated > 0){
                        VideoFreeModel videoFreeModel = new VideoFreeModel(thumbStandard, youtubeVideoId,
                                title, description, tagId, tag, datecreated);
                        if (status.equals("update")) {
                            WriteBatch batch = db.batch();
                            DocumentReference videoRef = videoFreeRef.document(videoFreeModelIntent.getDocumentId());
                            batch.update(videoRef, "description", description);
                            batch.update(videoRef, "tagId", tagId);
                            batch.update(videoRef, "thumbnailUrl", thumbStandard);
                            batch.update(videoRef, "title", title);
                            batch.update(videoRef, "videoYoutubeId", youtubeVideoId);
                            batch.update(videoRef, "tag", tag);
                            batch.update(videoRef, "dateCreated", datecreated);
                            batch.commit()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            onBackPressed();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(OpAddFreeCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
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
                                            pd.dismiss();
                                            Toast.makeText(OpAddFreeCourseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onFailure(Call<YResponse> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(OpAddFreeCourseActivity.this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            pd.dismiss();
            Toast.makeText(this, getString(R.string.link_not_valid), Toast.LENGTH_SHORT).show();
        }
    }

    private void showHapusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Apakah anda yakin ingin menghapus video ini?");
        builder.setTitle("Hapus Video");
        builder.setCancelable(false);
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                pd.show();
                hapus();
            }
        });

        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}