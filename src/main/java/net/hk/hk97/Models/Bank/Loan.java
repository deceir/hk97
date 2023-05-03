package net.hk.hk97.Models.Bank;


import lombok.Getter;
import lombok.Setter;
import net.hk.hk97.Utils.RandomString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column
    private long discordid;

    @Column
    private long amount;

    @Column
    private LocalDate dateLoaned = LocalDate.now();

    @Column
    private LocalDate dateDue;

    @Column
    private Boolean active = true;

    @Column
    private LocalDate datePaidOff = null;

    @Column
    private String banker;

    @Column
    private String depositcode = RandomString.getSaltString();

    @Column String notes;

    public void updateDepositCode() {
        this.depositcode = RandomString.getSaltString();
    }

}
