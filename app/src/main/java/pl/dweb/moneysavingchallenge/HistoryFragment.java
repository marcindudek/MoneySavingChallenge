package pl.dweb.moneysavingchallenge;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.dweb.moneysavingchallenge.database.DBHelper;
import pl.dweb.moneysavingchallenge.model.ChallengeEntity;
import pl.dweb.moneysavingchallenge.model.ChallengeFinisher;
import pl.dweb.moneysavingchallenge.model.DueEntity;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements ChallengesAdapter.OnItemClickListener {

    @BindView(R.id.history_recycler_view)
    RecyclerView recyclerView;
    private DBHelper dbHelper;
    private Dao<ChallengeEntity, Integer> challengeDao;
    private Dao<DueEntity, Long> dueDao;
    private EventBus bus = EventBus.getDefault();
    private ChallengesAdapter adapter;
    private List<ChallengeEntity> history = new ArrayList<>();
    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, v);
        try{
            dbHelper = OpenHelperManager.getHelper(getContext(), DBHelper.class);
            challengeDao = dbHelper.getChallengeDao();
            dueDao = dbHelper.getDueyDao();
            history = getChallenges();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter = new ChallengesAdapter(history, this, getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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
    public void challengeFinished(ChallengeFinisher finisher) {
        if(finisher.getFinished()) {
            List<ChallengeEntity> entities = adapter.getList();
            try{
                dbHelper = OpenHelperManager.getHelper(getContext(), DBHelper.class);
                challengeDao = dbHelper.getChallengeDao();
                dueDao = dbHelper.getDueyDao();
                entities = challengeDao.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            adapter = new ChallengesAdapter(entities, this, getContext());
            recyclerView.setAdapter(adapter);
        }

    }


    @Override
    public void OnItemClicked(ChallengeEntity challenge) {
        Intent intent = new Intent(getContext(), HistoryDetailActivity.class);
        intent.putExtra("challenge_id", challenge.getId());
        startActivity(intent);
    }

    @Override
    public void OnLongItemClicked(final ChallengeEntity challengeEntity) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.remove_alert_title))
                .setMessage(getString(R.string.remove_alert_content))
                .setNegativeButton(getString(R.string.answer_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.answer_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            deleteChallenge(challengeEntity);
                            history = getChallenges();
                            adapter.setData(history);
                            adapter.notifyDataSetChanged();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void deleteChallenge(ChallengeEntity challengeEntity) throws SQLException {
        challengeDao.delete(challengeEntity);

    }

    private List<ChallengeEntity> getChallenges() throws SQLException {
        return challengeDao.queryForAll();
    }
}
