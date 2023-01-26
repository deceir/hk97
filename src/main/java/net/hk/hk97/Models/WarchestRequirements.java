package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "wcreqs")
@Getter
@Setter
public class WarchestRequirements {
    @Id
    private long id;

    @Column
    private long gasoline;

    @Column
    private long munitions;

    @Column
    private long steel;

    @Column
    private long aluminum;

    @Column
    private long cash;

    @Column
    private int credits;



}
