package org.devfleet.zkillboard.zkilla.activity.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.devfleet.zkillboard.zkilla.R;
import org.devfleet.zkillboard.zkilla.activity.settings.SettingsActivity;
import org.devfleet.zkillboard.zkilla.arch.ZKillActivity;
import org.devfleet.zkillboard.zkilla.arch.ZKillView;

@ZKillView(
        value = MainActivityPresenter.class,
        title = R.string.app_title)
public class MainActivity extends ZKillActivity<MainActivityData> {

    private MainActivityWidget widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.widget = new MainActivityWidget(this);
        this.widget.setListener(killID -> {
            final Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://zkillboard.com/kill/" + killID + "/"));
            startActivity(intent);
        });

        setView(this.widget);

        final MainActivityPresenter presenter = getPresenter();
        presenter.getData().getKill().observe(this, k -> {
            if (null == k) {
                return;
            }
            this.widget.add(k);
        });

        presenter.getData().getState().observe(this, s -> {
            switch (s) {
                case CONNECTING:
                    setDescription(R.string.app_description_connecting);
                    break;
                case CONNECTED:
                    setDescription(r(R.string.app_description_connected, presenter.getChannel()));
                    break;
                case UNAVAILABLE:
                    setDescription(R.string.app_description_unavailable);
                    break;
                case DISCONNECTED:
                case ERROR:
                default:
                    setDescription(R.string.app_description_disconnected);
                    break;
            }
            supportInvalidateOptionsMenu();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final MainActivityPresenter presenter = getPresenter();
        this.widget.setPortraitVisible(presenter.getShowPortraits());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final MainActivityPresenter presenter = getPresenter();
        final boolean enabled = presenter.getEnabled();

        setMenuItem(menu, R.id.main_action_pause, enabled, enabled);
        setMenuItem(menu, R.id.main_action_resume, !enabled, !enabled);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MainActivityPresenter presenter = getPresenter();
        switch (item.getItemId()) {
            case R.id.main_action_settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.main_action_resume:
                presenter.setEnabled(true);
                return true;

            case R.id.main_action_pause:
                presenter.setEnabled(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
