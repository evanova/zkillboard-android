package org.devfleet.zkillboard.zkilla.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import org.devfleet.zkillboard.zkilla.R;
import org.devfleet.zkillboard.zkilla.arch.ZKillActivity;
import org.devfleet.zkillboard.zkilla.arch.ZKillPresenter;

public class MainActivity extends ZKillActivity<MainActivityData> {


    private MainActivityWidget widget;

    @Override
    protected Class<? extends ZKillPresenter> getPresenterClass() {
        return MainActivityPresenter.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.widget = new MainActivityWidget(this);
        this.widget.setListener(killID -> {
            final Intent intent = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://zkillboard.com/kill/" + killID + "/"));
            startActivity(intent);
        });


        final ViewGroup container = findViewById(R.id.activityContainer);
        container.addView(this.widget);

        final MainActivityPresenter presenter = getPresenter();
        presenter.getData().getKill().observe(this, k -> this.widget.add(k));
       /* this.zkill.observe(this, data -> {
            this.widget.add(data);
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
