package net.hk.hk97.Models.calc;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

public class CityCalc {

    @Getter @Setter
    private long base_cost = 0;

    @Getter @Setter
    private String base_cost_formatted;

    @Getter @Setter
    private long md_cost = 0;

    @Getter @Setter
    private long md_cost_saved = 0;

    @Getter @Setter
    private String md_cost_saved_f;

    @Getter @Setter
    private long up_cost = 1;

    @Getter @Setter
    private String up_cost_f;

    @Getter @Setter
    private long up_md_saved = 0;

    @Getter @Setter
    private String up_md_saved_f;

    @Getter @Setter
    private long aup_md_cost = 1;

    @Getter @Setter
    private String aup_md_cost_f;

    @Getter @Setter
    private long aup_md_saved = 0;

    @Getter @Setter
    private String aup_md_saved_f;

    @Getter @Setter
    private String md_cost_formatted;

    @Getter @Setter
    public int cities = 0;

    @Getter @Setter
    public int sixteencities = 0;

    @Getter @Setter
    public long gsa_md = 0;

    @Getter @Setter
    public long gsa_up = 0;

    @Getter @Setter
    public long gsa_aup = 0;

    @Getter @Setter
    private String gsa_saved_f;

    @Getter @Setter
    private String gsa_up_saved_f;

    @Getter @Setter
    private String gsa_aup_saved_f;

    @Getter @Setter
    private long gsa_saved = 0;

    @Getter @Setter
    private long gsa_up_saved = 0;

    @Getter @Setter
    private long gsa_aup_saved = 0;

    @Getter @Setter
    public String gsa_cost_f;

    @Getter @Setter
    public String gsa_up_cost_f;

    @Getter @Setter
    public String gsa_aup_cost_f;





    public void calculateCity(int city) {
        int currentCity = (city - 1);
        if (city > 10) {
            cities++;
        }
        if (city >= 16) {
            sixteencities++;
        }

        this.base_cost += (long) ((long) 50000 * Math.pow((currentCity - 1),3) + (150000 * currentCity) + 75000);

        this.md_cost = (long) (base_cost * .95);
        this.gsa_md = (long) (base_cost * .925);
        this.md_cost_saved = base_cost - md_cost;
        this.gsa_saved = base_cost - gsa_md;

        this.up_cost = (long) ((base_cost - (50000000 * this.cities)) * .95);
        this.gsa_up = (long) ((base_cost - (50000000 * this.cities)) * .925);

        if (up_cost < 0) {
            this.up_cost = 1;
        }
        this.up_md_saved = base_cost - up_cost;
        this.gsa_up_saved = base_cost - gsa_up;

        this.aup_md_cost = (long) ((this.base_cost - (50000000 * this.cities) - (100000000 * this.sixteencities)) * .95);
        this.gsa_aup = (long) ((this.base_cost - (50000000 * this.cities) - (100000000 * this.sixteencities)) * .925);
        if(aup_md_cost < 0) {
            this.aup_md_cost = 1;
        }
        this.aup_md_saved = base_cost - aup_md_cost;
        this.gsa_aup_saved = base_cost - gsa_aup;


        this.formatCost();

    }

    public void calculateCity(int city_one, int city_two) {

        city_one = city_one + 1;
        if (city_one < 10 && city_two > 10) {
            city_one = 10;
        }
        if (city_one == city_two) {
            calculateCity(city_one);
        } else {
            for (int index = city_one; index <= city_two; index++) {
                calculateCity(index);
            }
        }

    }

    public void formatCost() {
        DecimalFormat format = new DecimalFormat("##,##,##,##,##,##,##0");
        this.base_cost_formatted = "$" + format.format(this.base_cost);
        this.md_cost_formatted = "$" + format.format(this.md_cost);
        this.up_cost_f = "$" + format.format(this.up_cost);
        this.aup_md_cost_f = "$" + format.format(this.aup_md_cost);

        this.md_cost_saved_f = "$" + format.format(this.md_cost_saved);
        this.up_md_saved_f = "$" + format.format(this.up_md_saved);
        this.aup_md_saved_f = "$" + format.format(this.aup_md_saved);

        this.gsa_saved_f = "$" + format.format(this.gsa_saved);
        this.gsa_up_saved_f = "$" + format.format(this.gsa_up_saved);
        this.gsa_aup_saved_f = "$" + format.format(this.gsa_aup_saved);
        this.gsa_cost_f = "$" + format.format(this.gsa_md);
        this.gsa_up_cost_f = "$" + format.format(this.gsa_up);
        this.gsa_aup_cost_f = "$" + format.format(this.gsa_aup);
    }


//    public static void main(String[] args) {
//        CityCalc calc = new CityCalc();
//
//        calc.calculateCity(40);
//        System.out.println(calc.aup_md_cost);
//    }

}
