package net.hk.hk97.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.PrivateKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NAudit {

    private long id;

    private String name;

    private String leader;

    private String alliance;

    private double score;

    private int cities;

    private int soldiers;

    private int tanks;

    private int jets;

    private int ships;

    private int missiles;

    private int nukes;

    private long cash;

    private int gas;

    private int steel;

    private int munitions;

    private int aluminum;


}
