package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public class ActivityAudit {

    @Getter @Setter
    private int id;

    @Getter @Setter
    private long lastActive;


}
