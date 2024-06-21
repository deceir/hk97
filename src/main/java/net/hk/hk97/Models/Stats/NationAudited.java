package net.hk.hk97.Models.Stats;

import lombok.Data;

@Data
public class NationAudited {

    private String name;

    private String leader;

    private String discord;

    private int infra;

    private long id;

    private long food;

    private long uranium;

    private long iron;

    private long oil;

    private long bauxite;

    private long lead;

    private long coal;

    private long soldiers;

    private long population;

    private boolean ironworks = false;

    private boolean gasolineReserve = false;

    private boolean armsStockpile = false;

    private boolean bauxiteWorks = false;

    private int oilRefineries = 0;

    private int steelMills = 0;

    private int aluminumRefineries = 0;

    private int munitionsFactories = 0;

    private int nuclearPowerPlants = 0;

    private double foodConsumption = 0;

    private double uraToSend = 0;

    private double foodToSend = 0;

    private double ironToSend = 0;

    private double oilToSend = 0;

    private double leadToSend = 0;

    private double bauxToSend = 0;

    private double ironUsed = 0;
    private double oilUsed = 0;
    private double coalUsed = 0;
    private double leadUsed = 0;
    private double bauxUsed = 0;
    private double uraniumUsed = 0;
}
