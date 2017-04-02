package pl.dweb.moneysavingchallenge;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.wefika.flowlayout.FlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.dweb.moneysavingchallenge.database.DBHelper;
import pl.dweb.moneysavingchallenge.model.ChallengeEntity;
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

    @BindView(R.id.purpose_show)
    TextView purposeField;

    @BindView(R.id.saved_show)
    TextView savedSumField;

    @BindView(R.id.amount_show)
    TextView amountField;

    @BindView(R.id.empty_challenge)
    TextView emptyChallengeField;

    @BindView(R.id.dues_linear_layout)
    com.wefika.flowlayout.FlowLayout linear;

    @BindView(R.id.progressBar)
    CircularProgressBar progressBar;

    private DBHelper dbHelper;
    private Dao<ChallengeEntity, Integer> challengeDao;
    private Dao<DueEntity, Long> dueDao;
    private ChallengeEntity challenge;

    private SharedPreferences preferences;
    private Integer amount;
    private EventBus bus = EventBus.getDefault();
    private boolean finished = false;

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

        List<DueEntity> dues = new ArrayList<>();

        preferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        int id = preferences.getInt(CURRENT_CHALLENGE, 0);

        if(id == 0) {
            return v;
        }

        try {
            challenge = challengeDao.queryForId(id);
            dues = dueDao.queryForEq("challenge_id", id);
            amount = challenge.getAmount();
        } catch (SQLException e) {
                e.printStackTrace();
        }

        generateButtons(dues);
        purposeField.setText(challenge.getPurpose());
        amountField.setText(amount.toString());
        percentField.setText(calculateSavedPercent() +"%");


        return v;
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

    @Subscribe
    public void challengeAvailable(ChallengeEntity challengeEntity) {
        if(challenge != null) {
            return;
        }
        List<DueEntity> dues = new ArrayList<>();
            challenge = challengeEntity;
            amount = challengeEntity.getAmount();
        try {
            challengeDao.create(challenge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        editPreferences(challenge.getId());
        dues = createDues(challenge.getAmount(), challenge.getDues());
        finished = false;
        percentField.setVisibility(View.VISIBLE);
        amountField.setText(amount.toString());
        purposeField.setText(challenge.getPurpose());
        progressBar.setVisibility(View.VISIBLE);
        generateButtons(dues);
        calculateSavedPercent();

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

        savedSumField.setText(result.toString());
        result = result * 100 / amount;
        percentField.setText(result +"%");
        progressBar.setProgressWithAnimation(result);
        if(result == 100) {
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
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(CURRENT_CHALLENGE);
//        percentField.setVisibility(View.INVISIBLE);
//        progressBar.setVisibility(View.INVISIBLE);
        editor.commit();
        try {
            challenge.setFinishTimestamp(new Date());
            challengeDao.update(challenge);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finished = true;
        bus.post(finished);
        challenge = null;
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
        linear.removeAllViewsInLayout();
        emptyChallengeField.setVisibility(View.INVISIBLE);
        for(final DueEntity d : dues){
            Button button = new Button(new android.view.ContextThemeWrapper(getContext(),
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
                        v.setEnabled(false);
                        calculateSavedPercent();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            linear.addView(button);
        }
    }

}
