package net.hk.hk97.Models;

import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

public class WarRange {

    @Getter @Setter
    private long min_dec;

    @Getter @Setter
    private String min_dec_f;

    @Getter @Setter
    private long max_dec;

    @Getter @Setter
    private String max_dec_f;

    @Getter @Setter
    private long min_def;

    @Getter @Setter
    private String min_def_f;

    @Getter @Setter
    private long max_def;

    @Getter @Setter
    private String max_def_f;

    @Getter @Setter
    private long min_offspy;

    @Getter @Setter
    private String min_offspy_f;

    @Getter @Setter
    private long max_offspy;

    @Getter @Setter
    private String max_offspy_f;




    public void calculateRange(long score) {
        this.min_dec = (long) (score * 0.75);
        this.max_dec = (long) (score * 1.75);

        this.min_def = (long) (score * 0.57143);
        this.max_def = (long) (score * 1.33333);

        this.min_offspy = (long) (score * 0.40);
        this.max_offspy = (long) (score * 2.50);

    }


    public void formatScore() {
        DecimalFormat format = new DecimalFormat("##,##,##,##,##,##,##0");

        this.min_dec_f = format.format(this.min_dec);
        this.max_dec_f = format.format(this.max_dec);

        this.min_def_f = format.format(this.min_def);
        this.max_def_f = format.format(this.max_def);

        this.min_offspy_f = format.format(this.min_offspy);
        this.max_offspy_f = format.format(this.max_offspy);


    }


    public static void main(String[] args) {
        WarRange warRange = new WarRange();
        warRange.calculateRange(2649);
        System.out.println(warRange.min_dec);
        System.out.println(warRange.max_dec);
        System.out.println(warRange.min_def);
        System.out.println(warRange.max_def);

    }
}
