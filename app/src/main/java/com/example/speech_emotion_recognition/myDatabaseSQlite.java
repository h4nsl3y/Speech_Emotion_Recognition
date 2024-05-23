package com.example.speech_emotion_recognition;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class myDatabaseSQlite extends SQLiteOpenHelper {

    private Context context;
    private static final String DB_NAME="Record.DB";

    private static final int DB_VERSION= 2;
    private static final String TABLE1_NAME="myLibrary";
    private static final String TABLE2_NAME="myRecord";
    private static final String TABLE3_NAME="myFiles";
    private static final String COL_ID = "ID"; //primary key
    private static final String COL_TYPE = "Type"; // recordings or files
    private static final String COL_NAME = "Name"; // name of the recording or file
    //emotions
    private static final String COL_ANG = "Anger";
    private static final String COL_CAL = "Calmness";
    private static final String COL_FEA = "Fear";
    private static final String COL_DIS = "Disgust";
    private static final String COL_HAP = "Happiness";
    private static final String COL_SAD = "Sadness";
    private static final String COL_SUR = "Surprised";
    private static final String COL_CAT = "category";



    public myDatabaseSQlite(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query_1 =
                "CREATE TABLE " + TABLE1_NAME +
                " ("+
                        COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                        COL_TYPE +" TEXT, "+
                        COL_NAME +" TEXT "+
                ");";

        String query_2 =
                "CREATE TABLE " + TABLE2_NAME +
                " ("+
                        COL_NAME + " TEXT, " +
                        COL_ANG + " DECIMAL(10,5), " +
                        COL_CAL + " DECIMAL(10,5), " +
                        COL_DIS + " DECIMAL(10,5), " +
                        COL_FEA + " DECIMAL(10,5), " +
                        COL_HAP + " DECIMAL(10,5), " +
                        COL_SAD + " DECIMAL(10,5), " +
                        COL_SUR + " DECIMAL(10,5) " +
                " );";

        String query_3 =
                "CREATE TABLE " + TABLE3_NAME +
                        " ("+
                        COL_NAME + " TEXT, " +
                        COL_CAT + " TEXT " +
                        " );";

        db.execSQL(query_1);
        db.execSQL(query_2);
        db.execSQL(query_3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3_NAME);
        onCreate(db);
    }

    public void addData_record(String name,
                        Double ang, Double cal,
                        Double dis, Double fea,
                        Double hap, Double sad,
                               Double sur){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        cv1.put(COL_TYPE,"recording");
        cv1.put(COL_NAME,name);

        cv2.put(COL_NAME,name);
        cv2.put(COL_ANG,ang);
        cv2.put(COL_CAL,cal);
        cv2.put(COL_DIS,dis);
        cv2.put(COL_FEA,fea);
        cv2.put(COL_HAP,hap);
        cv2.put(COL_SAD,sad);
        cv2.put(COL_SUR,sur);

        long result1 = db.insert(TABLE1_NAME,null, cv1);
        if (result1 ==-1){
            Toast.makeText(context, "Table_1 error", Toast.LENGTH_SHORT).show();
        }
        long result2 = db.insert(TABLE2_NAME,null, cv2);
        if (result2 ==-1){
            Toast.makeText(context, "Table_3 error", Toast.LENGTH_SHORT).show();
        }
    }

    public void addData_file(String name, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();

        name = name.replaceAll("[-+^]*", " ");

        cv1.put(COL_TYPE,"file");
        cv1.put(COL_NAME,name);

        cv2.put(COL_NAME,name);
        cv2.put(COL_CAT,category);;

        long result1 = db.insert(TABLE1_NAME,null, cv1);
        if (result1 ==-1){
            Toast.makeText(context, "Table_1 error", Toast.LENGTH_SHORT).show();
        }
        long result2 = db.insert(TABLE3_NAME,null, cv2);
        if (result2 ==-1){
            Toast.makeText(context, "Table_3 error", Toast.LENGTH_SHORT).show();
        }
    }


    public String readFileData(Context context, String song_name){
        String category = "";
        String query = " SELECT DISTINCT " +  COL_CAT + "  "+
                " FROM " + TABLE3_NAME +
                " WHERE "+  COL_NAME + " = \"" + song_name + "\" ;";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor=db.rawQuery(query,null);
        }
        if(cursor.getCount()!=0){
            while(cursor.moveToNext()) {
                category = category + cursor.getString(0);
            }
        }
        return category;
    }

    public Cursor readLastData(){
        String query = "SELECT * FROM "+ TABLE2_NAME +
                " WHERE "+COL_NAME+"="+ " (" +
                                           "SELECT "+COL_NAME+" FROM "+TABLE1_NAME+" " +
                                                  "WHERE " +COL_TYPE+ " = 'recording' " +
                                                  "ORDER BY " +COL_ID+" DESC LIMIT 1) " +
                ";";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor=db.rawQuery(query,null);
        }
        return cursor;
    }


    public Cursor readAllMusicData(){
        String query = " SELECT DISTINCT ( " + COL_NAME + " ) ,"+
                COL_CAT+
                " FROM " + TABLE3_NAME + " ; " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor=db.rawQuery(query,null);
        }
        return cursor;
    }

    public ArrayList<String> titleFromCategory(String category){

        ArrayList<String> titleRecommend = new ArrayList<>();
        String query = " SELECT " + COL_NAME + " FROM " + TABLE3_NAME + " " +
                       " WHERE " + COL_CAT + " = '" + category + "' ; " ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db!=null){
            cursor=db.rawQuery(query,null);
            while(cursor.moveToNext()){
                titleRecommend.add(cursor.getString(0));
            }
        }
        cursor.close();
        return titleRecommend;
    }

}
