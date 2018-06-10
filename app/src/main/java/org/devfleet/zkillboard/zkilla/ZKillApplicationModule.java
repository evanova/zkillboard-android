package org.devfleet.zkillboard.zkilla;

import android.content.Context;


import org.devfleet.zkillboard.zkilla.activity.main.MainActivityComponent;
import org.devfleet.zkillboard.zkilla.eve.EveModule;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                EveModule.class
        },
        subcomponents = {
                MainActivityComponent.class
        }
)
public abstract class ZKillApplicationModule {

    @Provides
    @Singleton
    public static Context provideContext(final ZKillApplication app) {
        return app.getApplicationContext();
    }
}
