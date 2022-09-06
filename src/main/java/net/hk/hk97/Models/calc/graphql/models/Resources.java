package net.hk.hk97.Models.calc.graphql.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "resources")
@AllArgsConstructor
@NoArgsConstructor
public class Resources {

    @Id
    @Getter @Setter
    private String name;

    @Column
    @Getter @Setter
    private long price;

    @Column
    @Getter @Setter
    private LocalDateTime last_updated;


    public String getReadableDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm MM/dd/yyyy");
        String S = this.last_updated.format(formatter) + " (Server Time)"; // formats to 09/23/2009 13:53
        return S;
    }


}
