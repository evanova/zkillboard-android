package org.devfleet.zkillboard.zkilla.arch;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.MainThread;

public abstract class ZKillPresenter<T extends ZKillData> extends ViewModel implements LifecycleObserver {

    private final ZKillUseCase<T> useCase;

    public ZKillPresenter(final ZKillUseCase<T> useCase) {
        this.useCase = useCase;
    }

    public final T getData() {
        return this.useCase.getData();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @MainThread
    public void onResume() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @MainThread
    public void onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @MainThread
    public void onCreate() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @MainThread
    public void onDestroy() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    @MainThread
    public void onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    @MainThread
    public void onStop() {
    }
}
