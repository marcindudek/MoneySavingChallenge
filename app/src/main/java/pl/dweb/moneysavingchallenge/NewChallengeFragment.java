package pl.dweb.moneysavingchallenge;


import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.dweb.moneysavingchallenge.model.ChallengeEntity;

import static android.content.Context.MODE_PRIVATE;
import static pl.dweb.moneysavingchallenge.MainActivity.CURRENT_CHALLENGE;
import static pl.dweb.moneysavingchallenge.MainActivity.SHARED_PREFERENCES_NAME;


public class NewChallengeFragment extends Fragment {

    @BindView(R.id.amount)
    protected TextView amountField;

    @BindView(R.id.purpose)
    protected TextInputEditText purposeField;

    @BindView(R.id.week_spinner)
    protected Spinner weekSpinner;

    @BindView(R.id.currency_spinner)
    protected Spinner currencySpinner;


    private int weeks;
    private String currency;
    private SharedPreferences preferences;
    private EventBus bus = EventBus.getDefault();

    public NewChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_challenge, container, false);
        ButterKnife.bind(this, v);
        preferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if (preferences.contains(CURRENT_CHALLENGE)) {
            goToChallenge();
        }
        ArrayAdapter<CharSequence> weeksAdapter = ArrayAdapter.createFromResource(getContext(), R.array.weeks, android.R.layout.simple_spinner_item);
        weeksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpinner.setAdapter(weeksAdapter);
        weekSpinner.setSelection(weeksAdapter.getPosition("12"));
        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weeks = (position + 1) * 4;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(getContext(), R.array.currency, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }


    @OnClick(R.id.apply_btn)
    public void startChallenge() {

        if(ChallengeFragment.isActiveChallenge()) {
            AlertDialog dialog = null;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.confirm_dialog_title))
                    .setMessage(getString(R.string.confirm_dialog_content))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.answer_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ChallengeFragment.cancelChallenge();
                            newChallenge();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.answer_no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            goToChallenge();
                            dialog.dismiss();
                        }

                    });
            dialog = builder.create();
            dialog.show();
        } else {
            newChallenge();
        }

    }

    private void newChallenge() {
        if (weeks > 0) {
            Integer amount = Integer.valueOf(amountField.getText().toString());
            bus.postSticky(new ChallengeEntity(amount, weeks, purposeField.getText().toString(), currency));
            goToChallenge();
        }
    }

    private void goToChallenge() {
        ((MainActivity) getContext()).getViewPager().setCurrentItem(1, true);
    }

    @OnClick(R.id.increase_amount_btn)
    public void increaseAmount() {
        Integer i = Integer.valueOf(amountField.getText().toString());
        i += 500;
        amountField.setText(i.toString());
    }

    @OnClick(R.id.decrease_amount_btn)
    public void decreaseAmount() {
        Integer i = Integer.valueOf(amountField.getText().toString());
        if (i > 500) {
            i -= 500;
        }
        amountField.setText(i.toString());
    }


}