package com.example.speech_emotion_recognition;

// libraries
import static java.lang.Float.parseFloat;
import static java.lang.Math.max;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Base64;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EmptyStackException;

public class Medium {

    public Medium(){}
    //All data  from table
    private ArrayList<String> name,cat , ang,cal,dis,fea,hap,sad,sur;
    //latest -- most recent data from table
    private ArrayList<String> lst_name,lst_cat, lst_ang,lst_cal,lst_dis,lst_fea,lst_hap,lst_sad,lst_sur;
    private Double anger_upload, calm_upload, disgust_upload, fear_upload, happiness_upload, sadness_upload, surprised_upload;
    private String category, anger_lst, calm_lst, disgust_lst, fear_lst, happiness_lst, sadness_lst, surprised_lst;
    private String[]  anger_str, calm_str, disgust_str, fear_str, happiness_str, sadness_str,surprised_str;
    private ArrayList<Double> anger_arr, calm_arr, disgust_arr, fear_arr, happiness_arr, sadness_arr,surprised_arr;
    private static String server_url = "http://192.168.132.128:9000/audio_analyse";
    private static String col_ang = "#F72585";
    private static String col_cal = "#3F37C9";
    private static String col_dis = "#7209B7";
    private static String col_fea = "#3A0CA3";
    private static String col_hap = "#4CC9F0";
    private static String col_sad = "#4361EE";
    private static String col_sur = "#ff46d0";
    private static String col_lavender= "#8692f7";
    private static String col_white = "#FFFFFF";
    private static String col_back = "#4E60FB";




    public void store_file_data_in_array(Context context){
        myDatabaseSQlite mdb = new myDatabaseSQlite(context);
        name = new ArrayList<>();
        cat = new ArrayList<>();

        Cursor cursor_all = mdb.readAllMusicData();
        if(cursor_all.getCount()!=0){
            while(cursor_all.moveToNext()){
                name.add(cursor_all.getString(0));
                cat.add(cursor_all.getString(1));
            }
        }
        cursor_all.close();
    }

    public void retrieveLastFromDB(Context context){
        myDatabaseSQlite mdb = new myDatabaseSQlite(context);
        lst_name = new ArrayList<>();
        lst_ang = new ArrayList<>();
        lst_cal = new ArrayList<>();
        lst_dis = new ArrayList<>();
        lst_fea = new ArrayList<>();
        lst_hap = new ArrayList<>();
        lst_sad = new ArrayList<>();
        lst_sur = new ArrayList<>();

        Cursor cursor_lst = mdb.readLastData();
        if(cursor_lst.getCount()!=0){
            while(cursor_lst.moveToNext()){
                lst_name.add(cursor_lst.getString(0));
                lst_ang.add(cursor_lst.getString(1));
                lst_cal.add(cursor_lst.getString(2));
                lst_dis.add(cursor_lst.getString(3));
                lst_fea.add(cursor_lst.getString(4));
                lst_hap.add(cursor_lst.getString(5));
                lst_sad.add(cursor_lst.getString(6));
                lst_sur.add(cursor_lst.getString(7));
            }
        }
        cursor_lst.close();
    }

    public int average_calc(ArrayList<String> arr){
        Double avr = 0.0;
        int lenght = arr.size();
        for (String item :arr){
            avr = avr + Double.parseDouble(item);
        }
        avr = avr / lenght;
        int result = Integer.valueOf(avr.intValue());
        return(result);
  }

    public  ArrayList chart_data(Context context){
        retrieveLastFromDB(context);
        ArrayList<Integer> result = new ArrayList<>();
        int ang_avr = average_calc(lst_ang);
        int cal_avr = average_calc(lst_cal);
        int dis_avr = average_calc(lst_dis);
        int fea_avr = average_calc(lst_fea);
        int hap_avr = average_calc(lst_hap);
        int sad_avr = average_calc(lst_sad);
        int sur_avr = average_calc(lst_sur);



        result.add(ang_avr);
        result.add(cal_avr);
        result.add(dis_avr);
        result.add(fea_avr);
        result.add(hap_avr);
        result.add(sad_avr);
        result.add(sur_avr);
        return(result);
    }


