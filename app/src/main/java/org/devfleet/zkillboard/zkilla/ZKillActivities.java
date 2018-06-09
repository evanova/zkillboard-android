package org.devfleet.zkillboard.zkilla;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;

import org.devfleet.zkillboard.zkilla.activity.MainActivity;
import org.devfleet.zkillboard.zkilla.activity.MainActivityComponent;
import org.devfleet.zkillboard.zkilla.arch.ZKillModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class ZKillActivities {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ZKillModelFactory factory);

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> mainActivityFactory(MainActivityComponent.Builder builder);


}
