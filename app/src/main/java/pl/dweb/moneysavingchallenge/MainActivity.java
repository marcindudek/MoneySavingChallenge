package pl.dweb.moneysavingchallenge;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "pl.dweb.moneysavingchallenge";

    @BindView(R.id.amount)
    protected TextInputEditText amountField;

    @BindView(R.id.week_spinner)
    protected Spinner weekSpinner;

    private int weeks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ArrayAdapter<CharSequence> weeksAdapter = ArrayAdapter.createFromResource(this, R.array.weeks, android.R.layout.simple_spinner_item);
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
    }

    @OnClick(R.id.apply_btn)
    public void startChallenge() {

        if(!TextUtils.isEmpty(amountField.getText())) {
            int amount = Integer.valueOf(amountField.getText().toString());

            Intent intent = new Intent(this, ChallengeActivity.class);
            intent.putExtra(TAG + "/weeks", weeks);
            intent.putExtra(TAG + "/amount", amount);
            startActivity(intent);
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
        Intent intent = new Intent(this, IntroductionActivity.class);
        startActivity(intent);
    }

}
