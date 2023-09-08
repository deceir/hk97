package net.hk.hk97.Models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Data
public class AllianceKeys {


    @Id
    private Long id;

    @Column
    private String aaName;
}
