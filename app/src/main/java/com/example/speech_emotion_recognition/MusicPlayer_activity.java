package com.example.speech_emotion_recognition;

import static com.example.speech_emotion_recognition.MusicFragmentContainer_Activity.musicFiles;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import me.tankery.lib.circularseekbar.CircularSeekBar;

public class MusicPlayer_activity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private RelativeLayout container_layout, top_bar, title_bar, option_bar;
    private TextView song_name, artist_name,duration, duration_total;
    private ImageView  next_btn, prev_btn , shuffle_btn, repeat_btn , upload_btn, gradient, btn_back;
    private CircleImageView album_art  , album_art_hole;
    private FloatingActionButton play_pause_btn;
    private SeekBar seekbar;
    private CircularSeekBar circularSeekBar;
    private static ArrayList<MusicFiles> song_list =new ArrayList<>();
    private static Uri uri;
    private static MediaPlayer mediaPlayer;
    private Handler handler;
    private Thread  playThread ,prevThread, nextThread;
    private GradientDrawable gradientDrawable ,plainDrawable;
    private Medium medium;
    private int position = -1;
    private String category;
    static boolean shuffleBoolean ,repeatBoolean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //instances
        medium = new Medium();
        handler = new Handler();
        //initialising layouts
        container_layout = findViewById(R.id.player_container);
        top_bar = findViewById(R.id.player_top_button);
        title_bar = findViewById(R.id.player_album_card);
        option_bar = findViewById(R.id.player_bottom_btn_layout);
        // initialising textviews
        song_name = findViewById(R.id.player_song);
        artist_name = findViewById(R.id.player_artist);
        duration = findViewById(R.id.player_duration);
        duration_total = findViewById(R.id.player_duration_total);
        //initialising imageviews
        btn_back = findViewById(R.id.player_back_btn);
        album_art = findViewById(R.id.player_cover_art) ;
        next_btn = findViewById(R.id.player_skip_next) ;
        prev_btn = findViewById(R.id.player_skip_previous) ;
        shuffle_btn = findViewById(R.id.player_shuffle) ;
        repeat_btn = findViewById(R.id.player_repeat) ;
        upload_btn = findViewById(R.id.player_upload_btn);
        //gradient = findViewById(R.id.player_img_gradient);
        album_art_hole = findViewById(R.id.player_cover_art_hole) ;
        //initialising floating button
        play_pause_btn  = findViewById(R.id.player_play_pause);
        //initialising seekbar
        seekbar = findViewById(R.id.player_seekBar);
        circularSeekBar = findViewById(R.id.player_circular_seekbar);
        //initialising boolean values
        shuffleBoolean = false;
        repeatBoolean = false;

        setNotificationBar();
        getIntentMethod();
        setColor();


        mediaPlayer.setOnCompletionListener(this);

        circularSeekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(@Nullable CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo((int) (progress*1000));
                }
            }

            @Override
            public void onStopTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

            }

            @Override
            public void onStartTrackingTouch(@Nullable CircularSeekBar circularSeekBar) {

            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        MusicPlayer_activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                    seekbar.setProgress(mCurrentPosition);
                    duration.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadBtnClicked();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MusicFragmentContainer_Activity.class));
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();
            }
        });

        shuffle_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (shuffleBoolean){
                    shuffleBoolean = false;
                    shuffle_btn.setImageResource(R.drawable.ic_baseline_shuffle_24);
                }
                else{
                    shuffleBoolean = true;
                    shuffle_btn.setImageResource(R.drawable.ic_baseline_shuffle_off_24);
                }
            }
        });

        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean){
                    repeatBoolean = false;
                    repeat_btn.setImageResource(R.drawable.ic_baseline_repeat_24);
                }
                else{
                    repeatBoolean = true;
                    repeat_btn.setImageResource(R.drawable.ic_baseline_repeat_on_24);
                }
            }
        });
    }



    private void getIntentMethod(){
        position = getIntent().getIntExtra("position", -1);
        song_list= musicFiles;
        if(song_list != null){
            play_pause_btn.setImageResource(R.drawable.ic_baseline_pause_24);
            uri = Uri.parse(song_list.get(position).getPath());

        }

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        metaData(uri);

        seekbar.setMax(mediaPlayer.getDuration()/1000);
        circularSeekBar.setMax(mediaPlayer.getDuration()/1000);
    }

    private String formattedTime(int time_int){
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(time_int % 60);
        String minutes = String.valueOf(time_int / 60);

        totalout = minutes+":"+seconds;
        totalNew = minutes+":0" + seconds;

        if(seconds.length() == 1){
            return totalNew;
        }
        else{
            return totalout;
        }
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int song_duration = Integer.parseInt(song_list.get(position).getDuration());
        duration_total.setText(formattedTime(song_duration/1000));
        song_name.setText(song_list.get(position).getTitle());
        artist_name.setText(song_list.get(position).getArtist());

        category = medium.getCategory(song_list.get(position).getTitle(),this);

        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap = null;
        if(art != null){
            bitmap = BitmapFactory.decodeByteArray(art,0, art.length);
            imageAnimation(this, album_art, bitmap);
        }
        else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.background_logo)
                    .into(album_art);
        }
        setColor();
    }

    private void setColor(){
        int color_value;
        switch(category) {
            case "fierce":
                color_value = 0xFFF72585;
                break;
            case "frightful":
                color_value = 0xFF3A0CA3;
                break;
            case "happy":
                color_value = 0xFF4CC9F0;
                break;
            case "peaceful":
                color_value = 0xFF3F37C9;
                break;
            case "sad":
                color_value = 0xFF4361EE;
                break;
            default:
                color_value = 0xFF8692F7;
                break;
        }
        gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{ color_value , 0x00000000 });
        plainDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{ color_value , color_value });


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(color_value);

        album_art.setBorderColor(color_value);
        circularSeekBar.setCircleColor(color_value);
