package org.devfleet.zkillboard.zkilla.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.devfleet.zkillboard.zkilla.R;
import org.devfleet.zkillboard.zkilla.eve.EveFormat;
import org.devfleet.zkillboard.zkilla.eve.EveImages;
import org.devfleet.zkillboard.zkilla.eve.ZKillData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class MainActivityWidget extends FrameLayout {

    public interface Listener {
        void onKillSelected(final long killID);
    }

    static class ZKillHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rowKillVictimImage)
        ImageView victimImage;

        @BindView(R.id.rowKillVictimText)
        TextView victimText;

        @BindView(R.id.rowKillShipText)
        TextView shipText;

        @BindView(R.id.rowKillLocationText)
        TextView locationText;

        @BindView(R.id.rowKillISKText)
        TextView iskText;

        public ZKillHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void render(final ZKillData data) {
            EveImages.characterIcon(data.getVictim().getCharacterID(), this.victimImage);

            victimText.setText(data.getVictim().getCharacterName());
            shipText.setText(data.getVictim().getShipTypeName());
            locationText.setText(r(
                    R.string.row_kill_location,
                    data.getLocation().getConstellationName(),
                    data.getLocation().getSolarSystemName()));
            locationText.setTextColor(EveFormat.getSecurityLevelColor(data.getLocation().getSecurity()));
            if (null == data.getZkb()) {
                iskText.setVisibility(View.INVISIBLE);
            }
            else {
                iskText.setVisibility(View.VISIBLE);
                iskText.setText(r(
                        R.string.row_kill_isk,
                        EveFormat.Currency.MEDIUM(data.getZkb().getFittedValue(), false),
                        EveFormat.Currency.MEDIUM(data.getZkb().getTotalValue(), false)));
            }
        }

        private String r(@StringRes final int resId, final Object... format) {
            if (null == format) {
                return itemView.getResources().getString(resId);
            }
            return String.format(itemView.getResources().getString(resId), format);
        }

    }

    static class ZKillAdapter extends RecyclerView.Adapter<ZKillHolder> {

        private final List<ZKillData> data = new ArrayList<>();
        private int max = 100;

        @NonNull
        @Override
        public ZKillHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_kill,
                    parent,
                    false);
            return new ZKillHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ZKillHolder holder, final int position) {
            holder.itemView.setOnClickListener(l -> onItemClicked(this.data.get(position), position));
            holder.render(this.data.get(position));
        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }

        public synchronized void add(final ZKillData data) {
            if (this.data.size() == max) {
                this.data.remove(this.data.size() - 1);
            }

            this.data.add(0, data);
            notifyDataSetChanged();
        }

        public synchronized void setMax(final int max) {
            this.max = Math.max(1, max);
            this.data.clear();
            notifyDataSetChanged();
        }

        protected void onItemClicked(final ZKillData item, final int position) {

        }
    }

    private ZKillAdapter adapter;
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

    public void add(final ZKillData data) {
        this.adapter.add(data);
    }

    public void setListener(final Listener listener) {
        this.listener = listener;
    }

    private void init() {
        this.adapter = new ZKillAdapter() {
            @Override
            protected void onItemClicked(final ZKillData item, final int position) {
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
