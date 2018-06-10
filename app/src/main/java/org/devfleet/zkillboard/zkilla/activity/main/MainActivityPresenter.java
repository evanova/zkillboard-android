package org.devfleet.zkillboard.zkilla.activity.main;

import android.support.annotation.MainThread;

import org.devfleet.zkillboard.zkilla.arch.ZKillPresenter;

import javax.inject.Inject;

public class MainActivityPresenter extends ZKillPresenter<MainActivityData> {

    private final MainActivityUseCase useCase;
    private boolean enabled;

    @Inject
    public MainActivityPresenter(final MainActivityUseCase useCase) {
        super(useCase);
        this.useCase = useCase;
        this.enabled = true;
    }

    @Override
    public void onResume() {
        if (this.enabled) {
            this.useCase.setEnabled(true);
        }
    }

    @Override
    public void onPause() {
        this.useCase.setEnabled(false);
    }

    @Override
    protected void onCleared() {
        this.useCase.setEnabled(false);
    }

    String getChannel() {
        return this.useCase.getChannel();
    }

    @MainThread
    void setChannel(final String channel) {
        this.useCase.setChannel(channel);
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public boolean getShowPortraits() {
        return this.useCase.getShowPortraits();
    }

    @MainThread
    void setEnabled(final boolean enabled) {
        this.enabled = enabled;
        this.useCase.setEnabled(enabled);
    }
}
