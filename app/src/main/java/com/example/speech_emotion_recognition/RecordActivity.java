package com.example.speech_emotion_recognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity {

    private Button  btn_start, btn_stop, btn_file;
    private ImageView btn_back;
    private MediaRecorder mediarecorder;
    private PieChart piechart;
    private BarChart barchart;
    private LineChart linechart;
    private Medium medium;
    private boolean toggle_record;
    private String name;
    private LottieAnimationView record_lottie;
    private final static int FILE_PERMISSION_CODE = 200;
    private final static int MICROPHONE_PERMISSION_CODE=201;
    private final static int AUDIO_FILE_PERMISSION_CODE=202;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        get_base_Path();
        initInstances();

        medium.draw_pie_chart(RecordActivity.this, piechart);
        medium.draw_barchart(RecordActivity.this, barchart);
        medium.draw_line(RecordActivity.this, linechart);
        piechart.animate();
        barchart.animateY(1200);
        barchart.animateX(1000);
        linechart.animateX(2000);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            btn_file.setVisibility(View.INVISIBLE);
        }


        btn_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAudiofilePermission();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home_Activity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle_record==false){
                    Long tsLong = System.currentTimeMillis()/1000;
                    name = tsLong.toString();
                }
                if(isMicrophonePresent()){
                    getMicrophonePermission();
                }
                if(ContextCompat.checkSelfPermission(RecordActivity.this, android.Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                toggle_record = true;
                start_animate_record();
                new Thread(new Runnable() {
                    @Override
                    public void run() {


                            while (toggle_record == true) {

                                try {
                                    record();

                                    Thread.sleep(3000);

                                    stopRecord();// stop recording
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            medium.draw_pie_chart(RecordActivity.this, piechart);
                                            medium.draw_barchart(RecordActivity.this, barchart);
                                            medium.draw_line(RecordActivity.this, linechart);
                                            piechart.notifyDataSetChanged();
                                            piechart.invalidate();
                                            barchart.notifyDataSetChanged();
                                            barchart.invalidate();
                                            linechart.notifyDataSetChanged();
                                            linechart.invalidate();
                                        }
                                    });

                                    upload(name);

                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(RecordActivity.this, "Error while recording", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    }).start();
                }
            }
        });

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle_record = false;
                stop_animate_record();
            }
        });
        setNotificationBar();
    }

    private void initInstances(){
        medium = new Medium();
        btn_file = findViewById(R.id.btn_file_access);
        btn_back = findViewById(R.id.btn_back_recorder);
        btn_start = findViewById(R.id.record_start);
        btn_stop = findViewById(R.id.record_stop);
        piechart = findViewById(R.id.piechart_record);
        barchart = findViewById(R.id.barchart_record);
        linechart = findViewById(R.id.linechart_record);
        record_lottie = findViewById(R.id.record_animationView);
   }

    private boolean isMicrophonePresent(){
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){return true; }
        else{return false; }
    }


/*
* record()
* Purpose :  Use to record audio via device microphone
*/
    private void record(){
        try{
            mediarecorder = new MediaRecorder();
            mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediarecorder.setOutputFile(getRecordingFilePath());
            mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediarecorder.prepare();
            mediarecorder.start();
        }
        catch(Exception e) {
            Toast.makeText(this,"recording has failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void start_animate_record(){
        record_lottie.setVisibility(View.VISIBLE);
    }

    private void stop_animate_record(){
        record_lottie.setVisibility(View.INVISIBLE);
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "output.wav");
        return file.getPath();
    }
/*
 * stopRecord()
 * Purpose :  Use to stop recording of audio via device microphone
 */
    private void stopRecord(){
        mediarecorder.stop();
        mediarecorder.release();
        mediarecorder = null;
    }

    public String get_base_Path(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        return musicDirectory.getPath();
    }

    private void upload(String name){
        String filetype = "recording";
        String format = "wav";
        String path = getRecordingFilePath();
        String dbname = "record"+name;
        try {
            medium.audioUpload(RecordActivity.this,path ,dbname , filetype, format);
        } catch (IOException | JSONException e) {
            Toast.makeText(RecordActivity.this, "Error while uploading audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNotificationBar(){
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(0xFF8692F7);
    }

    private void getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.RECORD_AUDIO},MICROPHONE_PERMISSION_CODE);}
    }

    private void getAudiofilePermission(){
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.READ_MEDIA_AUDIO},AUDIO_FILE_PERMISSION_CODE);
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            btn_file.setVisibility(View.INVISIBLE);}
    }
}