package org.devfleet.zkillboard.zkilla.eve.esi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ESIType {
    /*"description": "The Rifter is a...",
    "group_id": 25,
    "name": "Rifter",
    "published": true,
    "type_id": 587*/

    @JsonProperty("name")
    private String typeName;

    @JsonProperty("type_id")
    private long typeId;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(final long typeId) {
        this.typeId = typeId;
    }
}
