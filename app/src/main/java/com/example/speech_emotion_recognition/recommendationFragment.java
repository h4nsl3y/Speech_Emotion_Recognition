package com.example.speech_emotion_recognition;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class recommendationFragment extends Fragment {

    RecyclerView recyclerView;
    MusicRecommendAdapter musicRecommendAdapter;
    Medium medium;
    private ArrayList<MusicFiles> recomendedMusic = new ArrayList<>();
    private ArrayList<String> category = new ArrayList<String>(Arrays.asList("fierce", "frightful", "happy", "peaceful", "sad"));
    private  ArrayList<String> emotionResult = new ArrayList<>();

    public recommendationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        recyclerView = view.findViewById(R.id.music_recyclerView);
        recyclerView.setHasFixedSize(true);
        medium = new Medium();

        getRecommendAudio(getActivity());

        if(recomendedMusic.size() != 0){
            musicRecommendAdapter = new MusicRecommendAdapter(getContext(),recomendedMusic);
            recyclerView.setAdapter(musicRecommendAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        }
        return view;
    }

    public void counterEmotion(){
        String emotion = medium.dominantEmotion(getActivity());
//        Toast.makeText(getActivity(), emotion, Toast.LENGTH_SHORT).show();
        switch (emotion){
            case "ang":
                emotionResult.add("peaceful");
                emotionResult.add("frightful");
                break;
            case "cal":
                emotionResult.add("peaceful");
                emotionResult.add("happy");
                break;
            case "dis":
                emotionResult.add("peaceful");
                emotionResult.add("fierce");
                break;
            case "fea":
                emotionResult.add("happy");
                emotionResult.add("peaceful");
                break;
            case "sad":
                emotionResult.add("peaceful");
                emotionResult.add("happy");
                emotionResult.add("fierce");
                break;
            default:
                emotionResult.add("fierce");
                emotionResult.add("frightful");
                emotionResult.add("happy");
                emotionResult.add("peaceful");
                emotionResult.add("sad");
                break;
        }
    }

    public ArrayList<MusicFiles> getRecommendAudio(Context context){
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        counterEmotion();

        ArrayList<String> recommend = medium.recommendTitles(getActivity(),emotionResult);

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, // Path
                MediaStore.Audio.Media.ARTIST};
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null,
                null);
        if (cursor != null){
            while(cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);


                if (recommend.contains(title)){

                    System.out.println(path);
                    System.out.println(title);
                    System.out.println(artist);
                    System.out.println(album);
                    System.out.println(duration);
                    recomendedMusic.add(new MusicFiles(path,title,artist,album,duration));
                }

                //take log.e for check
                //Log.e("Path : " + path ,"Album : " + album );
            }
            cursor.close();
        }
        return tempAudioList;
    }
}