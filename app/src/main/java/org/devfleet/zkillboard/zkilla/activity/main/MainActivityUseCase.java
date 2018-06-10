package org.devfleet.zkillboard.zkilla.activity.main;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.StringUtils;
import org.devfleet.zkillboard.zkilla.arch.ZKillUseCase;
import org.devfleet.zkillboard.zkilla.eve.ZKillLive;
import org.devfleet.zkillboard.zkilla.eve.zkill.ZKillClient;

import javax.inject.Inject;

class MainActivityUseCase extends ZKillUseCase<MainActivityData> {

    private final MainActivityData data;
    private final ZKillLive zkill;

    private final SharedPreferences preferences;
    private final Context context;

    private boolean enabled = true;

    @Inject
    public MainActivityUseCase(final Context context, final ZKillClient client) {
        this.context = context.getApplicationContext();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        final MutableLiveData<MainActivityData.State> state = new MutableLiveData<>();
        this.zkill = new ZKillLive(client) {
            @Override
            protected void onOpen() {
                state.postValue(MainActivityData.State.CONNECTED);
            }

            @Override
            protected void onClose() {
                state.postValue(MainActivityData.State.DISCONNECTED);
            }

            @Override
            protected void onFailure(final Throwable t) {
                state.postValue(MainActivityData.State.ERROR);
            }
        };

        this.zkill.setChannel(this.preferences.getString("preferences.channel", "killstream"));
        this.data = new MainActivityData(this.zkill, state);
    }

    @Override
    public MainActivityData getData() {
        return this.data;
    }

    protected void setChannel(final String channel) {
        this.zkill.setChannel(channel);
        if (StringUtils.isNotBlank(channel)) {
            this.preferences.edit().putString("preferences.channel", channel).apply();
        }
    }

    protected String getChannel() {
        return this.zkill.getChannel();
    }

    protected boolean getShowPortraits() {
        return !preferences.getBoolean("settings.portraits", true);
    }

    protected void setEnabled(final boolean enabled) {
        if (enabled) {
            if (checkCanConnect() && checkCanRun()) {
                this.zkill.setEnabled(true);
            }
            else {
                this.data.setState(MainActivityData.State.UNAVAILABLE);
            }
            return;
        }

        this.zkill.setEnabled(false);
    }

    private boolean checkCanConnect() {
        final ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {
            return false;
        }

        switch (activeNetwork.getType()) {
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_MOBILE_DUN:
                //don't use mobile
                return preferences.getBoolean("settings.network", true) ?
                        false : activeNetwork.isConnectedOrConnecting();
            default:
                return activeNetwork.isConnectedOrConnecting();
        }
    }

    private boolean checkCanRun() {
        //don't use battery
        return preferences.getBoolean("settings.battery", true) ?
                !isCharging(context) : true;
    }

    private static boolean isCharging(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            return batteryManager.isCharging();
        }

        final Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL);
    }
}
