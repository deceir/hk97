package net.hk.hk97.Models.calc;

import lombok.Data;

import java.time.LocalDate;
@Data
public class ConsumptionCity {

    boolean atWar;
    private String nation;
    private LocalDate founded;
    private String name;
    private int infra;
    private int land;

}
