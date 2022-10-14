package net.hk.hk97.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter

public class Bank {

    @Id
    private String discordid;

    @Column
    private String name;

    @Column
    private long nationid;

    @Column
    private long cash = 0;

    @Column
    private long food =0;

    @Column
    private long uranium =0;

    @Column
    private long coal =0;

    @Column
    private long oil =0;

    @Column
    private long leadRss =0;

    @Column
    private long iron =0;

    @Column
    private long bauxite =0;

    @Column
    private long gasoline =0;

    @Column
    private long munitions =0;

    @Column
    private long steel =0;

    @Column
    private long aluminum =0;

    @Column
    @JsonIgnore
    private String depositcode = RandomString.getSaltString();


    public void updateDepositCode() {
        this.depositcode = RandomString.getSaltString();
    }

    public long getTotals() {
        long total = 0;
        total += this.cash;
        total += this.food;
        total += this.uranium;
        total += this.coal;
        total += this.iron;
        total += this.oil;
        total += this.leadRss;
        total += this.munitions;
        total += this.aluminum;
        total += this.steel;
        total += this.gasoline;
        return total;
    }

    public List<String> bankString() {
        List<String> list = Arrays.asList(this.getName() + "", this.getNationid() + "", (this.getDiscordid()) + "", this.getCash() + "", this.getFood()+ "", this.getUranium() + "", this.getLeadRss()+ "", this.getCoal() + "", this.getIron() + "", this.getOil() + "", this.getBauxite() + "", this.getSteel() + "", this.getMunitions() + "", this.getGasoline() + "", this.getAluminum() + "");
        return list;
    }


}
