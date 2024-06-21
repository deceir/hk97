package net.hk.hk97.Models.Stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class NationRevenue  {
    private String name;

    private String leader;

    long revenue;

    private int cities;

    private String alliance;

}
