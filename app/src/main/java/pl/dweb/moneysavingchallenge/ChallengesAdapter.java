package pl.dweb.moneysavingchallenge;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.dweb.moneysavingchallenge.model.ChallengeEntity;

/**
 * Created by md on 3/29/17.
 */

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ChallengesHolder> {

    private List<ChallengeEntity> list;

    public ChallengesAdapter(List<ChallengeEntity> list) {
        this.list = list;
    }

    public ChallengesAdapter() {
        this.list = new ArrayList<>();
    }

    @Override
    public ChallengesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_challenge_item, parent, false);
        return new ChallengesHolder(view);
    }

    @Override
    public void onBindViewHolder(ChallengesHolder holder, int position) {
        holder.challengeEntity = list.get(position);
        holder.setData();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ChallengesHolder extends RecyclerView.ViewHolder {

        TextView amountField;
        TextView weeksField;
        TextView startField;
        TextView finishField;
        ChallengeEntity challengeEntity;

        public ChallengesHolder(View itemView) {
            super(itemView);
            amountField = (TextView) itemView.findViewById(R.id.challenge_item_amount);
            weeksField = (TextView) itemView.findViewById(R.id.challenge_item_weeks);
            startField = (TextView) itemView.findViewById(R.id.challenge_item_start);
            finishField = (TextView) itemView.findViewById(R.id.challenge_item_finish);
        }

        public void setData() {
            amountField.setText(challengeEntity.getAmount().toString());
            weeksField.setText(challengeEntity.getDues().toString());
            startField.setText(challengeEntity.getStartTimestamp().toString());
            String finishDate = (challengeEntity.getFinishTimestamp() != null) ? challengeEntity.getFinishTimestamp().toString() : "-";
            finishField.setText(finishDate);
        }
    }
}
