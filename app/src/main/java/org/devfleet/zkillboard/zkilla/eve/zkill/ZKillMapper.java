package org.devfleet.zkillboard.zkilla.eve.zkill;

import android.support.annotation.NonNull;

public interface ZKillMapper {

    @NonNull
    ZKillEntity map(@NonNull final ZKillEntity entity);

}
