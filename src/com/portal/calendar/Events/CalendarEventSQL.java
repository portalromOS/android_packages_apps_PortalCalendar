package com.portal.calendar.Events;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.portal.calendar.Utils.CalendarUtils;
import com.portal.calendar.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class CalendarEventSQL extends SQLiteOpenHelper {
    private Context context;

    private static final int DB_VERSION = 11;

    private static final String TABLE_NAME = "event";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_DETAIL = "detail";
    public static final String COL_DATE_TIME = "date_time";

    public static final String COL_ALARM = "alarm";
    public static final String COL_ALL_DAY = "allDay";
    public static final String COL_DATE_TIME_END = "date_time_end";
    public static final String COL_SOUND_NAME = "soundName";

    public static final String COL_COLOR = "color";


    public CalendarEventSQL(@Nullable Context context) {
        super(context, CalendarUtils.DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String q = "CREATE TABLE " + TABLE_NAME  + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                COL_NAME + " TEXT, " +
                COL_DETAIL + " TEXT ," +
                COL_DATE_TIME + " TEXT, " +
                COL_DATE_TIME_END + " TEXT, " +
                COL_ALL_DAY + " INTEGER, " +
                COL_ALARM + " INTEGER, " +
                COL_SOUND_NAME + " TEXT, " +
                COL_COLOR + " TEXT " +
        ");";

        db.execSQL(q);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //Retorna o id do modelo inserido ou atualizado
    public long addOrUpdate(CalendarEventModel model){
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_NAME, model.name);
        cv.put(COL_DETAIL, model.detail);
        cv.put(COL_DATE_TIME, CalendarUtils.toSQLite(model.date, model.time));
        cv.put(COL_DATE_TIME_END, CalendarUtils.toSQLite(model.dateEnd, model.timeEnd));
        cv.put(COL_ALL_DAY, model.allDay);
        cv.put(COL_ALARM, model.alarm);
        cv.put(COL_SOUND_NAME, model.alarmSoundName);
        cv.put(COL_COLOR, model.color);

        if(model.hasId()){
            result = db.update(TABLE_NAME, cv, COL_ID+"=?", new String[]{model.id+""});
            if(result == -1){
                CalendarUtils.showMsg(context, R.string.sql_calendarEvents_update);
            }
            else{
                result = model.id;
            }
        }
        else{
            result = db.insert(TABLE_NAME, null, cv);//return insertedId
            if(result == -1){
                CalendarUtils.showMsg(context, R.string.sql_calendarEvents_add);
            }
        }

        return result;
    }

    public boolean delete(CalendarEventModel model) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, COL_ID+"=?", new String[]{model.id+""});

        if(result == -1){
            CalendarUtils.showMsg(context, R.string.sql_calendarEvents_remove);
            return false;
        }
        return true;
    }



    public ArrayList<CalendarEventModel> getByDay_minInfo(LocalDate date){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CalendarEventModel> result = new ArrayList<CalendarEventModel>() ;
        if(db != null){
            String q = "SELECT " + COL_ID  + ", " + COL_NAME  + ", " + COL_DATE_TIME + ", " + COL_DATE_TIME_END  + ", " + COL_ALL_DAY + ", " +
                    COL_DETAIL + ", " + COL_COLOR +
                    " FROM " + TABLE_NAME  +
                    " WHERE " +" date(" + COL_DATE_TIME  + ") <= '" + CalendarUtils.toSQLite(date) +"' AND "+
                    " date(" + COL_DATE_TIME_END  + ") >= '" + CalendarUtils.toSQLite(date) + "'" +
                    " ORDER BY " + COL_ALL_DAY + " DESC , datetime(" + COL_DATE_TIME  + ")";

            Cursor cursor = null;

            cursor = db.rawQuery(q, null);

            while(cursor.moveToNext()){
                result.add(new CalendarEventModel(cursor));
            }
        }
        return result;
    }

    public CalendarEventModel getById(int eventId){
        SQLiteDatabase db = this.getReadableDatabase();
        CalendarEventModel result = null;
        if(db != null){
            String q = "SELECT * " +
                    " FROM " + TABLE_NAME  +
                    " WHERE " + COL_ID +" = " +  eventId;

            Cursor cursor = null;
            cursor = db.rawQuery(q, null);
            if (cursor.getCount()>0){
                cursor.moveToFirst();
                result = new CalendarEventModel(cursor);
            }
        }
        return result;
    }

    public boolean hasEventByDay(LocalDate date){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = false;
        if(db != null){
            String q = "SELECT COUNT(" + COL_ID  + ") " +
                    " FROM " + TABLE_NAME  +
                    " WHERE " +" date(" + COL_DATE_TIME  + ") <= '" + CalendarUtils.toSQLite(date) +"' AND "+
                    " date(" + COL_DATE_TIME_END  + ") >= '" + CalendarUtils.toSQLite(date) + "'";

            Cursor cursor = null;

            cursor = db.rawQuery(q, null);
            cursor.moveToFirst();
            int count= cursor.getInt(0);
            cursor.close();

            result = (count>0);

        }
        return result;
    }


    public ArrayList<CalendarEventModel> getEventsByDayMonthView(LocalDate date){
        ArrayList<CalendarEventModel> result = new ArrayList<CalendarEventModel>() ;
        SQLiteDatabase db = this.getReadableDatabase();

        if(db != null){
            String q = "SELECT " + COL_ID  + ", " + COL_DATE_TIME  + ", " + COL_DATE_TIME_END  +", " + COL_ALL_DAY  +", " + COL_COLOR  +
                    " FROM " + TABLE_NAME  +
                    " WHERE " +" date(" + COL_DATE_TIME  + ") <= '" + CalendarUtils.toSQLite(date) +"' AND "+
                    " date(" + COL_DATE_TIME_END  + ") >= '" + CalendarUtils.toSQLite(date) + "'" +
                    " ORDER BY " + COL_ALL_DAY + ", datetime(" + COL_DATE_TIME  + ")";

            Cursor cursor = null;

            cursor = db.rawQuery(q, null);

            while(cursor.moveToNext()){
                result.add(new CalendarEventModel(cursor));
            }
        }
        return result;
    }

    public ArrayList<CalendarEventModel> getFutureAlarms(){
        ArrayList<CalendarEventModel> result = new ArrayList<CalendarEventModel>() ;
        SQLiteDatabase db = this.getReadableDatabase();
        LocalDate now = LocalDate.now();

        if(db != null){
            String q = "SELECT " + COL_ID  + ", " + COL_NAME  + ", " + COL_DATE_TIME  + ", " + COL_ALARM  + ", " + COL_DETAIL  +
                    " FROM " + TABLE_NAME  +
                    " WHERE " +" date(" + COL_DATE_TIME  + ") >= '" + CalendarUtils.toSQLite(now) +"' AND "+ COL_ALARM + " >= 0"
                    ;

            Cursor cursor = null;

            cursor = db.rawQuery(q, null);

            while(cursor.moveToNext()){
                result.add(new CalendarEventModel(cursor));
            }
        }
        return result;
    }
}
