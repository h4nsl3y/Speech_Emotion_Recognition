package com.example.speech_emotion_recognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {


    Context context;
    ArrayList name, ang,cal,dis,fea,hap,sad;

    CustomAdapter(Context context ,ArrayList name,
                  ArrayList ang,ArrayList cal,
                  ArrayList dis,ArrayList fea,
                  ArrayList hap,ArrayList sad){
        this.context = context;
        this.name = name;
        this.ang = ang;
        this.cal = cal;
        this.dis = dis;
        this.fea = fea;
        this.hap = hap;
        this.sad = sad;

    }

    @NonNull
    @Override
    public CustomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.MyViewHolder holder, int position) {
        holder.txt_record_title.setText(String.valueOf(name.get(position)));
        holder.txt_ang.setText("ang : "+String.valueOf(ang.get(position)));
        holder.txt_cal.setText("cal : "+String.valueOf(cal.get(position)));
        holder.txt_dis.setText("dis : "+String.valueOf(dis.get(position)));
        holder.txt_fea.setText("fea : "+String.valueOf(fea.get(position)));
        holder.txt_hap.setText("hap : "+String.valueOf(hap.get(position)));
        holder.txt_sad.setText("sad : "+String.valueOf(sad.get(position)));
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_record_title,txt_ang, txt_cal, txt_dis, txt_fea, txt_hap, txt_sad;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_record_title = itemView.findViewById(R.id.txt_record_title);
            txt_ang = itemView.findViewById(R.id.txt_ang);
            txt_cal = itemView.findViewById(R.id.txt_cal);
            txt_dis = itemView.findViewById(R.id.txt_dis);
            txt_fea = itemView.findViewById(R.id.txt_fea);
            txt_hap = itemView.findViewById(R.id.txt_hap);
            txt_sad = itemView.findViewById(R.id.txt_sad);
        }
    }

}
