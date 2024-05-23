package com.example.speech_emotion_recognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyVieHolder> {

    private Context mcontext;
    private ArrayList<MusicFiles> mFiles;
    private Medium medium;

    MusicAdapter(Context mcontext,ArrayList<MusicFiles> mFiles){
        this.mFiles = mFiles;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public MusicAdapter.MyVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.music_items, parent , false);
        return new MyVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MyVieHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        try {
            byte[] image = getAlbumArt(mFiles.get(position).getPath());
            if( image != null){
                Glide.with(mcontext).asBitmap().load(image).into(holder.album_art);
            }
            else{
                Glide.with(mcontext).load(R.drawable.ic_baseline_music_note_24).into(holder.album_art);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                medium = new Medium();
                String file_name = mFiles.get(position).getTitle();
                //String category = medium.getCategory(file_name,mcontext);
                Intent intent = new Intent(mcontext, MusicPlayer_activity.class);

                intent.putExtra("position",position);

                //intent.putExtra("category",category);

                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyVieHolder extends RecyclerView.ViewHolder {
        TextView file_name;
        ImageView album_art;

        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_name);
            album_art = itemView.findViewById(R.id.music_img);
        }
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return(art);
    }

}
