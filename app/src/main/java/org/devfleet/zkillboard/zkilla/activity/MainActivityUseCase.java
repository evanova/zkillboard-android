package org.devfleet.zkillboard.zkilla.activity;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.StringUtils;
import org.devfleet.zkillboard.zkilla.arch.ZKillUseCase;
import org.devfleet.zkillboard.zkilla.eve.ESIClient;
import org.devfleet.zkillboard.zkilla.eve.ZKillLive;

import javax.inject.Inject;

class MainActivityUseCase extends ZKillUseCase<MainActivityData> {

    private final MainActivityData data;
    private final ZKillLive zkill;

    private final SharedPreferences preferences;

    @Inject
    public MainActivityUseCase(final Context context, final ESIClient esi) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        final MutableLiveData<MainActivityData.State> state = new MutableLiveData<>();
        this.zkill = new ZKillLive(esi) {
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

    protected void setEnabled(final boolean enabled) {
        if (enabled) {
            this.data.setState(MainActivityData.State.CONNECTING);
        }
        this.zkill.setEnabled(enabled);
    }
}
