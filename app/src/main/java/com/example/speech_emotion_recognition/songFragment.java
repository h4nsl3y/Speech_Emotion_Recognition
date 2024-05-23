package com.example.speech_emotion_recognition;

import static com.example.speech_emotion_recognition.MusicFragmentContainer_Activity.musicFiles;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class songFragment extends Fragment {

    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    public songFragment(){
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);
        recyclerView = view.findViewById(R.id.music_recyclerView);
        recyclerView.setHasFixedSize(true);
        if(musicFiles.size() >= 1){
            musicAdapter = new MusicAdapter(getContext(),musicFiles);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        }
        return view;
    }
}