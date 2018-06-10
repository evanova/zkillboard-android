package org.devfleet.zkillboard.zkilla;

import android.content.Context;


import org.devfleet.zkillboard.zkilla.activity.main.MainActivityComponent;
import org.devfleet.zkillboard.zkilla.eve.ESIClient;
import org.devfleet.zkillboard.zkilla.eve.ZKillClient;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
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

    @Provides
    @Singleton
    public static ESIClient provideESI(final Context context) {
        final File esiCache = new File(context.getCacheDir() + File.pathSeparator + "esi");
        esiCache.mkdirs();
        return new ESIClient(
                esiCache,
                20 * 1024 * 1024,
                20 * 1000);
    }

    @Provides
    @Singleton
    public static ZKillClient provideZKill(final Context context) {
        return new ZKillClient();
    }
}
