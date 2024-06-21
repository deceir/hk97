package net.hk.hk97.Models.Bank;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.hk.hk97.Models.Enums.BankLogType;
import net.hk.hk97.Models.Enums.WithdrawalTypes;
import net.hk.hk97.Utils.RandomString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table
@Data
public class BankLogs {

    @Id
    long id;

    @Column
    BankLogType type;

    @Column
    String banker;
    @Column
    String bankerName;

    @Column
    long receiver;

    @Column

    private Instant date = Instant.now();

    @Column

    private long cash = 0;

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
