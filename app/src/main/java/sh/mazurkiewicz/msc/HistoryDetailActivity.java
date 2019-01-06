package sh.mazurkiewicz.msc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sh.mazurkiewicz.msc.database.DBHelper;
import sh.mazurkiewicz.msc.model.ChallengeEntity;
import sh.mazurkiewicz.msc.model.DueEntity;

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

    @BindView(R.id.history_detail_finish_label)
    TextView finishDateLabelField;

    @BindView(R.id.detailRecycler)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
        setSupportActionBar(toolbar);
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
            String finishDateLabel = (challenge.getFinishTimestamp() != null) ? getString(R.string.date_finish_label) : getString(R.string.status_label);
            String finishDate = (challenge.getFinishTimestamp() != null) ? sdf.format(challenge.getFinishTimestamp()) :
                    challenge.getCanceled() ? getString(R.string.status_canceled) : getString(R.string.status_in_progress);
            finishDateLabelField.setText(finishDateLabel);
            finishDateField.setText(finishDate);

            DetailsAdapter adapter = new DetailsAdapter(dues, challenge.getCurrency());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }
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
            case R.id.action_notify:
//              startActivity(new Intent(this, SearchNewsActivity.class));
                break;
        }
        return true;
    }
}
