package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "warrooms")
public class Warroom {

    @Id
    @Getter @Setter
    private int id;

    @Column
    @Getter @Setter
    private String channelid;


}
