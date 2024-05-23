package com.example.speech_emotion_recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import java.io.File;

public class NavigationActivity extends AppCompatActivity{

    private Button btn_back;
    private TextView nofile;
    private File[] filesAndFolder;
    private RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation);

        btn_back = findViewById(R.id.btn_back_navigation);
        nofile = findViewById(R.id.noFileText);
        recyclerView = findViewById(R.id.recyclerView_explorer);

        String path = getIntent().getStringExtra("path");
        File root = new File(path);
        filesAndFolder = root.listFiles();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MusicFragmentContainer_Activity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        });


        if (filesAndFolder == null || filesAndFolder.length == 0) {
            nofile.setVisibility(View.VISIBLE);
            return;
        }
        nofile.setVisibility(View.INVISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CustomAdapter_0(NavigationActivity.this,
                getApplicationContext(),filesAndFolder));

    }


}



