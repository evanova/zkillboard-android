package org.devfleet.zkillboard.zkilla;

import android.content.Context;


import org.devfleet.zkillboard.zkilla.activity.MainActivityComponent;
import org.devfleet.zkillboard.zkilla.eve.ZKillModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {ZKillModule.class},
        subcomponents = {
            MainActivityComponent.class
        }
)
public class ZKillApplicationModule {

    @Provides
    @Singleton
    public static Context provideContext(final ZKillApplication app) {
        return app.getApplicationContext();
    }
}
