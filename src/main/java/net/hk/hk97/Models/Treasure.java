package net.hk.hk97.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "treasures")
@AllArgsConstructor
@NoArgsConstructor
public class Treasure implements Comparable<Treasure> {

    @Id
    @Getter @Setter
    private String name;

    @Column
    @Getter @Setter
    private String color;

    @Column
    @Getter @Setter
    private String continent;

    @Column
    @Getter @Setter
    private String spawn_date;

    public String getNextSpawn() {

        LocalDate date = LocalDate.parse(this.spawn_date);

        date = date.plusDays(60);

        return date.toString();
    }

    public  LocalDate date() {
        LocalDate date = LocalDate.parse(this.spawn_date);

        date = date.plusDays(60);

        return date;

    }

    @Override
    public int compareTo(Treasure o) {
        if (this.date() == null || o.date() == null) {
            return 0;
        }
        return date().compareTo(o.date());
    }
}
