package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "bank_accounts")
public class Bank {

    @Id
    @Getter @Setter
    private String id;

    @Getter @Setter
    private long cash;

    @Getter @Setter
    private long food;

    @Getter @Setter
    private long uranium;

    @Getter @Setter
    private long coal;

    @Getter @Setter
    private long oil;

    @Getter @Setter
    private long lead;

    @Getter @Setter
    private long iron;

    @Getter @Setter
    private long bauxite;

    @Getter @Setter
    private long gasoline;

    @Getter @Setter
    private long munitions;

    @Getter @Setter
    private long steel;

    @Getter @Setter
    private long aluminum;

}
