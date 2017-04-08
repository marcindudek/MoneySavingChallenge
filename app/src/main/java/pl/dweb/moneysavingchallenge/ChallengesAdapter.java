package pl.dweb.moneysavingchallenge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import pl.dweb.moneysavingchallenge.model.ChallengeEntity;

/**
 * Created by md on 3/29/17.
 */

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ChallengesHolder> {

    private List<ChallengeEntity> list;
    private OnItemClickListener listener;
    private Context context;

    public ChallengesAdapter(List<ChallengeEntity> list, OnItemClickListener listener, Context context) {
        this.list = list;
        this.listener = listener;
        this.context = context;
    }

    public ChallengesAdapter(OnItemClickListener listener) {
        this.list = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public ChallengesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_challenge_item, parent, false);
        return new ChallengesHolder(view, context, listener);
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

    public List<ChallengeEntity> getList() {
        return list;
    }

    public void setData(List<ChallengeEntity> list) {
        this.list = list;
    }


    public class ChallengesHolder extends RecyclerView.ViewHolder {

        TextView challengeIdField;
        TextView amountField;
        TextView weeksField;
        TextView startField;
        TextView finishField;
        TextView purposeField;
        ChallengeEntity challengeEntity;
        View item;
        Context context;
        TextView statusField;
        OnItemClickListener holderListener;

        public ChallengesHolder(View itemView, Context context, OnItemClickListener listener) {
            super(itemView);
            item = itemView;
            holderListener = listener;
            this.context = context;
            challengeIdField = (TextView) itemView.findViewById(R.id.challenge_id);
            amountField = (TextView) itemView.findViewById(R.id.amount_show);
            weeksField = (TextView) itemView.findViewById(R.id.dues_show);
            startField = (TextView) itemView.findViewById(R.id.date_start_show);
            finishField = (TextView) itemView.findViewById(R.id.date_finish_show);
            purposeField = (TextView) itemView.findViewById(R.id.purpose_show);
            statusField = (TextView) itemView.findViewById(R.id.left_status);
        }

        public void setBackground(boolean color) {

        }

        public void setData() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            challengeIdField.setText("#" + challengeEntity.getId());
            amountField.setText(challengeEntity.getAmount() + " " + challengeEntity.getCurrency());
            weeksField.setText(challengeEntity.getDues().toString());
            if(TextUtils.isEmpty(challengeEntity.getPurpose())) {
                purposeField.setVisibility(View.INVISIBLE);
            } else {
                purposeField.setVisibility(View.VISIBLE);
                String purpose = (challengeEntity.getPurpose().length() > 30) ? challengeEntity.getPurpose().substring(0,27) + "..." : challengeEntity.getPurpose();
                purposeField.setText(purpose);
            }
            startField.setText(sdf.format(challengeEntity.getStartTimestamp()));
            if(challengeEntity.getFinishTimestamp() != null) {
                statusField.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                finishField.setText(sdf.format(challengeEntity.getFinishTimestamp()));
            } else if(challengeEntity.getCanceled()) {
                statusField.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                finishField.setText(context.getString(R.string.status_canceled));
            } else {
                statusField.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                finishField.setText(context.getString(R.string.status_in_progress));
            }
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holderListener.OnItemClicked(challengeEntity);
                }
            });
            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    holderListener.OnLongItemClicked(challengeEntity);
                    return true;
                }
            });
        }


    }

    public interface OnItemClickListener{
        void OnItemClicked(ChallengeEntity challenge);
        void OnLongItemClicked(ChallengeEntity challengeEntity);
    }

}
