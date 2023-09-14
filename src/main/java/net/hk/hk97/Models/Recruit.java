package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "recruits")
public class Recruit {


    @Id
    @Getter @Setter
    public long id;

    @Column
    @Getter @Setter
    public boolean initial_message;

    @Column
    @Getter @Setter
    public boolean advice_message;

    @Column
    @Getter @Setter
    public LocalDate date;

}