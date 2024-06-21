package net.hk.hk97.Models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class InformationMessage {

    @Id
    int id;

    @Column(unique=true)
    String type;

    @Column
    String names;

    @Column
    String rank;
}
