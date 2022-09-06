package net.hk.hk97.Models.calc;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

public class LandCalc {

    // land policy, arable land, advanced engineer

    @Getter
    @Setter
    private long base_cost = 0;

    @Getter @Setter
    private String base_cost_f;

    @Getter @Setter
    private long one_cost = 0;

    @Getter @Setter
    private String one_cost_f;

    @Getter @Setter
    private long one_cost_saved = 0;

    @Getter @Setter
    private String one_cost_saved_f;

    @Getter @Setter
    private long two_cost = 0;

    @Getter @Setter
    private String two_cost_f;

    @Getter @Setter
    private long two_cost_saved = 0;

    @Getter @Setter
    private String two_cost_saved_f;

    @Getter @Setter
    private long aec_cost = 0;

    @Getter @Setter
    private String aec_cost_f;

    @Getter @Setter
    private long aec_cost_saved = 0;

    @Getter @Setter
    private String aec_cost_saved_f;

    @Getter @Setter
    private long gsa_cost = 0;

    @Getter @Setter
    private String gsa_cost_f;

    @Getter @Setter
    private long gsa_cost_saved = 0;

    @Getter @Setter
    private String gsa_cost_saved_f;

    public double landPrice(double amount) {
        return (0.002 * Math.pow((amount - 20),2) ) + 50;
    }

    public void calculateLand(int land_start, int land_end) {

        double difference = land_end - land_start;
        double timesDivisible;
        double currentLand = land_start;
        double costOfChunk;
        if (difference > 500 && difference % 500 == 0) {
            timesDivisible = difference / 500;
            while (timesDivisible > 0) {
                this.base_cost += landPrice(currentLand) * 500;
                currentLand += 500;
                timesDivisible -= 1;
            }
        }
        if (difference > 500 && difference % 500 != 0) {
            costOfChunk = (landPrice(land_start) * (difference % 500));
            this.base_cost += costOfChunk;
            calculateLand((int)Math.floor(land_start + (difference % 500)), land_end);
        }
        if (difference <= 500) {
            this.base_cost += landPrice(land_start) * difference;
        }

        this.one_cost = (long) (base_cost * .95);
        this.one_cost_saved = base_cost - one_cost;
        this.two_cost = (long) (base_cost * .90);
        this.two_cost_saved = base_cost - two_cost;
        this.aec_cost = (long) (base_cost * .85);
        this.aec_cost_saved = base_cost - aec_cost;
        this.gsa_cost = (long) (base_cost * .925);
        this.gsa_cost = (long) (this.gsa_cost * .95);
        this.gsa_cost = (long) (this.gsa_cost * .95);
        this.gsa_cost_saved = base_cost - gsa_cost;


    }

    public void calculateLand(int land_start, int land_end, int city_count){
        calculateLand(land_start, land_end);
        this.base_cost = (this.base_cost * city_count);


        this.one_cost = (long) (base_cost * .95);
        this.one_cost_saved = base_cost - one_cost;
        this.two_cost = (long) (base_cost * .90);
        this.two_cost_saved = base_cost - two_cost;
        this.aec_cost = (long) (base_cost * .85);
        this.aec_cost_saved = base_cost - aec_cost;
        this.gsa_cost = (long) (base_cost * .925);
        this.gsa_cost = (long) (this.gsa_cost * .95);
        this.gsa_cost = (long) (this.gsa_cost * .95);
        this.gsa_cost_saved = base_cost - gsa_cost;

    }

    public void formatCost() {
        DecimalFormat format = new DecimalFormat("##,##,##,##,##,##,##0");

        this.base_cost_f = "$" + format.format(this.base_cost);
        this.one_cost_f = "$" + format.format(this.one_cost);
        this.one_cost_saved_f = "$" + format.format(this.one_cost_saved);
        this.two_cost_f = "$" + format.format(this.two_cost);
        this.two_cost_saved_f = "$" + format.format(this.two_cost_saved);
        this.aec_cost_f = "$" + format.format(this.aec_cost);
        this.aec_cost_saved_f = "$" + format.format(this.aec_cost_saved);
        this.gsa_cost_f = "$" + format.format(this.gsa_cost);
        this.gsa_cost_saved_f = "$" + format.format(this.gsa_cost_saved);

    }


}
