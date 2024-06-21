package net.hk.hk97.Models;


import lombok.Data;

@Data
public class NationWarchestAudited {

    private long id;

    private String name;

    private String leader;

    private String discord;

    private int cities;

    private double munitions;

    private double steel;

    private double aluminum;

    private double gasoline;

    private double munitionsToSend = 0;

    private double steelToSend = 0;

    private double aluminumToSend = 0;

    private double gasolineToSend = 0;

}
