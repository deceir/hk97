package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;

public class WarInfo {

    @Getter @Setter
    private int id;

    @Getter @Setter
    private String reason;

    @Getter @Setter
    private String warType;

    @Getter @Setter
    private int turnsLeft;

    @Getter @Setter
    private int attResistance;

    @Getter @Setter
    private int defResistance;

    @Getter @Setter
    private long attInfraDestroyed;

    @Getter @Setter
    private long defInfraDestroyed;

    @Getter @Setter
    private Military attackerNation;

    @Getter @Setter
    private Military defenderNation;

    @Getter @Setter
    private int attmaps;

    @Getter @Setter
    private int defmaps;


}
