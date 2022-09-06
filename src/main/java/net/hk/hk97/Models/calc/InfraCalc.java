package net.hk.hk97.Models.calc;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;


public class InfraCalc {

    @Getter @Setter
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

    public double infraPrice(double amount) {
        return (Math.pow(Math.abs(amount -10 ), 2.2) / 710) + 300;
    }

    public void calculateInfra(long infra_start, long infra_end) {

        double difference = infra_end - infra_start;
        double timesDivisible;
        double currentInfra = infra_start;
        double costOfChunk;

        if (difference > 100 && difference % 100 == 0) {
            timesDivisible = difference / 100;
            while (timesDivisible > 0) {
                this.base_cost += (Math.round(infraPrice(currentInfra) * 100));
                currentInfra += 100;
                timesDivisible -= 1;
            }
        }
        if (difference > 100 && difference % 100 != 0) {
            costOfChunk = (infraPrice(infra_start) * (difference % 100));
            this.base_cost += costOfChunk;
            calculateInfra((Math.round(Math.floor(infra_start) + (difference % 100))), infra_end);
        }
        if (difference <= 100) {
            this.base_cost += infraPrice(infra_start) * difference;
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

    public void calculateInfra(int infra_start, int infra_end, int city_count){
        calculateInfra(infra_start, infra_end);
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

//    public static void main(String[] args) {
//        InfraCalc calc = new InfraCalc();
//        calc.calculateInfra(2800, 2950);
//        calc.formatCost();
//        System.out.println(calc.base_cost_f);
//    }


}
