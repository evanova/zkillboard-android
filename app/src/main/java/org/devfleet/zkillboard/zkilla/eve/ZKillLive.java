package org.devfleet.zkillboard.zkilla.eve;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.LongSparseArray;

import com.annimon.stream.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//TODO Not sure if it is good design to bunch clients in a live data
public class ZKillLive extends MutableLiveData<ZKillData> {

    private final String channel;
    private final ESIClient esi;

    private ZKillClient zk = null;

    public ZKillLive(final Context context, final String channel) {
        this.channel = channel;

        final File esiCache = new File(context.getCacheDir() + File.pathSeparator + "esi");
        esiCache.mkdirs();
        this.esi = new ESIClient(
            esiCache,
            20 * 1024 * 1024,
            20 * 1000);
    }

    @Override
    public void observe(@NonNull final LifecycleOwner owner, @NonNull final Observer<ZKillData> observer) {
        super.observe(owner, observer);
        if (hasObservers()) {
            ensureOpen();
        }
    }

    @Override
    public void observeForever(@NonNull final Observer<ZKillData> observer) {
        super.observeForever(observer);
        if (hasObservers()) {
            ensureOpen();
        }
    }

    @Override
    public void removeObserver(@NonNull final Observer<ZKillData> observer) {
        super.removeObserver(observer);
        if (!hasObservers()) {
            ensureClose();
        }
    }

    @Override
    public void removeObservers(@NonNull final LifecycleOwner owner) {
        super.removeObservers(owner);
        if (!hasObservers()) {
            ensureClose();
        }
    }

    private synchronized void ensureOpen() {
        if (null == this.zk) {
            this.zk = new ZKillClient() {
                @Override
                protected void onMessage(final ZKillData data) {
                    final ZKillData mapped = map(data);
                    if (null != mapped) {
                        postValue(mapped);
                    }
                }
            };
            this.zk.open(this.channel);
        }
    }

    private synchronized void ensureClose() {
        if (null != this.zk) {
            this.zk.close();
            this.zk = null;
        }
    }


    private ZKillData map(final ZKillData data) {
        final ZKillData.Location location = data.getLocation();
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
