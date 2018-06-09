package org.devfleet.zkillboard.zkilla.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.WorkerThread;

import org.devfleet.zkillboard.zkilla.arch.ZKillData;
import org.devfleet.zkillboard.zkilla.eve.ZKillEntity;

public class MainActivityData extends ZKillData {

    public enum ZKillState {
        CONNECTED,
        DISCONNECTED,
        ERROR
    }

    private final MutableLiveData<ZKillEntity> kill;
    private final MutableLiveData<ZKillState> state;

    public MainActivityData(final MutableLiveData<ZKillEntity> kill, final MutableLiveData<ZKillState> state) {
        this.kill = kill;
        this.state = state;
        this.state.setValue(ZKillState.DISCONNECTED);
    }

    public LiveData<ZKillEntity> getKill() {
        return kill;
    }

    @WorkerThread
    void setKill(final ZKillEntity zkill) {
        this.kill.postValue(zkill);
    }

    public LiveData<ZKillState> getState() {
        return state;
    }

    @WorkerThread
    void setState(final ZKillState state) {
        this.state.postValue(state);
    }
}
