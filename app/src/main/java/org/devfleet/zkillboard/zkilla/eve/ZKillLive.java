package org.devfleet.zkillboard.zkilla.eve;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LongSparseArray;

import com.annimon.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.devfleet.zkillboard.zkilla.eve.esi.ESIClient;
import org.devfleet.zkillboard.zkilla.eve.esi.ESIName;
import org.devfleet.zkillboard.zkilla.eve.zkill.ZKillClient;
import org.devfleet.zkillboard.zkilla.eve.zkill.ZKillEntity;

import java.util.ArrayList;
import java.util.List;

public class ZKillLive extends MutableLiveData<ZKillEntity> {

    private String channel = "killstream";
    private boolean enabled = false;

    private final ZKillClient zk;

    public ZKillLive(final ZKillClient zk) {
        this.zk = zk;
        this.zk.setListener(new ZKillClient.Listener() {
            @Override
            protected void onMessage(final ZKillEntity data) {
                postValue(data);
            }

            @Override
            protected void onOpen() {
                ZKillLive.this.onOpen();
            }

            @Override
            protected void onClose() {
                ZKillLive.this.onClose();
            }

            @Override
            protected void onFailure(final Throwable t) {
                ZKillLive.this.onFailure(t);
            }
        });
    }

    @CallSuper
    public synchronized void setChannel(final String channel) {
        if (StringUtils.equals(this.channel, channel)) {
            return;
        }

        this.zk.close();
        if (StringUtils.isBlank(channel)) {
            this.channel = null;
            return;
        }

        this.channel = channel.trim();
        if (this.enabled) {
            this.zk.open(this.channel);
        }
    }

    @Nullable
    public final String getChannel() {
        return channel;
    }

    @CallSuper
    public synchronized void setEnabled(final boolean enabled) {
        if (enabled == this.enabled) {
            return;
        }

        this.enabled = enabled;
        if (enabled) {
            this.zk.open(this.channel);
        }
        else {
            this.zk.close();
        }
    }

    protected void onOpen() {}

    protected void onClose() {}

    protected void onFailure(Throwable t) {}

    @Override
    @CallSuper
    public void observe(@NonNull final LifecycleOwner owner, @NonNull final Observer<ZKillEntity> observer) {
        super.observe(owner, observer);
        if (hasObservers()) {
            this.zk.open(this.channel);
        }
    }

    @Override
    @CallSuper
    public void observeForever(@NonNull final Observer<ZKillEntity> observer) {
        super.observeForever(observer);
        if (hasObservers()) {
            this.zk.open(this.channel);
        }
    }

    @Override
    @CallSuper
    public void removeObserver(@NonNull final Observer<ZKillEntity> observer) {
        super.removeObserver(observer);
        if (!hasObservers()) {
            this.zk.close();
        }
    }

    @Override
    @CallSuper
    public void removeObservers(@NonNull final LifecycleOwner owner) {
        super.removeObservers(owner);
        if (!hasObservers()) {
            this.zk.close();
        }
    }

    private static ZKillEntity map(final ESIClient esi, final ZKillEntity data) {
        final ZKillEntity.Location location = data.getLocation();
        if (null == location) {
            return null;
        }
        if (null == data.getVictim()) {
            return null;
        }

        final List<Long> ids = new ArrayList<>();
        ids.add(location.getAllianceID());
        ids.add(location.getConstellationID());
        ids.add(location.getLocationID());
        ids.add(location.getSolarSystemID());

        Stream.of(data.getInvolved()).forEach(i -> {
            ids.add(i.getAllianceID());
            ids.add(i.getCharacterID());
            ids.add(i.getCorporationID());
            ids.add(i.getShipTypeID());
        });

        final List<ESIName> names = esi.findNames(
                Stream.of(ids).distinct().filter(id -> id != 0).toList(), EveLocale.EN.getEveLocale());

        final LongSparseArray<String> map = new LongSparseArray<>(names.size());
        for (ESIName n: names) {
            map.put(n.getId(), n.getName());
        }

        location.setAllianceName(map.get(location.getAllianceID()));
        location.setConstellationName(map.get(location.getConstellationID()));
        location.setLocationName(map.get(location.getLocationID()));
        location.setSolarSystemName(map.get(location.getSolarSystemID()));

        Stream.of(data.getInvolved()).forEach(i -> {
            i.setAllianceName(map.get(i.getAllianceID()));
            i.setCharacterName(map.get(i.getCharacterID()));
            i.setCorporationName(map.get(i.getCorporationID()));
            i.setShipTypeName(map.get(i.getShipTypeID()));
        });

        return data;
    }
}
