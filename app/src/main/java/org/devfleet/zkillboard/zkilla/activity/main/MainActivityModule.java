package org.devfleet.zkillboard.zkilla.activity.main;

import android.arch.lifecycle.ViewModel;

import org.devfleet.zkillboard.zkilla.arch.ZKillModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ZKillModelKey(MainActivityPresenter.class)
    abstract ViewModel bind(MainActivityPresenter presenter);
}
