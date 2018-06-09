package org.devfleet.zkillboard.zkilla.eve;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.util.LongSparseArray;

import com.annimon.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.devfleet.zkillboard.zkilla.eve.ESIClient;
import org.devfleet.zkillboard.zkilla.eve.ZKillClient;
import org.devfleet.zkillboard.zkilla.eve.ZKillEntity;

import java.util.ArrayList;
import java.util.List;

public class ZKillLive extends MutableLiveData<ZKillEntity> {
    private String channel = "killstream";

    private final ZKillClient zk;

    public ZKillLive(final ESIClient esi) {
        this.zk = new ZKillClient() {
            @Override
            protected void onMessage(final ZKillEntity data) {
                postValue(map(esi, data));
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
        };
    }

    public void setChannel(final String channel) {
        if (StringUtils.equals(this.channel, channel)) {
            return;
        }

        this.zk.close();
        this.channel = channel;
    }

    public void setEnabled(final boolean enabled) {
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
    public void observe(@NonNull final LifecycleOwner owner, @NonNull final Observer<ZKillEntity> observer) {
        super.observe(owner, observer);
        if (hasObservers()) {
            this.zk.open(this.channel);
        }
    }

    @Override
    public void observeForever(@NonNull final Observer<ZKillEntity> observer) {
        super.observeForever(observer);
        if (hasObservers()) {
            this.zk.open(this.channel);
        }
    }

    @Override
    public void removeObserver(@NonNull final Observer<ZKillEntity> observer) {
        super.removeObserver(observer);
        if (!hasObservers()) {
            this.zk.close();
        }
    }

    @Override
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
                Stream.of(ids).distinct().filter(id -> id != 0).toList());

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
