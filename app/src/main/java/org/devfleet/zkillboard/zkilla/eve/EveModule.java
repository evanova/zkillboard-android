package org.devfleet.zkillboard.zkilla.eve;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LongSparseArray;

import com.annimon.stream.Stream;

import org.devfleet.zkillboard.zkilla.eve.esi.ESIClient;
import org.devfleet.zkillboard.zkilla.eve.esi.ESIName;
import org.devfleet.zkillboard.zkilla.eve.zkill.ZKillClient;
import org.devfleet.zkillboard.zkilla.eve.zkill.ZKillEntity;
import org.devfleet.zkillboard.zkilla.eve.zkill.ZKillMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class EveModule {

    private static class ESIMapper implements ZKillMapper {
        private final ESIClient esi;

        public ESIMapper(final ESIClient esi) {
            this.esi = esi;
        }

        @Nullable
        @Override
        public ZKillEntity map(@NonNull final ZKillEntity entity) {
            final ZKillEntity.Location location = entity.getLocation();
            if (null == location) {
                return null;
            }
            if (null == entity.getVictim()) {
                return null;
            }

            final List<Long> ids = new ArrayList<>();
            ids.add(location.getAllianceID());
            ids.add(location.getConstellationID());
            ids.add(location.getLocationID());
            ids.add(location.getSolarSystemID());

            Stream.of(entity.getInvolved()).forEach(i -> {
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

            Stream.of(entity.getInvolved()).forEach(i -> {
                i.setAllianceName(map.get(i.getAllianceID()));
                i.setCharacterName(map.get(i.getCharacterID()));
                i.setCorporationName(map.get(i.getCorporationID()));
                i.setShipTypeName(map.get(i.getShipTypeID()));
            });

            return entity;
        }
    }

    @Provides
    @Singleton
    public static ESIClient provideESI(final Context context) {
        final File esiCache = new File(context.getCacheDir() + File.pathSeparator + "esi");
        esiCache.mkdirs();
        return new ESIClient(
                esiCache,
                20 * 1024 * 1024,
                20 * 1000);
    }

    @Provides
    @Singleton
    public static ZKillClient provideZKill(final Context context, final ESIClient client) {
        return new ZKillClient(new ESIMapper(client));
    }
}
