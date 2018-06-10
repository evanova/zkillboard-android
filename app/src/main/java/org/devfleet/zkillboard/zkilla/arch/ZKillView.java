package org.devfleet.zkillboard.zkilla.arch;

import android.support.annotation.StringRes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Target(ElementType.TYPE)
@Retention(RUNTIME)
@Documented
public @interface ZKillView {

    Class<? extends ZKillPresenter> value();

    @StringRes int title() default 0;

    @StringRes int description() default 0;
}
