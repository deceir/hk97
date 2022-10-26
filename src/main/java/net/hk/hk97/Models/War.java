package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table
public class War {

    @Id
    @Getter @Setter
    private int id;

    @Column
    @Getter @Setter
    private String warType;

    @Column
    @Getter @Setter
    private String date;

    @Column
    @Getter @Setter
    private int attId;

    @Column
    @Getter @Setter
    private int defId;

    @Column
    @Getter @Setter
    private String attAa;

    @Column
    @Getter @Setter
    private String defAa;

    @Column
    @Getter @Setter
    private String status;

    @Column
    @Getter @Setter
    private LocalDateTime dateTimeAdded = LocalDateTime.now();

}
