package net.hk.hk97.Models.Bank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.hk.hk97.Utils.RandomString;
import net.hk.hk97.Models.Enums.WithdrawalTypes;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Withdrawal {

    @Id
    @Getter
    @Setter
    private String discordid;

    @Column
    @Getter @Setter
    private Instant date = Instant.now();

    @Column
    @Getter
    @Setter
    private long cash = 0;

    @Column
    @Getter
    @Setter
    private long food = 0;

    @Column
    @Getter
    @Setter
    private long uranium = 0;

    @Column
    @Getter
    @Setter
    private long coal = 0;

    @Column
    @Getter
    @Setter
    private long oil = 0;

    @Column
    @Getter
    @Setter
    private long leadRss = 0;

    @Column
    @Getter
    @Setter
    private long iron = 0;

    @Column
    @Getter
    @Setter
    private long bauxite = 0;

    @Column
    @Getter
    @Setter
    private long gasoline = 0;

    @Column
    @Getter
    @Setter
    private long munitions = 0;

    @Column
    @Getter
    @Setter
    private long steel = 0;

    @Column
    @Getter
    @Setter
    private long aluminum = 0;

    @Column
    @Getter
    @Setter
    private String depositcode = RandomString.getSaltString();

    @Column
    @Getter @Setter
    private WithdrawalTypes withdrawalType;

}