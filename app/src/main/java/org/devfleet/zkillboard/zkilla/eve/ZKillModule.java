package org.devfleet.zkillboard.zkilla.eve;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ZKillModule {

    @Provides
    @Singleton
    public static ZKillLive provideZKill(final Context context) {
        return new ZKillLive(context.getApplicationContext(), "killstream");
    }
}