//        circularSeekBar.setRotation(270);

//        gradient.setBackground(gradientDrawable);
        top_bar.setBackgroundColor(color_value);

        container_layout.setBackgroundColor(color_value);


//        title_bar.setBackgroundColor(color_value);
//        title_bar.setBackground(plainDrawable);
//        option_bar.setBackgroundColor(color_value);
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        play_pause_BtnClicked();
        super.onResume();
    }

    private void playThreadBtn(){
        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                play_pause_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play_pause_BtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void prevThreadBtn(){
        prevThread = new Thread(){
            @Override
            public void run() {
                super.run();
                prev_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void nextThreadBtn(){
        nextThread = new Thread(){
            @Override
            public void run() {
                super.run();
                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void play_pause_BtnClicked(){
        if (mediaPlayer.isPlaying()){
            play_pause_btn.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            mediaPlayer.pause();
        }
        else{
            play_pause_btn.setImageResource(R.drawable.ic_baseline_pause_24);
            mediaPlayer.start();
        }
        seekbar.setMax(mediaPlayer.getDuration()/1000);
        circularSeekBar.setMax(mediaPlayer.getDuration()/1000);
        playAudio();
    }

    private void nextBtnClicked(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if(shuffleBoolean && !repeatBoolean){
            position = getRandom(song_list.size() -1);
        }
        else if(!shuffleBoolean && !repeatBoolean){
            position = ((position + 1)% song_list.size()) ;
        }
        // if repeat boolean is true, position won t increment
        uri = Uri.parse(song_list.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        metaData(uri);
        playAudio();
        play_pause_btn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        mediaPlayer.start();
        }

    private void prevBtnClicked(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if(shuffleBoolean && !repeatBoolean){
            position = getRandom(song_list.size() -1);
        }
        else if(!shuffleBoolean && !repeatBoolean){
            position = ((position - 1)<0 ? (song_list.size() -1 ) : (position -1));
        }
        // if repeat boolean is true, position won t increment
        uri = Uri.parse(song_list.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        metaData(uri);
        playAudio();
        play_pause_btn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        mediaPlayer.start();
    }

    private void uploadBtnClicked(){
        try {
            String file_path = uri.getPath();
            String name = song_list.get(position).getTitle();
            if(file_path!= null){
                lottie_loading_Dialog lottie_load_dialog = new lottie_loading_Dialog(this);

                String path[]= file_path.split("/");
                String file[]= path[(path.length-1)].split("\\.");
                String extension = file[file.length-1];
                upload(file_path,name, extension);
                lottie_load_dialog.startLoadingDialog("");
            }
        }catch (Exception e){
            Toast.makeText(this, "Error occurred while uploading", Toast.LENGTH_SHORT).show();
        }
    }

    private void playAudio(){
        MusicPlayer_activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                    seekbar.setProgress(mCurrentPosition);
                    circularSeekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this,1000);
            }
        });
        mediaPlayer.setOnCompletionListener(this);
    }

    public void imageAnimation(Context context, ImageView imageView, Bitmap bitmap){
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if(mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    private void upload(String path,String name,String format){
        Medium medium = new Medium();
        String filetype = "file";
        String filepath[]= path.split("/");
        String file[]= filepath[(filepath.length-1)].split("\\.");
        try {
            medium.audioUpload(this, path,name, filetype, format);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setNotificationBar(){
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(0xFF8692F7);
    }

    private int getRandom(int i){
        Random random = new Random();
        return random.nextInt(i+1);
    }
}