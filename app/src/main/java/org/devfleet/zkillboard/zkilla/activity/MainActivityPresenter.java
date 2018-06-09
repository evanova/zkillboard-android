package org.devfleet.zkillboard.zkilla.activity;

import org.devfleet.zkillboard.zkilla.arch.ZKillPresenter;

import javax.inject.Inject;

public class MainActivityPresenter extends ZKillPresenter<MainActivityData> {

    private MainActivityUseCase useCase;

    @Inject
    public MainActivityPresenter(final MainActivityUseCase useCase) {
        super(useCase);
        this.useCase = useCase;
    }

    @Override
    public void onResume() {
        useCase.setEnabled(true);
    }

    @Override
    public void onPause() {
        useCase.setEnabled(false);
    }

    public void setChannel(final String channel) {
        useCase.setChannel(channel);
        useCase.setEnabled(true);
    }
}
