package pl.dweb.moneysavingchallenge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import pl.dweb.moneysavingchallenge.model.DueEntity;

/**
 * Created by md on 4/8/17.
 */

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsHolder> {

    List<DueEntity> list;
    private Context context;
    private String currency;

    public DetailsAdapter(Context context) {
        this.context = context;
    }

    public DetailsAdapter(List<DueEntity> list, Context context, String currency) {
        this.list = list;
        this.context = context;
        this.currency = currency;
    }

    public void setData(List<DueEntity> list, String currency) {
        this.list = list;
        this.currency = currency;
    }

    @Override
    public DetailsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_details_challenge_item, parent, false);
        return new DetailsAdapter.DetailsHolder(view);    }

    @Override
    public void onBindViewHolder(DetailsHolder holder, int position) {
        holder.due = list.get(position);
        holder.setData();

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DetailsHolder extends RecyclerView.ViewHolder {

        TextView dueNumber;
        TextView dueAmount;
        TextView dueDate;
        DueEntity due;

        public DetailsHolder(View itemView) {
            super(itemView);
            dueNumber = (TextView) itemView.findViewById(R.id.due_number);
            dueAmount = (TextView) itemView.findViewById(R.id.due_amount);
            dueDate = (TextView) itemView.findViewById(R.id.due_date);
        }

        public void setData() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            dueAmount.setText(due.getDue()  + " " + currency);
            String number = (due.getDueNumber() < 10) ? "  " + due.getDueNumber() + "." : due.getDueNumber() + ".";
            dueNumber.setText(number);
            String date = (due.getTimestamp() == null) ? "---      " : sdf.format(due.getTimestamp());
            dueDate.setText(date);
        }
    }
}
