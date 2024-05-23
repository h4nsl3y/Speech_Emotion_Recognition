package com.example.speech_emotion_recognition;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import java.io.File;

public class CustomAdapter_0 extends RecyclerView.Adapter<CustomAdapter_0.ViewHolder>{
    private Activity activity;
    private Context context;
    private File[]filesAndFolder;
    public CustomAdapter_0(Activity activity , Context context, File[]filesAndFolder){
        this.activity = activity;
        this.context = context;
        this.filesAndFolder = filesAndFolder;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File selectedFile = filesAndFolder[position];
        holder.textView.setText(selectedFile.getName());

        if(selectedFile.isDirectory()){
            holder.imagView.setImageResource(R.drawable.ic_baseline_folder_24);
        }else{
            holder.imagView.setImageResource(R.drawable.ic_baseline_audio_file_24);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFile.isDirectory()){

                    Intent intent = new Intent(context, NavigationActivity.class);
                    String path = selectedFile.getAbsolutePath();
                    intent.putExtra("path",path);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, Home_Activity.class);
                    String path = Environment.getExternalStorageDirectory().getPath();
                    intent.putExtra("filepath",selectedFile.getAbsolutePath());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context. startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesAndFolder.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imagView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.explorer_file_txt);
            imagView = itemView.findViewById(R.id.explorer_icon_view);
        }
    }
}
