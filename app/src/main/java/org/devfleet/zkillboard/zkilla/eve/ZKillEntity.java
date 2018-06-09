package org.devfleet.zkillboard.zkilla.eve;

import com.annimon.stream.Stream;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class ZKillEntity {

    public static class Character {

        @JsonProperty
        private long characterID;
        private String characterName;

        @JsonProperty
        private long corporationID;
        private String corporationName;

        @JsonProperty
        private long allianceID;
        private String allianceName;

        @JsonProperty
        private long shipTypeID;
        private String shipTypeName;

        @JsonProperty
        private long groupID;

        @JsonProperty("isVictim")
        private boolean victim;

        @JsonProperty
        private boolean finalBlow;

        public long getCharacterID() {
            return characterID;
        }

        public String getCharacterName() {
            return characterName;
        }

        public void setCharacterName(final String characterName) {
            this.characterName = characterName;
        }

        public long getCorporationID() {
            return corporationID;
        }

        public String getCorporationName() {
            return corporationName;
        }

        public void setCorporationName(final String corporationName) {
            this.corporationName = corporationName;
        }

        public long getAllianceID() {
            return allianceID;
        }

        public String getAllianceName() {
            return allianceName;
        }

        public void setAllianceName(final String allianceName) {
            this.allianceName = allianceName;
        }

        public long getShipTypeID() {
            return shipTypeID;
        }

        public String getShipTypeName() {
            return shipTypeName;
        }

        public void setShipTypeName(final String shipTypeName) {
            this.shipTypeName = shipTypeName;
        }

        public long getGroupID() {
            return groupID;
        }

        public boolean getVictim() {
            return victim;
        }

        public boolean getFinalBlow() {
            return finalBlow;
        }

    }

    public static class Location {
        @JsonProperty
        private long locationID;
        private String locationName;

        @JsonProperty
        private long solarSystemID;
        private String solarSystemName;

        @JsonProperty
        private long constellationID;
        private String constellationName;

        @JsonProperty
        private long allianceID;
        private String allianceName;

        private float security;

        public long getLocationID() {
            return locationID;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(final String locationName) {
            this.locationName = locationName;
        }

        public long getSolarSystemID() {
            return solarSystemID;
        }

        public String getSolarSystemName() {
            return solarSystemName;
        }

        public void setSolarSystemName(final String solarSystemName) {
            this.solarSystemName = solarSystemName;
        }

        public long getConstellationID() {
            return constellationID;
        }

        public String getConstellationName() {
            return constellationName;
        }

        public void setConstellationName(final String constellationName) {
            this.constellationName = constellationName;
        }

        public long getAllianceID() {
            return allianceID;
        }

        public String getAllianceName() {
            return allianceName;
        }

        public void setAllianceName(final String allianceName) {
            this.allianceName = allianceName;
        }

        public float getSecurity() {
            return security;
        }
    }

    public static class ZKB {
        @JsonProperty
        private String hash;

        @JsonProperty
        private long locationID;

        @JsonProperty
        private double fittedValue;

        @JsonProperty
        private double totalValue;

        public String getHash() {
            return hash;
        }

        public void setHash(final String hash) {
            this.hash = hash;
        }

        public long getLocationID() {
            return locationID;
        }

        public void setLocationID(final long locationID) {
            this.locationID = locationID;
        }

        public double getFittedValue() {
            return fittedValue;
        }

        public void setFittedValue(final double fittedValue) {
            this.fittedValue = fittedValue;
        }

        public double getTotalValue() {
            return totalValue;
        }

        public void setTotalValue(final double totalValue) {
            this.totalValue = totalValue;
        }
    }

    @JsonProperty("system")
    private Location location;

    @JsonProperty("zkb")
    private ZKB zkb;

    private List<Character> involved = Collections.emptyList();

    @JsonProperty
    private int attackerCount;

    @JsonProperty
    private boolean awox;

    @JsonProperty
    private long categoryID;

    @JsonProperty
    private long killID;

    @JsonProperty
    private boolean npc;

    @JsonProperty
    private boolean processed;

    @JsonProperty
    private boolean solo;

    @JsonIgnore
    private Character victim;

    public int getAttackerCount() {
        return attackerCount;
    }

    public boolean getAwox() {
        return awox;
    }

    public long getCategoryID() {
        return categoryID;
    }

    public long getKillID() {
        return killID;
    }

    public boolean getNpc() {
        return npc;
    }

    public boolean getProcessed() {
        return processed;
    }

    public boolean getSolo() {
        return solo;
    }

    public Location getLocation() {
        return location;
    }

    public ZKB getZkb() {
        return zkb;
    }

    @JsonProperty("involved")
    public List<Character> getInvolved() {
        return involved;
    }

    @JsonProperty("involved")
    public void setInvolved(final List<Character> involved) {
        this.involved = involved;
        this.victim = (CollectionUtils.isEmpty(involved)) ?
                null :
                Stream.of(involved).filter(i -> i.getVictim())
                .findFirst()
                .orElse(null);
    }

    public Character getVictim() {
        return victim;
    }
}
