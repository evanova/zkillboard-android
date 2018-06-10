package org.devfleet.zkillboard.zkilla.activity.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import org.devfleet.zkillboard.zkilla.eve.zkill.ZKillEntity;

class MainActivityWidget extends FrameLayout {

    public interface Listener {
        void onKillSelected(final long killID);
    }

    private KillListAdapter adapter;
    private Listener listener;

    public MainActivityWidget(@NonNull final Context context) {
        super(context);
        init();
    }

    public MainActivityWidget(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainActivityWidget(@NonNull final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void add(final ZKillEntity data) {
        this.adapter.add(data);
    }

    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    private void init() {
        this.adapter = new KillListAdapter() {
            @Override
            protected void onItemClicked(final ZKillEntity item, final int position) {
                if (null != listener) {
                    listener.onKillSelected(item.getKillID());
                }
            }
        };

        final RecyclerView view = new RecyclerView(getContext());
        view.setLayoutManager(new LinearLayoutManager(getContext()));
        view.setAdapter(this.adapter);
        addView(view);
    }
}
