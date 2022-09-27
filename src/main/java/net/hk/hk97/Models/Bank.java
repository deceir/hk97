package net.hk.hk97.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Bank {

    @Id
    @Getter @Setter
    private String discordid;

    @Column
    @Getter @Setter
    private long cash = 0;

    @Column
    @Getter @Setter
    private long food =0;

    @Column
    @Getter @Setter
    private long uranium =0;

    @Column
    @Getter @Setter
    private long coal =0;

    @Column
    @Getter @Setter
    private long oil =0;

    @Column
    @Getter @Setter
    private long leadRss =0;

    @Column
    @Getter @Setter
    private long iron =0;

    @Column
    @Getter @Setter
    private long bauxite =0;

    @Column
    @Getter @Setter
    private long gasoline =0;

    @Column
    @Getter @Setter
    private long munitions =0;

    @Column
    @Getter @Setter
    private long steel =0;

    @Column
    @Getter @Setter
    private long aluminum =0;

    @Column
    @Getter @Setter
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
}
