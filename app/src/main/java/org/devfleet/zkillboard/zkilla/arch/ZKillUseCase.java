package org.devfleet.zkillboard.zkilla.arch;

public abstract class ZKillUseCase<T extends ZKillData> {

    public abstract T getData();
}