    public ArrayList line_data(Context context){
        retrieveLastFromDB(context);
        ArrayList result = new ArrayList<>();
        result.add(lst_ang);
        result.add(lst_cal);
        result.add(lst_dis);
        result.add(lst_fea);
        result.add(lst_hap);
        result.add(lst_sad);
        result.add(lst_sur);
        return(result);
    }
/*
* audioUpload()
* Purpose - Use to upload audio file to application server for processing
*           add result to database
*/
    public void audioUpload(Context context,String path,String name, String filetype, String format) throws IOException, JSONException {
        String audio_base = data64(context , path);
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONObject jsonParams = new JSONObject();
        jsonParams.put("audio", audio_base);
        jsonParams.put("type",filetype);
        jsonParams.put("format",format);
        jsonParams.put("title",name);

        // Building a request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,server_url,
                jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(filetype == "recording"){
                                anger_lst= (String) response.get("anger");
                                calm_lst= (String) response.get("calm");
                                disgust_lst= (String) response.get("disgust");
                                fear_lst= (String) response.get("fear");
                                happiness_lst= (String) response.get("happiness");
                                sadness_lst= (String) response.get("sadness");
                                surprised_lst= (String) response.get("surprised");
                                saveRecord(context,name);
                            }
                            if(filetype == "file"){
                                category= (String) response.get("category");
                                addFileToDatabase(context,name);
                            }

                        }
                        catch (JSONException e) {
                            Toast.makeText(context, "error decode", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"some error found while processing",Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public String data64(Context context, String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));

        String base_audio= Base64.encodeToString(bytes,Base64.DEFAULT);
        return base_audio;
    }

    public void saveRecord(Context context,  String name){
        anger_arr = new ArrayList<>();
        calm_arr = new ArrayList<>();
        disgust_arr = new ArrayList<>();
        fear_arr = new ArrayList<>();
        happiness_arr = new ArrayList<>();
        sadness_arr = new ArrayList<>();
        surprised_arr = new ArrayList<>();

        anger_str = (anger_lst.split(","));
        calm_str = (calm_lst.split(","));
        disgust_str = (disgust_lst.split(","));
        fear_str = (fear_lst.split(","));
        happiness_str = (happiness_lst.split(","));
        sadness_str = (sadness_lst.split(","));
        surprised_str = (surprised_lst.split(","));

        for (int i = 0; i < anger_str.length; i++){

            anger_upload = Double.parseDouble(anger_str[i]);
            calm_upload = Double.parseDouble(calm_str[i]);
            disgust_upload = Double.parseDouble(disgust_str[i]);
            fear_upload = Double.parseDouble(fear_str[i]);
            happiness_upload = Double.parseDouble(happiness_str[i]);
            sadness_upload = Double.parseDouble(sadness_str[i]);
            surprised_upload = Double.parseDouble(surprised_str[i]);

            myDatabaseSQlite myDB = new myDatabaseSQlite(context);
            myDB.addData_record(name,
                    anger_upload ,calm_upload,
                    disgust_upload,fear_upload,
                    happiness_upload,sadness_upload,
                    surprised_upload);
        }
    }

    public void addFileToDatabase(Context context, String name){
    myDatabaseSQlite myDB = new myDatabaseSQlite(context);
    myDB.addData_file(name,category);
    }

    public String getCategory(String song_name, Context context){
        myDatabaseSQlite mdb = new myDatabaseSQlite(context);
        return mdb.readFileData(context, song_name);
    }

    public void draw_pie_chart(Context context, PieChart piechart){

        ArrayList<Integer> pie_val = chart_data(context);

        ArrayList<PieEntry> emotions = new ArrayList<>();
        emotions.add(new PieEntry( pie_val.get(0))); //,"Anger"));
        emotions.add(new PieEntry( pie_val.get(1))); //,"Calm"));
        emotions.add(new PieEntry( pie_val.get(2))); //,"Disgust"));
        emotions.add(new PieEntry( pie_val.get(3))); //,"Fear"));
        emotions.add(new PieEntry( pie_val.get(4))); //,"Happiness"));
        emotions.add(new PieEntry( pie_val.get(5))); //,"Sadness"));
        emotions.add(new PieEntry( pie_val.get(6))); //,"Surprised"));



        PieDataSet pieDataSet = new PieDataSet(emotions,"Emotions");
        pieDataSet.setColors(new int[] {
                Color.parseColor(col_ang),
                Color.parseColor(col_cal),
                Color.parseColor(col_dis),
                Color.parseColor(col_fea),
                Color.parseColor(col_hap),
                Color.parseColor(col_sad),
                Color.parseColor(col_sur)});

        pieDataSet.setValueTextColor(Color.parseColor(col_white));

        PieData pieData = new PieData(pieDataSet);

        piechart.setData(pieData);
        piechart.getDescription().setEnabled(false);
        piechart.setHoleRadius(30f);
        piechart.setTransparentCircleRadius(0f);
        piechart.setHoleColor(Color.parseColor(col_white));

        piechart.setDrawSliceText(false);
        piechart.getLegend().setEnabled(false);
    }

