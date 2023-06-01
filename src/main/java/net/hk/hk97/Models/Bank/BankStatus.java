package net.hk.hk97.Models.Bank;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BankStatus {

    @Id
    int id;

    @Column
    String withdrawalStatus;

    @Column
    String depositStatus;

}
