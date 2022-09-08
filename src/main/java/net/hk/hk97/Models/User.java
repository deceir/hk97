package net.hk.hk97.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.transaction.Transactional;

@Entity
@Table(name = "users")
@Transactional
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Getter @Setter
    private String discordid;

    @Column
    @Getter @Setter
    private String name;

    @Column(unique = true)
    @Getter @Setter
    private long nationid;

//    @Column
//    @Getter @Setter
//    private String id_string;

    @Column
    @Getter @Setter
    private boolean registered = false;

    @Column(updatable = false)
    @Getter @Setter
    private int verification = (int)(Math.random() * 50 + 1);


//
//    @OneToOne
//    @Getter @Setter
//    private Nation nation;

//    public void setDiscord_id(Long id){
//        this.discord_id = id;
//        this.id_string = id.toString();
//    }

}
    
    
