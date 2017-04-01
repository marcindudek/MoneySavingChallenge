package pl.dweb.moneysavingchallenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.dweb.moneysavingchallenge.model.ChallengeEntity;

import static pl.dweb.moneysavingchallenge.ChallengeActivity.CURRENT_CHALLENGE;
import static pl.dweb.moneysavingchallenge.ChallengeActivity.SHARED_PREFERENCES_NAME;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "pl.dweb.moneysavingchallenge";

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager_view)
    ViewPager viewPager;

    public static final String SHARED_PREFERENCES_NAME = "msc.preferences";
    public static final String CURRENT_CHALLENGE = "current_challenge";
    private SharedPreferences preferences;
    FirebaseJobDispatcher dispatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MyAdapter adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        preferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        int startPosition = (preferences.contains(CURRENT_CHALLENGE)) ? 1 : 0;
        viewPager.setCurrentItem(startPosition);
        dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Job notificationService = dispatcher.newJobBuilder()

    }

    public ViewPager getViewPager() {
        return viewPager;
    }


    class MyAdapter extends FragmentPagerAdapter {

        private FragmentManager fragmentManager;



        public MyAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new NewChallengeFragment();
                case 1: return new ChallengeFragment();
                case 2: return new HistoryFragment();
            }
            throw new RuntimeException("coś poszło nie tak");
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getResources().getString(R.string.new_challenge_label);
                case 1: return getResources().getString(R.string.current_challenge_label);
                case 2: return getResources().getString(R.string.history_label);
            }
            throw new RuntimeException("coś poszło nie tak");
        }




    }


}
