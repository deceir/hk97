package net.hk.hk97.Models.Bank;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table
@Data
public class AllianceBankHistory {

    @Id
    LocalDate date;

    @Column
    long cash = 0;

    @Column
    private long food = 0;

    @Column
    private long uranium = 0;

    @Column
    private long coal = 0;

    @Column
    private long oil = 0;

    @Column
    private long leadRss = 0;

    @Column
    private long iron = 0;

    @Column
    private long bauxite = 0;

    @Column
    private long gasoline = 0;

    @Column
    private long munitions = 0;

    @Column
    private long steel = 0;

    @Column
    private long aluminum = 0;

}
