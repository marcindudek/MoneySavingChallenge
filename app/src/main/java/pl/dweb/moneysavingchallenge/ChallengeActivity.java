package pl.dweb.moneysavingchallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.wefika.flowlayout.FlowLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChallengeActivity extends AppCompatActivity {


    @BindView(R.id.percent)
    TextView textView;

    @BindView(R.id.dues_linear_layout)
    com.wefika.flowlayout.FlowLayout linear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);
        ButterKnife.bind(this);

        int weeks;
        int amount;
        weeks = getIntent().getIntExtra(MainActivity.TAG + "/weeks", 1);
        amount = getIntent().getIntExtra(MainActivity.TAG + "/amount", 1);

        int fourWeeks = weeks / 4;
        int due = (amount / weeks) / 5;
        int[] dues = new int[weeks];
        for(int i = 0; i < (weeks); i+=4) {
            dues[i] =  (int)  (due * 1.25) * 5;
            dues[i+1] = (int) (due * 1.125) * 5;
            dues[i+2] = (int) (due * 0.875) * 5;
            dues[i+3] = (int) (due * 0.75) * 5;
        }
        int dues_tmp = 0;
        for(int i = 0; i < dues.length; i++) {
            dues_tmp += dues[i];
        }
        int dd = amount - dues_tmp;
        int ddd = dd / (weeks/2);
        ddd = (ddd > 5) ? 5 : 0;
        for(int i = 0; i < weeks; i+=4) {
            dues[i + 1] += ddd;
            dues[i + 2] += ddd;
        }

        dues_tmp = 0;

        for(int i = 0; i < dues.length; i++) {
            dues_tmp += dues[i];
        }

        dd = amount - dues_tmp;
        while(dd > 0) {
            for (int i = 3; i < weeks; i += 4) {
                if (dd > 0) {
                    dues[i] += 5;
                    dd -= 5;
                }
            }
        }


        dues_tmp = 0;

        for(int i = 0; i < dues.length; i++) {
            dues_tmp += dues[i];
        }

        due *= 5;
        int change = amount - (due * weeks);
        int saved = 0;
        String result = "" + (saved * 100/amount) + "%";

        textView.setText("" + (amount - dues_tmp));

            for (int i = 0; i < weeks; i++) {

                Button button = new Button(new android.view.ContextThemeWrapper(this,
                        android.R.style.Widget_Material_Button_Colored),
                        null,
                        android.R.style.Widget_Material_Button_Colored);
                button.setLayoutParams(new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT));
                button.setText("" + (dues[i]));
                linear.addView(button);
            }

    }
}
