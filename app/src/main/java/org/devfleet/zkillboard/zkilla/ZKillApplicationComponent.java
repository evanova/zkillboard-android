package org.devfleet.zkillboard.zkilla;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                ZKillApplicationModule.class,
                ZKillActivities.class}
)
public interface ZKillApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(ZKillApplication application);

        ZKillApplicationComponent build();
    }

    void inject(ZKillApplication application);
}
