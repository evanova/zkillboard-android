package org.devfleet.zkillboard.zkilla.activity.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Stream;

import org.devfleet.zkillboard.zkilla.R;
import org.devfleet.zkillboard.zkilla.eve.EveFormat;
import org.devfleet.zkillboard.zkilla.eve.EveImages;
import org.devfleet.zkillboard.zkilla.eve.zkill.ZKillEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class KillListAdapter extends RecyclerView.Adapter<KillListAdapter.KillHolder> {

    static class KillEntry {

        @NonNull
        private ZKillEntity entity;

        @NonNull
        private ZKillEntity.Character victim;

        @Nullable
        private ZKillEntity.Character last;

        public KillEntry(final ZKillEntity entity) {
            this.entity = entity;
            this.victim = this.entity.getVictim();
            if (entity.getAttackerCount() == 0) {
                this.last = null;
            }
            else {
                this.last =
                    Stream.of(this.entity.getInvolved())
                    .filter(c -> c.getFinalBlow())
                    .findFirst()
                    .orElse(null);
            }
        }

        @NonNull
        public ZKillEntity.Character getVictim() {
            return victim;
        }

        @Nullable
        public ZKillEntity.Character getLast() {
            return last;
        }

        @NonNull
        public ZKillEntity.Location getLocation() {
            return entity.getLocation();
        }

        @NonNull
        public ZKillEntity.ZKB getZkb() {
            return entity.getZkb();
        }
    }

    static class KillHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rowKillVictimImage)
        ImageView victimImage;

        @BindView(R.id.rowKillVictimText)
        TextView victimText;

        @BindView(R.id.rowKillShipText)
        TextView shipText;

        @BindView(R.id.rowKillLocationText)
        TextView locationText;

        @BindView(R.id.rowKillSecurityText)
        TextView securityText;

        @BindView(R.id.rowKillISKText)
        TextView iskText;

        public KillHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void render(final KillEntry data) {
            EveImages.characterIcon(data.getVictim().getCharacterID(), this.victimImage);

            victimText.setText(r(
                    R.string.row_kill_character,
                    data.getVictim().getCharacterName(),
                    data.getVictim().getCorporationName()));

            shipText.setText(data.getVictim().getShipTypeName());
            locationText.setText(r(
                    R.string.row_kill_location,
                    data.getLocation().getConstellationName(),
                    data.getLocation().getSolarSystemName()));

            securityText.setText(r(
                    R.string.row_kill_security,
                    EveFormat.Number.FLOAT(data.getLocation().getSecurity())));
            securityText.setTextColor(EveFormat.getSecurityLevelColor(data.getLocation().getSecurity()));

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

    private final List<KillEntry> data = new ArrayList<>();
    private int max = 100;

    @NonNull
    @Override
    public KillHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_kill,
                parent,
                false);
        return new KillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final KillHolder holder, final int position) {
        holder.itemView.setOnClickListener(l -> onItemClicked(this.data.get(position).entity, position));
        holder.render(this.data.get(position));
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void add(final ZKillEntity data) {
        if ((null == data) || (null == data.getVictim())) {
            return;
        }

        if (this.data.size() == max) {
            this.data.remove(this.data.size() - 1);
        }

        this.data.add(0, new KillEntry(data));
        notifyDataSetChanged();
    }

    public void setMax(final int max) {
        this.max = Math.max(1, max);
        this.data.clear();
        notifyDataSetChanged();
    }

    protected void onItemClicked(final ZKillEntity item, final int position) {

    }
}
