package pl.dweb.moneysavingchallenge;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "pl.dweb.moneysavingchallenge";

    @BindView(R.id.amount)
    protected TextInputEditText amountField;

    @BindView(R.id.weeks)
    protected TextInputEditText weeksField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.apply_btn)
    public void startChallenge() {

    }

    @OnClick(R.id.advanced_btn)
    public void advancedSettings() {
        Intent intent = new Intent(this, AdvancedSettingsActivity.class);
        intent.putExtra(TAG+"/amount", Integer.valueOf(amountField.getText().toString()));
        intent.putExtra(TAG+"/weeks", Integer.valueOf(weeksField.getText().toString()));
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.fab)
    public void introduction() {
        Intent intent = new Intent(this, IntroductionActivity.class);
        startActivity(intent);
    }

}
