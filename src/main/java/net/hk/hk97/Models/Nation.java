package net.hk.hk97.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

@Entity
@Table
public class Nation {

    @Id
    @Getter @Setter
    private long id;

    @Column(unique = true)
    @Getter @Setter
    private String nation;

    @Column
    @Getter @Setter
    private String leader;

    @Column
    @Getter @Setter
    private String continent;

    @Column
    @Getter @Setter
    private String color;

    @Column
    @Getter @Setter
    private String alliance;

    @Column
    @Getter @Setter
    private String acronym;

    @Column
    @Getter @Setter
    private int allianceid;

    @Column
    @Getter @Setter
    private int allianceposition;

    @Column
    @Getter @Setter
    private int cities;

    @Column
    @Getter @Setter
    private int offensivewars;

    @Column
    @Getter @Setter
    private int defensivewars;

    @Column
    @Getter @Setter
    private double score;

    @Column
    @Getter @Setter
    private int minutessinceactive;

    @Setter @Getter
    private int soldiers;

    @Setter @Getter
    private int tanks;

    @Setter @Getter
    private int aircraft;

    @Setter @Getter
    private int ships;

    @Setter @Getter
    private int missiles;

    @Setter @Getter
    private int nukes;

    @Getter @Setter
    String last_active;

    public Duration getActivity() {
        TemporalAccessor creationAccessor = DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(this.getLast_active() );
        Instant instant = Instant.from( creationAccessor );
        Duration duration = Duration.between(instant, Instant.now());
        return duration;
    }
}
