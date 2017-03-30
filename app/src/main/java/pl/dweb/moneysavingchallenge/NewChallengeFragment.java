package pl.dweb.moneysavingchallenge;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.dweb.moneysavingchallenge.model.ChallengeEntity;

import static android.content.Context.MODE_PRIVATE;
import static pl.dweb.moneysavingchallenge.ChallengeActivity.CURRENT_CHALLENGE;
import static pl.dweb.moneysavingchallenge.ChallengeActivity.SHARED_PREFERENCES_NAME;
import static pl.dweb.moneysavingchallenge.MainActivity.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewChallengeFragment extends Fragment {

    @BindView(R.id.amount)
    protected TextInputEditText amountField;

    @BindView(R.id.purpose)
    protected TextInputEditText purposeField;

    @BindView(R.id.week_spinner)
    protected Spinner weekSpinner;

    private int weeks;
    private SharedPreferences preferences;
    private EventBus bus = EventBus.getDefault();

    public NewChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_challenge, container, false);
        ButterKnife.bind(this, v);
        preferences = getContext().getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if(preferences.contains(CURRENT_CHALLENGE)) {
            startChallenge();
        }
        ArrayAdapter<CharSequence> weeksAdapter = ArrayAdapter.createFromResource(getContext(), R.array.weeks, android.R.layout.simple_spinner_item);
        weeksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weekSpinner.setAdapter(weeksAdapter);
        weekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0) weeks = (position)*4;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }



    @OnClick(R.id.apply_btn)
    public void startChallenge() {

        ChallengeEntity challengeEntity = null;
        if(weeks > 0) {
            int amount = Integer.valueOf(amountField.getText().toString());
            bus.post(new ChallengeEntity(amount, weeks, purposeField.getText().toString()));
            ((MainActivity)getContext()).getViewPager().setCurrentItem(1, true);
        }
    }

    @OnClick(R.id.advanced_btn)
    public void advancedSettings() {
        Integer i = Integer.valueOf(amountField.getText().toString());
        i += 500;
        amountField.setText(i.toString());
    }

    @OnClick(R.id.fab)
    public void introduction() {
        Intent intent = new Intent(getContext(), IntroductionActivity.class);
        startActivity(intent);
    }

}
