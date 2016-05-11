package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.event.StatusBarEvent;
import name.caiyao.microreader.ui.fragment.SettingsFragment;
import name.caiyao.microreader.utils.RxBus;
import rx.Subscription;
import rx.functions.Action1;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fl_preference)
    FrameLayout flPreference;

    public Subscription rxSubscription;

    private SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.activity_setting_title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        rxSubscription = RxBus.getDefault().toObservable(StatusBarEvent.class)
                .subscribe(new Action1<StatusBarEvent>() {
                    @Override
                    public void call(StatusBarEvent statusBarEvent) {
                       recreate();
                    }
                });
        setToolBar(null,toolbar, true, true, null);
        getFragmentManager().beginTransaction().replace(R.id.fl_preference, settingsFragment).commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }
}
