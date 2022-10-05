package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Interview {

    @Id
    @Getter @Setter
    private long id;

    @Column
    @Getter @Setter
    private boolean active;

    @Column
    @Getter @Setter
    private long channelId;

}
