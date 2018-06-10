package org.devfleet.zkillboard.zkilla.arch;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.devfleet.zkillboard.zkilla.R;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public abstract class ZKillActivity<T extends ZKillData> extends AppCompatActivity {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ZKillPresenter<T> presenter;

    private Toolbar toolbar;

    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ZKillView view = getClass().getAnnotation(ZKillView.class);
        if (null == view) {
            this.presenter = null;
        }
        else {
            if (view.title() != 0) {
                setTitle(view.title());
            }
            if (view.description() != 0) {
                setDescription(view.description());
            }

            this.presenter = ViewModelProviders.of(this, viewModelFactory).get(view.value());
            if (null == this.presenter) {
                throw new IllegalStateException("No injected presenter found matching " + view.value());
            }
            getLifecycle().addObserver(this.presenter);
        }
    }

    @Override
    public final void setTitle(final CharSequence title) {
        this.toolbar.setTitle(title);
    }

    @Override
    public final void setTitle(@StringRes final int title) {
        this.toolbar.setTitle(title);
    }

    public final void setDescription(final CharSequence description) {
        this.toolbar.setSubtitle(description);
    }

    public final void setDescription(@StringRes final int description) {
        this.toolbar.setSubtitle(description);
    }

    protected final void setView(final View view) {
        final ViewGroup container = findViewById(R.id.activityContainer);
        container.removeAllViews();
        container.addView(view);
    }

    protected final String r(@StringRes final int resId, final Object... format) {
        if (null == format) {
            return getResources().getString(resId);
        }
        return String.format(getResources().getString(resId), format);
    }

    protected final String q(@PluralsRes final int resId, final int quantity) {
        return getResources().getQuantityString(resId, quantity);
    }

    protected final String q(@PluralsRes final int resId, final int quantity, final Object... format) {
        return getResources().getQuantityString(resId, quantity, format);
    }

    protected final String a(@ArrayRes final int resId, final int index) {
        final String[] ar = getResources().getStringArray(index);
        if ((index < 0) || (index >= ar.length)) {
            return "" + index;
        }
        return ar[index];
    }

    protected final <P extends ZKillPresenter<T>> P getPresenter() {
        if(null == presenter) {
            throw new IllegalStateException("No presenter available. Did you forget to annotate this activity with ZKillView?");
        }
        return (P)presenter;
    }

    protected static void setMenuItem(
            @NonNull final Menu menu,
            @IdRes final int menuItemId,
            final boolean visible) {
        setMenuItem(menu, menuItemId, visible, visible);
    }

    protected static void setMenuItem(
            @NonNull final Menu menu,
            @IdRes int menuItemId,
            final boolean visible,
            final boolean enabled) {
        MenuItem item = menu.findItem(menuItemId);
        if (null == item) {
            return;
        }
        setMenuItem(item, visible, enabled);
    }

    protected static void setMenuItem(
            final MenuItem item,
            final boolean visible) {
        setMenuItem(item, visible, visible);

    }

    protected static void setMenuItem(
            final MenuItem item,
            final boolean visible,
            final boolean enabled) {

        item.setVisible(visible);
        item.setEnabled(enabled);

        final Drawable d = item.getIcon();
        if (null == d) {
            return;
        }
        d.setColorFilter(enabled ? Color.WHITE : Color.LTGRAY, PorterDuff.Mode.MULTIPLY);
    }

}
