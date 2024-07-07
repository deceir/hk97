package net.hk.hk97.Models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table
public class Radiation {

    @Id
    private String id;

    @Column
    double radiationLevel;

}
