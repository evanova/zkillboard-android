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
        final MutableLiveData<MainActivityData.ZKillState> state = new MutableLiveData<>();
        this.zkill = new ZKillLive(esi) {
            @Override
            protected void onOpen() {
                state.postValue(MainActivityData.ZKillState.CONNECTED);
            }

            @Override
            protected void onClose() {
                state.postValue(MainActivityData.ZKillState.DISCONNECTED);
            }

            @Override
            protected void onFailure(final Throwable t) {
                state.postValue(MainActivityData.ZKillState.ERROR);
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

    protected void setEnabled(final boolean enabled) {
        this.zkill.setEnabled(enabled);
    }
}
