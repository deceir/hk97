package net.hk.hk97.Models.Diplomatia;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Data
@Table
public class DiplomatiaSignup {

    @Id
    private int id;

    @Column
    private String emailAddress;

    @Column
    private LocalDateTime signupDate = LocalDateTime.now();

    @Column
    private boolean invited = false;

}
