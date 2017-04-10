package pl.dweb.moneysavingchallenge;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.dweb.moneysavingchallenge.database.DBHelper;
import pl.dweb.moneysavingchallenge.model.ChallengeEntity;
import pl.dweb.moneysavingchallenge.model.ChallengeFinisher;
import pl.dweb.moneysavingchallenge.model.DueEntity;

import static android.content.Context.MODE_PRIVATE;
import static pl.dweb.moneysavingchallenge.MainActivity.CURRENT_CHALLENGE;
import static pl.dweb.moneysavingchallenge.MainActivity.SHARED_PREFERENCES_NAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChallengeFragment extends Fragment {


    @BindView(R.id.percent)
    TextView percentField;

    @BindView(R.id.empty_info)
    TextView emptyInfoField;

    @BindView(R.id.purpose_show)
    TextView purposeField;

    @BindView(R.id.saved_show)
    TextView savedSumField;

    @BindView(R.id.date_start_show)
    TextView amountField;

    @BindView(R.id.progressBar)
    CircularProgressBar progressBar;

    @BindView(R.id.current_due_show)
    TextView currentDueField;

    @BindView(R.id.all_dues_show)
    TextView allDuesField;

    @BindView(R.id.due_amount)
    TextView dueAmountField;

    @BindView(R.id.purpose_label)
    TextView purposeLabelField;

    @BindView(R.id.due_label)
    TextView dueLabelField;

    @BindView(R.id.saved_label)
    TextView savedLabelField;

    @BindView(R.id.amount_label)
    TextView amountLabelField;

    @BindView(R.id.current_due_label)
    TextView currentDueLabelField;

    @BindView(R.id.due_separator)
    TextView dueSeparatorField;

    @BindView(R.id.week_1_buton)
    Button weekButton1;

    private Integer index;
    private DBHelper dbHelper;
    private static Dao<ChallengeEntity, Integer> challengeDao;
    private Dao<DueEntity, Long> dueDao;
    private static ChallengeEntity challenge;
    private List<DueEntity> dues;
    private SharedPreferences preferences;
    private Integer amount;
    private EventBus bus = EventBus.getDefault();
    private boolean finished = false;
    private boolean enabled;
    private Intervals interval;
    private int monthDay, weekDay, hour, minute;


    public ChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_challenge, container, false);

        ButterKnife.bind(this, v);

        try{
            dbHelper = OpenHelperManager.getHelper(getContext(), DBHelper.class);
            challengeDao = dbHelper.getChallengeDao();
            dueDao = dbHelper.getDueyDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        preferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        readSettings();
        int id = preferences.getInt(CURRENT_CHALLENGE, 0);

        if(id == 0) {
            setEmptyView();
            return v;
        }

        try {
            challenge = challengeDao.queryForId(id);
            dues = dueDao.queryForEq("challenge_id", id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(challenge == null) {
            finishChallenge();
            getActivity().getParent().recreate();
        }
        prepareView();
        restoreProgress();
        return v;
    }

    private void setEmptyView() {
        emptyInfoField.setVisibility(View.VISIBLE);
        percentField.setVisibility(View.INVISIBLE);
        purposeField.setVisibility(View.INVISIBLE);
        savedSumField.setVisibility(View.INVISIBLE);
        amountField.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        currentDueField.setVisibility(View.INVISIBLE);
        allDuesField.setVisibility(View.INVISIBLE);
        dueAmountField.setVisibility(View.INVISIBLE);
        purposeLabelField.setVisibility(View.INVISIBLE);
        dueLabelField.setVisibility(View.INVISIBLE);
        weekButton1.setVisibility(View.INVISIBLE);
        savedLabelField.setVisibility(View.INVISIBLE);
        amountLabelField.setVisibility(View.INVISIBLE);
        currentDueLabelField.setVisibility(View.INVISIBLE);
        dueSeparatorField.setVisibility(View.INVISIBLE);
    }

    private void setChallengeView() {
        emptyInfoField.setVisibility(View.INVISIBLE);
        percentField.setVisibility(View.VISIBLE);
        purposeField.setVisibility(View.VISIBLE);
        savedSumField.setVisibility(View.VISIBLE);
        amountField.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        currentDueField.setVisibility(View.VISIBLE);
        allDuesField.setVisibility(View.VISIBLE);
        dueAmountField.setVisibility(View.VISIBLE);
        purposeLabelField.setVisibility(View.VISIBLE);
        dueLabelField.setVisibility(View.VISIBLE);
        weekButton1.setVisibility(View.VISIBLE);
        savedLabelField.setVisibility(View.VISIBLE);
        amountLabelField.setVisibility(View.VISIBLE);
        currentDueLabelField.setVisibility(View.VISIBLE);
        dueSeparatorField.setVisibility(View.VISIBLE);
    }


    private void prepareView() {
        setChallengeView();
        amount = challenge.getAmount();
        index = 0;
        generateButtons();
        calculateSavedPercent();
        if(TextUtils.isEmpty(challenge.getPurpose())) {
            purposeLabelField.setVisibility(View.INVISIBLE);
        } else {
            purposeLabelField.setVisibility(View.VISIBLE);

        }
        dueLabelField.setVisibility(View.VISIBLE);
        purposeField.setText(challenge.getPurpose());
        amountField.setText(amount.toString() + " " + challenge.getCurrency());
        percentField.setText(calculateSavedPercent() +"%");
        dueAmountField.setText(dues.get(index).getDue() + " " + challenge.getCurrency());
        currentDueField.setText(String.valueOf(index+1));
        allDuesField.setText(String.valueOf(dues.size()));
    }

    private void readSettings() {
        enabled = preferences.getBoolean("enable_notifications", false);
        switch(preferences.getInt("notifications_interval", -1)) {
            case R.id.notify_monthly_radio:
                interval = Intervals.MONTHLY;
                break;
            case R.id.notify_weekly_radio:
                interval = Intervals.WEEKLY;
                break;
            default:
                interval = Intervals.NONE;
        }
        monthDay = preferences.getInt("notification_monthday", -1);
        weekDay = preferences.getInt("notification_weekday", -1);
        hour = preferences.getInt("notifications_hour", -1);
        minute = preferences.getInt("notifications_minute", -1);
    }


    private void restoreProgress() {
        for(int i = 0; i < dues.size(); i++) {
            if(dues.get(i).getTimestamp() != null) {
                updateDues();
            } else {
                break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);

    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true)
    public void challengeAvailable(ChallengeEntity challengeEntity) {

            challenge = challengeEntity;

            amount = challenge.getAmount();
        try {
            challengeDao.create(challenge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        editPreferences(challenge.getId());
        dues = createDues(challenge.getAmount(), challenge.getDues());
        finished = false;
        bus.removeStickyEvent(challengeEntity);
        prepareView();
    }

    public static void cancelChallenge() {
        challenge.setCanceled(true);
        try {
            challengeDao.update(challenge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        challenge = null;
    }

    public static boolean isActiveChallenge() {
        return challenge != null;
    }

    private Integer calculateSavedPercent() {
        Integer result = 0;
        try {
            List<DueEntity> dues = dueDao.queryBuilder().where().eq("challenge_id", challenge.getId()).and().isNotNull("timestamp").query();
            for (DueEntity d : dues) {
                result += d.getDue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        savedSumField.setText(result.toString() + " " + challenge.getCurrency());
        result = result * 100 / amount;
        percentField.setText(result +"%");
        progressBar.setProgressWithAnimation(result);
        if(result == 100) {
            removeFromPreferences();
            finishChallenge();
        }
        return result;

    }

    private void editPreferences(Integer id) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_CHALLENGE, id);
        editor.commit();
    }

    private void finishChallenge() {
        removeFromPreferences();
        try {
            challenge.setFinishTimestamp(new Date());
            challengeDao.update(challenge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ChallengeFinisher finisher = new ChallengeFinisher(challenge.getId(), true);
        bus.post(finisher);
        dueLabelField.setVisibility(View.INVISIBLE);
        dueAmountField.setText(getString(R.string.challenge_completed));
        challenge = null;
    }

    private void removeFromPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(CURRENT_CHALLENGE);
        editor.commit();
    }

    private List<DueEntity> createDues(Integer amount, Integer weeks) {

        int fours = weeks / 4;
        int due = (amount / weeks) / 5;
        int[] dues = new int[weeks];
        for(int i = 0; i < (weeks); i+=4) {
            dues[i] =  (int)  (due * 1.25) * 5;
            dues[i+1] = (int) (due * 1.125) * 5;
            dues[i+2] = (int) (due * 0.875) * 5;
            dues[i+3] = (int) (due * 0.75) * 5;
        }

        int dues_tmp = 0;
        for(int i = 0; i < dues.length; i++) {
            dues_tmp += dues[i];
        }
        int dd = amount - dues_tmp;
        int ddd = dd / (weeks/2);
        ddd = (ddd > 5) ? 5 : 0;
        for(int i = 0; i < weeks; i+=4) {
            dues[i + 1] += ddd;
            dues[i + 2] += ddd;
        }

        dues_tmp = 0;

        for(int i = 0; i < dues.length; i++) {
            dues_tmp += dues[i];
        }

        dd = amount - dues_tmp;

        while(dd > 0) {
            for (int i = 3; i < weeks; i += 4) {
                if (dd > 0) {
                    dues[i] += 5;
                    dd -= 5;
                }
            }
        }


        dues_tmp = 0;

        for(int i = 0; i < dues.length; i++) {
            dues_tmp += dues[i];
        }

        due *= 5;
        int change = amount - (due * weeks);
        List<DueEntity> dueList = new ArrayList<>(dues.length);
        for(int i = 0; i< dues.length; i++) {
            DueEntity dueEntity =new DueEntity(challenge, dues[i], i+1, null);
            try {
                dueDao.create(dueEntity);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dueList.add(dueEntity);

        }

        return dueList;

    }

    private void generateButtons() {
        weekButton1.setClickable(true);
        weekButton1.setBackground(getResources().getDrawable(R.drawable.round_button));
//                    v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        weekButton1.setPressed(false);

        weekButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDues();
                if(index < dues.size()) {
//                    Intent intent = new Intent(getContext(), Receiver.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
//                    AlarmManager am = (AlarmManager)getContext().getSystemService(ALARM_SERVICE);
//                    am.set(am.RTC_WAKEUP, am.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
                } else {
                    v.setClickable(false);
                    v.setBackground(getResources().getDrawable(R.drawable.round_complete_button));
//                    v.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    v.setPressed(true);
                }
            }
        });



    }

    @OnClick(R.id.week_1_buton)
    public void clicked() {
        updateDues();
        if(index < dues.size() && enabled) {
            setReminder();
        } else {
            weekButton1.setText("Challenge completed!");
            weekButton1.setClickable(false);
            weekButton1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            weekButton1.setPressed(true);
        }
    }

    private void setReminder() {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Long alarmTime = null;
        switch (interval) {
            case WEEKLY:
                alarmTime = calculateWeeklyTime();
                break;
            case MONTHLY:
                alarmTime = calculateMonthlyTime();
        }
        Intent intent = new Intent(getContext(), Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
        alarmManager.set(AlarmManager.RTC, alarmTime, pendingIntent);

    }

    private Long calculateWeeklyTime() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);

        int daysDiff = (currentDay < weekDay) ? (weekDay - currentDay) : currentDay + weekDay - 1;
        calendar.add(Calendar.DAY_OF_MONTH, daysDiff);
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }

    private Long calculateMonthlyTime() {
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        if(currentDay < monthDay) {
            calendar.add(Calendar.DAY_OF_MONTH, monthDay - currentDay);
        } else {
            calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) + 1));
        }
        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTimeInMillis();
    }


    private void updateDues() {
        try {
            dues.get(index).setTimestamp(new Date());
            dueDao.update(dues.get(index));
            calculateSavedPercent();
            index++;
            if(index < dues.size()) {
                dueAmountField.setText(dues.get(index).getDue() + " " + challenge.getCurrency());
                currentDueField.setText(dues.get(index).getDueNumber().toString());
            }

            allDuesField.setText(String.valueOf(dues.size()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
