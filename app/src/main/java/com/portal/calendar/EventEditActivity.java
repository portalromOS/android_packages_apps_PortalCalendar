package com.portal.calendar;

import static java.lang.Math.abs;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.portal.calendar.Alarm.AlarmItem;
import com.portal.calendar.Alarm.AlarmScheduler;
import com.portal.calendar.Events.CalendarEventModel;
import com.portal.calendar.Events.CalendarEventSQL;
import com.portal.calendar.MonthDay.MonthDayViewHolder;
import com.portal.calendar.Utils.CalendarUtils;
import com.portal.calendar.Utils.ColorItemAdapter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class EventEditActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener
{
    //public static String CALENDAR_EVENT_BUNDLE_NAME = "CALENDAR_EVENT_BUNDLE_NAME";
    public static String CALENDAR_EVENT_BUNDLE_EVENT_ID="CALENDAR_EVENT_BUNDLE_EVENT_ID";

    private MediaPlayer mediaPlayer;
    private final ArrayList<String> alarmSoundValues = new ArrayList<>(Arrays.asList("", "alarm_clock_beep", "data_scaner", "rooster_crowing_in_the_morning"));
    private final ArrayList<Integer> alarmValues = new ArrayList<>(Arrays.asList(-1, 0, 1, 8, 24));

    private final ArrayList<String> colorValues = new ArrayList<>(Arrays.asList("highLight", "event01", "event02",
            "event03", "event04", "event05", "event06", "event07", "event08", "event09"));
    private EditText eventName;
    private TextView eventDate;
    private TextView eventTime;

    private View formDivDateEnd;

    private TextView eventDateEnd;
    private TextView eventTimeEnd;

    private View pickerOnUpdate = null;

    private SwitchCompat eventAllDay;

    private Spinner eventAlarm;

    private Button alarmPermissionRequestBtn;
    private View formDivAlarmSound;
    private Spinner eventAlarmSound;

    private ImageButton playSoundIconBtn;
    private TextView eventDetail;

    private Spinner eventColor;

    private ActivityResultLauncher<String[]> permissionResultLauncher;
    private boolean hasPermissionNotification = false;

    private boolean inNotificationDebug = false; //para permitir que alertas "passados" sejam disparados de imediato

    private CalendarEventSQL sqlHelper;
    CalendarEventModel model = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        setupGui();

        sqlHelper = new CalendarEventSQL(this);

        long eventId = getIntent().getLongExtra(CALENDAR_EVENT_BUNDLE_EVENT_ID, -1);
        if(eventId >= 0)
            model = sqlHelper.getById((int)eventId);

        /*
        Bundle b = getIntent().getExtras();
        if(b != null){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                model =  b.getSerializable(CALENDAR_EVENT_BUNDLE_NAME,CalendarEventModel.class) ;
            }else{
                model =  (CalendarEventModel) b.getSerializable(CALENDAR_EVENT_BUNDLE_NAME) ;
            }
        }
        */

        if(model == null){
            model = new CalendarEventModel();
            findViewById(R.id.deleteIconBtn).setVisibility(View.GONE);
            resetForm();
        }
        else{
            findViewById(R.id.saveIconBtn).setVisibility(View.GONE);
            updateUI();
        }

        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                hasPermissionNotification = true;
                /*
                if(result.get(Manifest.permission.POST_NOTIFICATIONS)!=null){
                    hasPermissionNotification = result.get(Manifest.permission.POST_NOTIFICATIONS);
                }
                */
                updateUI();
            }
        });
        requestPermission();
    }
    private void setupGui(){

        eventName = findViewById(R.id.eventName);
        eventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                model.name = String.valueOf(eventName.getText());
            }
        });

        eventDate = findViewById(R.id.eventDate);
        eventTime = findViewById(R.id.eventTime);

        eventAllDay = findViewById(R.id.eventAllDay);
        eventAllDay.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            model.allDay = isChecked;
            updateUI();
        });

        formDivDateEnd= findViewById(R.id.formDivDateEnd);
        eventDateEnd = findViewById(R.id.eventDateEnd);
        eventTimeEnd = findViewById(R.id.eventTimeEnd);

        alarmPermissionRequestBtn = findViewById(R.id.alarmPermissionRequestBtn);
        eventAlarm = findViewById(R.id.eventAlarm);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.eventAlarms, R.layout.custom_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, getAlarmStrings());
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        eventAlarm.setAdapter(adapter);
        eventAlarm.setOnItemSelectedListener(this);

        formDivAlarmSound = findViewById(R.id.formDivAlarmSound);

        eventAlarmSound = findViewById(R.id.eventAlarmSound);
        ArrayAdapter<String> adapterSound = new ArrayAdapter<>(
                this,
                R.layout.custom_spinner_item,
                CalendarUtils.getStringResourcesArray(this, alarmSoundValues, "alarmSound_")
        );
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        eventAlarmSound.setAdapter(adapterSound);
        eventAlarmSound.setOnItemSelectedListener(this);

        playSoundIconBtn = findViewById(R.id.playSoundIconBtn);

        eventDetail = findViewById(R.id.eventDetail);
        eventDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                model.detail = String.valueOf(eventDetail.getText());
            }
        });

        eventColor = findViewById(R.id.eventColor);
        ColorItemAdapter adapterColor = new ColorItemAdapter(this,  colorValues);
        eventColor.setAdapter(adapterColor);
        eventColor.setOnItemSelectedListener(this);

    }
    private ArrayList<String> getAlarmStrings() {
        ArrayList<String> alarmNames = new ArrayList<>();

        for (Integer val:alarmValues) {
            String stringConst = "eventAlarms_";
            stringConst += (val < 0) ? "N"+abs(val) : val;
            alarmNames.add(CalendarUtils.getStringResourceByName(this, stringConst));
        }
        return alarmNames;
    }

    private void updateUI(){
        eventName.setText(model.name);
        eventDetail.setText(model.detail);

        eventTime.setText(CalendarUtils.formTime(model.time));
        eventDate.setText(CalendarUtils.formDate(model.date));
        eventTimeEnd.setText(CalendarUtils.formTime(model.timeEnd));
        eventDateEnd.setText(CalendarUtils.formDate(model.dateEnd));

        eventAllDay.setChecked(model.allDay);

        if(model.allDay){
            eventTime.setVisibility(View.GONE);
            formDivDateEnd.setVisibility(View.GONE);
        }
        else{
            eventTime.setVisibility(View.VISIBLE);
            formDivDateEnd.setVisibility(View.VISIBLE);
        }

        eventAlarm.setSelection(CalendarUtils.getListPositionByValue(alarmValues, model.alarm),true);

        if(hasPermissionNotification){
            eventAlarm.setVisibility(View.VISIBLE);
            alarmPermissionRequestBtn.setVisibility(View.GONE);
            if(model.alarm == -1)
                formDivAlarmSound.setVisibility(View.GONE);
            else
                formDivAlarmSound.setVisibility(View.VISIBLE);


            if(model.alarmSoundName.equals(""))
                playSoundIconBtn.setVisibility(View.GONE);
            else
                playSoundIconBtn.setVisibility(View.VISIBLE);

            eventAlarmSound.setSelection(CalendarUtils.getListPositionByValue(alarmSoundValues, model.alarmSoundName),true);
        }
        else{
            eventAlarm.setVisibility(View.GONE);
            formDivAlarmSound.setVisibility(View.GONE);
            alarmPermissionRequestBtn.setVisibility(View.VISIBLE);
        }

        eventColor.setSelection(CalendarUtils.getListPositionByValue(colorValues, model.color),true);



    }
    private void resetForm(){
        model.publicReset(CalendarUtils.selectedDate, LocalTime.of( 8,0));
        updateUI();

    }
    public void toggleSoundAction(View view) {
        if(!model.alarmSoundName.equals("")){
            @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier("raw/"+model.alarmSoundName, null, this.getPackageName());

            if (mediaPlayer != null){
                if(mediaPlayer.isPlaying()){
                    stopSoundAction(playSoundIconBtn);
                    return;
                }
                else{
                    mediaPlayer.release();
                }
            }

            mediaPlayer = MediaPlayer.create(view.getContext(), resId);
            mediaPlayer.start();
            playSoundIconBtn.setImageResource(R.drawable.baseline_stop_circle_24);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSoundAction(playSoundIconBtn);
                }
            });
        }
    }
    public void stopSoundAction(View view) {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            playSoundIconBtn.setImageResource(R.drawable.baseline_play_circle_outline_24);
        }
    }
    public void updatePermissionsAction(View view) {
        requestPermission();
    }

    public void closeEventAction(View view) {
        finish();
    }

    public void saveEventAction(View view) {
        if(model.isValid(this, true)){
            long result = sqlHelper.addOrUpdate(model);
            if(result>0){
                model.id = result;
                if(model.alarm >= 0){

                    AlarmItem ai = new AlarmItem((int)model.id, model.name, model.detail, model.alarmSoundName);
                    AlarmScheduler as = new AlarmScheduler(this);

                    if(as.validTime(model.getAlarmDateTime()) || inNotificationDebug){
                        as.schedule(ai, model.getAlarmDateTime());
                        CalendarUtils.showMsg(this, R.string.event_form_alarmAdded);
                    }
                    else{
                        CalendarUtils.showMsg(this, R.string.event_form_alarmOutdated);
                    }

                }
                finish();
            }
        }
        else{
            //CalendarUtils.showMsg(this, R.string.event_form_invalidFields);
        }
    }
    public void deleteEventAction(View view) {
        if(sqlHelper.delete(model)){

            AlarmItem ai = new AlarmItem((int)model.id, model.name, model.detail, model.alarmSoundName);
            AlarmScheduler as = new AlarmScheduler(this);
            as.cancel(ai);

            finish();
        }
    }
    public void popUpDatePickerAction(View view) {
        pickerOnUpdate = view;
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.CustomDatePickerDialog, this, model.date.getYear(), model.date.getMonthValue(), model.date.getDayOfMonth());
        dialog.show();
    }

    public void popUpTimePickerAction(View view) {
        pickerOnUpdate = view;
        TimePickerDialog dialog = new TimePickerDialog(this, R.style.CustomTimePickerDialog,this, model.time.getHour(), model.time.getMinute(), true);
        dialog.invalidateOptionsMenu();
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if(pickerOnUpdate == eventDate){
            model.updateDate(year, monthOfYear, dayOfMonth);
            if(model.allDay){
                model.updateDateEnd(year, monthOfYear, dayOfMonth);
            }
        }
        else if(pickerOnUpdate == eventDateEnd){
            model.updateDateEnd(year, monthOfYear, dayOfMonth);
        }


        updateUI();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        if(pickerOnUpdate == eventTime){
            model.updateTime(hour,minute);

            if(model.allDay){
                model.updateTimeEnd(hour,minute);
            }
        }
        if(pickerOnUpdate == eventTimeEnd){
            model.updateTimeEnd(hour,minute);
        }

        updateUI();
        pickerOnUpdate = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        if(parent == eventAlarm){
            model.alarm = alarmValues.get(pos);
        }
        else if(parent == eventAlarmSound){
            model.alarmSoundName = alarmSoundValues.get(pos);
            stopSoundAction(playSoundIconBtn);
        }
        else if (parent == eventColor){
            model.color = colorValues.get(pos);
        }
        updateUI();
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void requestPermission(){
        ArrayList<String> permissionRequest = new ArrayList<>();
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermissionNotification = (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED);
            if(!hasPermissionNotification)
                permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        */

        hasPermissionNotification = (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED);
        if(!hasPermissionNotification)
            permissionRequest.add(Manifest.permission.SCHEDULE_EXACT_ALARM);


        hasPermissionNotification = true;

        if(!permissionRequest.isEmpty()){
            permissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }

    }
}