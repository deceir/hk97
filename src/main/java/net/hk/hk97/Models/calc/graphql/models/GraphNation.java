package net.hk.hk97.Models.calc.graphql.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class GraphNation {

    @Getter
    @Setter
    private String nation_name;

    @Getter
    @Setter
    private String leader_name;
}
