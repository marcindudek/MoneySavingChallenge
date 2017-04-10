package pl.dweb.moneysavingchallenge;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static pl.dweb.moneysavingchallenge.MainActivity.SHARED_PREFERENCES_NAME;

public class NotificationSettingsActivity extends AppCompatActivity {


    @BindView(R.id.week_day_spinner)
    protected Spinner weekDaySpinner;

    @BindView(R.id.notification_switch)
    Switch notificationSwitch;

    @BindView(R.id.radioGroup)
    RadioGroup intervalRadio;

    @BindView(R.id.notify_timedate_label)
    TextView dateTimeLabel;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    protected static TextView notifyHourField;
    protected static TextView notifyMonthField;
    private SharedPreferences preferences;

    protected static int hour;
    protected static int minute;
    private int weekDay = -1;
    private int monthDay = -1;
    private boolean enbled = true;
    private int interval = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        notifyHourField = (TextView) findViewById(R.id.notify_hour);
        notifyMonthField = (TextView) findViewById(R.id.notify_month);
        preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        restoreSettings();
        notificationSwitch.setChecked(enbled);
        intervalRadio.check(interval);
        ArrayAdapter<CharSequence> weeksAdapter = ArrayAdapter.createFromResource(this, R.array.days_of_week, android.R.layout.simple_spinner_item);
        weeksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekDaySpinner.setAdapter(weeksAdapter);
        if(weekDay > -1) {
            weekDaySpinner.setSelection(weekDay-1);
        }
        weekDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weekDay = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        notifyHourField.setText(hour + ":" + minute);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, IntroductionActivity.class));
                break;
            case R.id.action_search:
//              startActivity(new Intent(this, SearchNewsActivity.class));
                break;
        }
        return true;
    }

    @OnClick(R.id.notify_hour)
    public void setHour(){
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }

    @OnClick(R.id.cancel_button)
    public void finishActivity() {
        finish();
    }

    @OnClick(R.id.save_button)
    public void saveSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("enable_notifications", notificationSwitch.isChecked());
        if(!notificationSwitch.isChecked()) {
            editor.commit();
            return;
        }
        editor.putInt("notifications_interval", intervalRadio.getCheckedRadioButtonId());
        switch(intervalRadio.getCheckedRadioButtonId()) {
            case R.id.notify_monthly_radio:
                editor.putInt("notification_monthday", monthDay);
                break;
            case R.id.notify_weekly_radio:
                editor.putInt("notification_weekday", weekDay);
                break;
        }

        editor.putInt("notifications_hour", hour);
        editor.putInt("notifications_minute", minute);

        editor.commit();
        finish();
    }

    private void restoreSettings() {
        enbled = preferences.getBoolean("enable_notifications", false);
        interval = preferences.getInt("notifications_interval", -1);
        switch(interval) {
            case R.id.notify_monthly_radio:
                notifyHourField.setVisibility(View.INVISIBLE);
                notifyMonthField.setVisibility(View.VISIBLE);
                dateTimeLabel.setText(getString(R.string.notify_monthday_label));
                break;
            case R.id.notify_weekly_radio:
                notifyHourField.setVisibility(View.VISIBLE);
                notifyMonthField.setVisibility(View.INVISIBLE);
                dateTimeLabel.setText(getString(R.string.notify_weekday_label));
                break;
            default:
                dateTimeLabel.setText(getString(R.string.notify_weekday_label));
                interval = R.id.notify_weekly_radio;
        }

        monthDay = preferences.getInt("notification_monthday", -1);
        weekDay = preferences.getInt("notification_weekday", -1);
        hour = preferences.getInt("notifications_hour", 0);
        minute = preferences.getInt("notifications_minute", 0);
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            hour = hourOfDay;
            NotificationSettingsActivity.minute = minute;
            notifyHourField.setText(hour + ":" + minute);

        }
    }


}
