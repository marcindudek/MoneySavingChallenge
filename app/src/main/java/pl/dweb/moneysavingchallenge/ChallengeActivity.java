package pl.dweb.moneysavingchallenge;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.query.In;
import com.wefika.flowlayout.FlowLayout;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.dweb.moneysavingchallenge.database.DBHelper;
import pl.dweb.moneysavingchallenge.model.ChallengeEntity;
import pl.dweb.moneysavingchallenge.model.DueEntity;

public class ChallengeActivity extends AppCompatActivity {


    @BindView(R.id.percent)
    TextView textView;

    @BindView(R.id.dues_linear_layout)
    com.wefika.flowlayout.FlowLayout linear;

    private DBHelper dbHelper;
    private Dao<ChallengeEntity, Integer> challengeDao;
    private Dao<DueEntity, Long> dueDao;
    private ChallengeEntity challenge;
    public static final String SHARED_PREFERENCES_NAME = "msc.preferences";
    public static final String CURRENT_CHALLENGE = "current_challenge";
    private SharedPreferences preferences;
    private Integer amount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        ButterKnife.bind(this);
        try{
            dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
            challengeDao = dbHelper.getChallengeDao();
            dueDao = dbHelper.getDueyDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<DueEntity> dues = new ArrayList<>();
        preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        if(getIntent().hasExtra(MainActivity.TAG + "/weeks") && getIntent().hasExtra(MainActivity.TAG + "/amount")) {
            int weeks = getIntent().getIntExtra(MainActivity.TAG + "/weeks", 1);
            amount = getIntent().getIntExtra(MainActivity.TAG + "/amount", 1);
            String purpose = getIntent().getStringExtra(MainActivity.TAG + "/purpose");

            challenge = new ChallengeEntity(amount, weeks, purpose);
            try {
                challengeDao.create(challenge);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            editPreferences(challenge.getId());
            dues = createDues(amount, weeks);

        } else {
            try {
                int id = preferences.getInt(CURRENT_CHALLENGE, 0);
                challenge = challengeDao.queryForId(id);
                dues = dueDao.queryForEq("challenge_id", id);
                amount = challenge.getAmount();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        generateButtons(dues);
        textView.setText(calculateSavedPercent() +"%");
        if(calculateSavedPercent() == 100) {
            try {
                challenge.setFinishTimestamp(new Date());
                challengeDao.update(challenge);
                finishChallenge();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


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

        return result * 100 / amount;
    }

   private void editPreferences(Integer id) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_CHALLENGE, id);
        editor.commit();
    }

    private void finishChallenge() {
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

    private void generateButtons(final List<DueEntity> dues) {
        for(final DueEntity d : dues){
            Button button = new Button(new android.view.ContextThemeWrapper(this,
                    android.R.style.Widget_Material_Button_Colored),
                    null,
                    android.R.style.Widget_Material_Button_Colored);
            button.setLayoutParams(new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT));
            button.setText(d.getDue().toString());
            button.setId(d.getDueNumber());
            button.setEnabled(d.getTimestamp() == null);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        d.setTimestamp(new Date());
                        dueDao.update(d);
                        recreate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            linear.addView(button);
        }
    }

}
