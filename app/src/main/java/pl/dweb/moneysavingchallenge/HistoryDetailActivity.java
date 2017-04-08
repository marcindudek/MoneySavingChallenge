package pl.dweb.moneysavingchallenge;

import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.dweb.moneysavingchallenge.database.DBHelper;
import pl.dweb.moneysavingchallenge.model.ChallengeEntity;
import pl.dweb.moneysavingchallenge.model.DueEntity;

public class HistoryDetailActivity extends AppCompatActivity {

    @BindView(R.id.history_detail_purpose)
    TextView purposeField;

    @BindView(R.id.history_detail_challenge_id)
    TextView challengeIDField;

    @BindView(R.id.history_detail_amount)
    TextView amountField;

    @BindView(R.id.history_detail_dues)
    TextView duesField;

    @BindView(R.id.history_detail_start_date)
    TextView startDateField;

    @BindView(R.id.history_detail_finish_date)
    TextView finishDateField;

    @BindView(R.id.detailRecycler)
    RecyclerView recyclerView;

    private ChallengeEntity challenge;
    private DBHelper dbHelper;
    private Dao<ChallengeEntity, Integer> challengeDao;
    private Dao<DueEntity, Long> dueDao;
    private List<DueEntity> dues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);
        ButterKnife.bind(this);
        if(getIntent().hasExtra("challenge_id")) {
            try {
                dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
                dueDao = dbHelper.getDueyDao();
                challengeDao = dbHelper.getChallengeDao();
                int id = getIntent().getIntExtra("challenge_id", 1);
                challenge = challengeDao.queryForId(id);
                dues = dueDao.queryForEq("challenge_id", id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            challengeIDField.setText(challenge.getId().toString());
            purposeField.setText(challenge.getPurpose());
            amountField.setText(challenge.getAmount() + " " + challenge.getCurrency());
            duesField.setText(challenge.getDues().toString());
            startDateField.setText(sdf.format(challenge.getStartTimestamp()));
            String finishDate = (challenge.getFinishTimestamp() != null) ? sdf.format(challenge.getFinishTimestamp()) : getString(R.string.status_canceled);
            finishDateField.setText(finishDate);

            DetailsAdapter adapter = new DetailsAdapter(dues, this, challenge.getCurrency());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }
    }
}
