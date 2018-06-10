package org.devfleet.zkillboard.zkilla.activity;

import android.arch.lifecycle.MutableLiveData;

import org.devfleet.zkillboard.zkilla.arch.ZKillUseCase;
import org.devfleet.zkillboard.zkilla.eve.ESIClient;
import org.devfleet.zkillboard.zkilla.eve.ZKillLive;

import javax.inject.Inject;

class MainActivityUseCase extends ZKillUseCase<MainActivityData> {

    private final MainActivityData data;
    private final ZKillLive zkill;

    @Inject
    public MainActivityUseCase(final ESIClient esi) {
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
        this.data = new MainActivityData(this.zkill, state);
    }

    @Override
    public MainActivityData getData() {
        return this.data;
    }

    protected void setChannel(final String channel) {
        this.zkill.setChannel(channel);
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
