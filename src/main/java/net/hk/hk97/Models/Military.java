package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class Military {

    @Getter @Setter
    private int id;

    @Getter @Setter
    private String nation_name;

    @Getter @Setter
    private String leader_name;

    @Getter @Setter
    private String color;

    @Getter @Setter
    private double score;

    @Getter @Setter
    private int soldiers;

    @Getter @Setter
    private int tanks;

    @Getter @Setter
    private int jets;

    @Getter @Setter
    private int ships;

    @Getter @Setter
    private int missiles;

    @Getter @Setter
    private int nukes;

    @Getter @Setter
    private int cities;

    @Getter @Setter
    private String aaAcronym;

    @Setter @Getter
    private String groundcontrol = "";

    @Setter @Getter
    private String airsuperiority = "";

    @Setter @Getter
    private String navalblockade = "";

    @Getter @Setter
    String last_active;

    @Getter @Setter
    private boolean ironDome;

    @Getter @Setter
    private boolean vds;

    @Getter @Setter
    private int beige_turns_left;

    @Getter @Setter
    private String aaname;

    public Duration getActivity() {
        TemporalAccessor creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(this.getLast_active() );
        Instant instant = Instant.from( creationAccessor );
        Duration duration = Duration.between(instant, Instant.now());
        return duration;
    }
}
