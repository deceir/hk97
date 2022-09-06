package net.hk.hk97.Models.calc.graphql.models;

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
@Table(name = "alliance")
@AllArgsConstructor
@NoArgsConstructor
public class Alliance {

    @Id
    @Getter @Setter
    public long id;

    @Column
    @Getter @Setter
    public String name;

    @Column
    @Getter @Setter
    public long aa_score;

    @Column
    @Getter @Setter
    public long cities;

    @Column
    @Getter @Setter
    public long aa_rank;

    @Column
    @Getter @Setter
    public String flag;

    @Column

    public long cur_barracks;

    @Column
    @Getter @Setter
    public long old_barracks;

    @Column

    public long cur_factories;

    @Column
    @Getter @Setter
    public long old_factories;

    @Column

    public long cur_hangars;

    @Column
    @Getter @Setter
    public long old_hangars;

    @Column
    public long cur_drydocks;

    @Column
    @Getter @Setter
    public long old_drydocks;

    @Column
    @Getter @Setter
    public LocalDate last_updated;

    public long getCur_barracks() {
        return cur_barracks;
    }

    public void setCur_barracks(long new_barracks) {
        this.old_barracks = this.cur_barracks;
        this.cur_barracks = new_barracks;
    }

    public long getCur_factories() {
        return cur_factories;
    }

    public void setCur_factories(long new_factories) {
        this.old_factories = this.cur_factories;
        this.cur_factories = new_factories;
    }

    public long getCur_hangars() {
        return cur_hangars;
    }

    public void setCur_hangars(long new_hangars) {
        this.old_hangars = this.cur_hangars;
        this.cur_hangars = new_hangars;
    }

    public long getCur_drydocks() {
        return cur_drydocks;
    }

    public void setCur_drydocks(long new_drydocks) {
        this.old_drydocks = this.cur_drydocks;
        this.cur_drydocks = new_drydocks;
    }
}
