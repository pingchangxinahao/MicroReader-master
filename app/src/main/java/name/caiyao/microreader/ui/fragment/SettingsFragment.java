package name.caiyao.microreader.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import name.caiyao.microreader.BuildConfig;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.UpdateItem;
import name.caiyao.microreader.event.StatusBarEvent;
import name.caiyao.microreader.presenter.ISettingPresenter;
import name.caiyao.microreader.presenter.impl.SettingPresenterImpl;
import name.caiyao.microreader.ui.iView.ISettingFragment;
import name.caiyao.microreader.utils.CacheUtil;
import name.caiyao.microreader.utils.RxBus;


public class SettingsFragment extends PreferenceFragment implements ISettingFragment {
    private Preference prefCache;
    private ISettingPresenter mISettingPresenter;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mISettingPresenter = new SettingPresenterImpl(this);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefCache = findPreference(getString(R.string.pre_cache_size));
        prefCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CacheUtil.deleteDir(SettingsFragment.this.getActivity().getCacheDir());
                showCacheSize(prefCache);
                return true;
            }
        });
        findPreference(getString(R.string.pre_feedback)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "me@caiyao.name", null));
                    startActivity(Intent.createChooser(intent, "选择邮件客户端:"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
        findPreference(getString(R.string.pre_author)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://caiyao.name/releases")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        findPreference(getString(R.string.pre_status_bar)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                RxBus.getDefault().send(new StatusBarEvent());
                return true;
            }
        });
        findPreference(getString(R.string.pre_nav_color)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                RxBus.getDefault().send(new StatusBarEvent());
                return true;
            }
        });
        Preference version = findPreference(getString(R.string.pre_version));
        version.setSummary(BuildConfig.VERSION_NAME);
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                mISettingPresenter.checkUpdate();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showCacheSize(prefCache);
    }

    @Override
    public void onPause() {
        super.onPause();
        mISettingPresenter.unsubcrible();
    }

    private void showCacheSize(Preference preference) {
        preference.setSummary(getActivity().getString(R.string.cache_size) + CacheUtil.getCacheSize(getActivity().getCacheDir()));
    }

    @Override
    public void showError(String error) {
        if (getActivity() != null)//Caused by: java.lang.IllegalStateException: Fragment SettingsFragment{9c95f97} not attached to Activity
            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUpdateDialog(final UpdateItem updateItem) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.update_title))
                .setMessage(String.format(getString(R.string.update_description), updateItem.getVersionName(), updateItem.getReleaseNote()))
                .setPositiveButton(getString(R.string.update_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateItem.getDownloadUrl())));
                    }
                })
                .setNegativeButton(getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    @Override
    public void showNoUpdate() {
        if (getActivity() != null)//Caused by: java.lang.IllegalStateException: Fragment SettingsFragment{9c95f97} not attached to Activity
            Toast.makeText(getActivity(), getString(R.string.update_no_update), Toast.LENGTH_SHORT).show();
    }
}