    public void draw_barchart(Context context, BarChart barchart){

        ArrayList<Integer> bar_val = chart_data(context);

        ArrayList<BarEntry> emotions = new ArrayList<>();
        emotions.add(new BarEntry(1,bar_val.get(0)));
        emotions.add(new BarEntry(2,bar_val.get(1)));
        emotions.add(new BarEntry(3,bar_val.get(2)));
        emotions.add(new BarEntry(4,bar_val.get(3)));
        emotions.add(new BarEntry(5,bar_val.get(4)));
        emotions.add(new BarEntry(6,bar_val.get(5)));
        emotions.add(new BarEntry(7,bar_val.get(6)));

        BarDataSet barDataSet = new BarDataSet(emotions,"emotions");
        barDataSet.setColors(new int[] {
                Color.parseColor(col_ang),
                Color.parseColor(col_cal),
                Color.parseColor(col_dis),
                Color.parseColor(col_fea),
                Color.parseColor(col_hap),
                Color.parseColor(col_sad),
                Color.parseColor(col_sur) });
        barDataSet.setValueTextColor(android.R.color.white);
        barDataSet.setValueTextSize(16f);

        BarData bardata = new BarData(barDataSet);

        barchart.setData(bardata);
        barchart.getDescription().setText("Emotions");

        XAxis xAxis = barchart.getXAxis();
        YAxis yAxisLeft = barchart.getAxisLeft();
        YAxis yAxisRight = barchart.getAxisRight();

        barchart.getAxisLeft().setDrawLabels(false);
        barchart.getDescription().setEnabled(false);

        barchart.setDrawGridBackground(false);
        xAxis.setDrawAxisLine(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisRight.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        yAxisLeft.setGridColor(Color.parseColor(col_back));
        yAxisRight.setGridColor(Color.parseColor(col_back));
        barchart.getLegend().setEnabled(false);
        xAxis.setValueFormatter(new MyAxisValueFormatter(bar_val));
    }

    private class MyAxisValueFormatter extends ValueFormatter {

        ArrayList<Integer> bar_val;
        public MyAxisValueFormatter(ArrayList<Integer> bar_val){
            this.bar_val =  bar_val ;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            float total = bar_val.get(0)+bar_val.get(1)+bar_val.get(2)+
                        bar_val.get(3)+bar_val.get(4)+bar_val.get(5)+
                        bar_val.get(6);


            int integer_val = Math.round(value);
            String item = "";
            int num1 = Math.round((bar_val.get(0) / total ) * 100);
            int num2 = Math.round((bar_val.get(1) / total ) * 100);
            int num3 = Math.round((bar_val.get(2) / total ) * 100);
            int num4 = Math.round((bar_val.get(3) / total ) * 100);
            int num5 = Math.round((bar_val.get(4) / total ) * 100);
            int num6 = Math.round((bar_val.get(5) / total ) * 100);
            int num7 = Math.round((bar_val.get(6) / total ) * 100);


            if (integer_val == 1){
                item = String.valueOf(num1) + "%"; //"Anger";
            }else if (integer_val == 2){
                item = String.valueOf(num2) + "%"; //"Calm";
            }else if (integer_val == 3){
                item = String.valueOf(num3) + "%"; //"Disgust";
            }else if (integer_val == 4){
                item = String.valueOf(num4) + "%"; //"Fear";
            }else if (integer_val == 5){
                item = String.valueOf(num5) + "%"; //"Happy";
            }else if (integer_val == 6){
                item = String.valueOf(num6) + "%"; //"Sad";
            }else if (integer_val == 7){
                item = String.valueOf(num7) + "%"; //"Surprised";
            }
            return(item);
        }
    }

    public void draw_line(Context context, LineChart linechart){
        ArrayList<ArrayList> data = line_data(context);

        ArrayList<Entry> yval_1 = new ArrayList<>();
        ArrayList<Entry> yval_2 = new ArrayList<>();
        ArrayList<Entry> yval_3 = new ArrayList<>();
        ArrayList<Entry> yval_4 = new ArrayList<>();
        ArrayList<Entry> yval_5 = new ArrayList<>();
        ArrayList<Entry> yval_6 = new ArrayList<>();
        ArrayList<Entry> yval_7 = new ArrayList<>();

        int val_1,val_2,val_3,val_4,val_5,val_6,val_7;

        for(int i=0; i<( data.get(0)).size() ;i++){
            val_1 = (int)(parseFloat((String )(data.get(0)).get(i)));
            val_2 = (int)(parseFloat((String )(data.get(1)).get(i)));
            val_3 = (int)(parseFloat((String )(data.get(2)).get(i)));
            val_4 = (int)(parseFloat((String )(data.get(3)).get(i)));
            val_5 = (int)(parseFloat((String )(data.get(4)).get(i)));
            val_6 = (int)(parseFloat((String )(data.get(5)).get(i)));
            val_7 = (int)(parseFloat((String )(data.get(6)).get(i)));

            int index = i*3;
            yval_1.add(new Entry(index,val_1));
            yval_2.add(new Entry(index,val_2));
            yval_3.add(new Entry(index,val_3));
            yval_4.add(new Entry(index,val_4));
            yval_5.add(new Entry(index,val_5));
            yval_6.add(new Entry(index,val_6));
            yval_7.add(new Entry(index,val_7));

        }

        LineDataSet set1, set2, set3, set4, set5, set6, set7;

        set1 = new_line_set(yval_1,"anger",col_ang);
        set2 = new_line_set(yval_2,"calm", col_cal);
        set3 = new_line_set(yval_3,"disgust", col_dis);
        set4 = new_line_set(yval_4,"fear", col_fea);
        set5 = new_line_set(yval_5,"happy", col_hap);
        set6 = new_line_set(yval_6,"sad", col_sad);
        set7 = new_line_set(yval_7,"surprised", col_sur);

        LineData data_lines = new LineData(set1,set2,set3,set4,set5,set6,set7);

        linechart.setData(data_lines);
        XAxis xAxis = linechart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        YAxis yAxis_L = linechart.getAxisLeft();
        YAxis yAxis_R = linechart.getAxisRight();
        yAxis_L.setDrawAxisLine(false);
        yAxis_R.setDrawAxisLine(false);

        xAxis.setAxisLineColor(Color.parseColor(col_back));
        yAxis_L.setGridColor(Color.parseColor(col_back));
        yAxis_R.setGridColor(Color.parseColor(col_back));

        linechart.getDescription().setEnabled(false);
//        linechart.animateX(500);
    }

    private LineDataSet new_line_set(ArrayList<Entry> yval, String title, String color ){
        LineDataSet linedataset = new LineDataSet(yval,title);
        linedataset.setColor(Color.parseColor(color));
        linedataset.setDrawValues(false);
        linedataset.setCircleColor(Color.parseColor(color));
        linedataset.setDrawFilled(true);
        linedataset.setFillColor(Color.parseColor(color));
        linedataset.setFillAlpha(30);
        return(linedataset);
    }

    public String dominantEmotion(Context context){
        String dominant_emotion = "null";
        ArrayList<Integer> category_val = new ArrayList<>();
        category_val = chart_data(context);

        int maximum_value = max(category_val.get(0),max(category_val.get(1),
                            max(category_val.get(2),max(category_val.get(3),
                            max(category_val.get(4),max(category_val.get(5),category_val.get(6)))))));

        if (maximum_value == category_val.get(0)) {dominant_emotion =  "ang";}
        else if (maximum_value == category_val.get(1)) {dominant_emotion =  "cal";}
        else if (maximum_value == category_val.get(2)) {dominant_emotion =  "dis";}
        else if (maximum_value == category_val.get(3)) {dominant_emotion =  "fea";}
        else if (maximum_value == category_val.get(4)) {dominant_emotion =  "hap";}
        else if (maximum_value == category_val.get(5)) {dominant_emotion =  "sad";}
        else if (maximum_value == category_val.get(5)) {dominant_emotion =  "sur";}
        return(dominant_emotion);
    }

    public ArrayList<String> recommendTitles(Context context, ArrayList<String> emotion){
        myDatabaseSQlite mdb = new myDatabaseSQlite(context);
        ArrayList<String> titleRecommend = new ArrayList<>();
        for(int i = 0 ; i <=  emotion.size()-1; i++){
            titleRecommend.addAll(mdb.titleFromCategory( emotion.get(i))) ;
        }
        return titleRecommend;
    }
}
