package net.hk.hk97.Models.calc.graphql.models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "militaries")
public class Military {


    @Id
    @Getter @Setter
    private long nation_id;

    @Getter @Setter
    private long aa_id;

    @Column
    @Getter @Setter
    private double nation_score;

    @Column
    @Getter @Setter
    private int soldiers;

    @Column
    @Getter @Setter
    private int tanks;

    @Column
    @Getter @Setter
    private int aircraft;

    @Column
    @Getter @Setter
    private int ships;

    @Column
    @Getter @Setter
    private int missiles;

    @Column
    @Getter @Setter
    private int nukes;

}
