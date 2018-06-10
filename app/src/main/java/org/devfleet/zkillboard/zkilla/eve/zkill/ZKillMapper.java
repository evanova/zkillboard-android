package org.devfleet.zkillboard.zkilla.eve.zkill;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ZKillMapper {

    @Nullable
    ZKillEntity map(@NonNull final ZKillEntity entity);

}
