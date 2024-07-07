package net.hk.hk97.Models.CityBuild.Revenue;

import lombok.Data;

@Data
public class CityRevenue {

    double revenue;
    double expenses;
    double profit;
    double food;
    double coal;
    double iron;
    double lead;
    double oil;
    double uranium;
    double bauxite;
    double munitions;
    double gasoline;
    double steel;
    double aluminum;
    double foodConsumed;
    double coalConsumed;
    double ironConsumed;
    double uraniumConsumed;
    double bauxiteConsumed;
    double oilConsumed;
    double leadConsumed;
    double netProfit;


    public void setNetProfitValue(double foodPrice, double coalPrice, double ironPrice, double leadPrice, double oilPrice, double uraniumPrice, double bauxitePrice, double munitionsPrice, double gasolinePrice, double steelPrice, double aluminumPrice) {
        double rssExpenses = (this.foodConsumed * foodPrice) + (this.coalConsumed * coalPrice) + (this.ironConsumed * ironPrice) + (this.leadConsumed * leadPrice) + (this.oilConsumed * oilPrice) + (this.uraniumConsumed * uraniumPrice) + (this.bauxiteConsumed * bauxitePrice);

        this.netProfit = (foodPrice * this.food) + (coalPrice * this.coal)+ (ironPrice * this.iron) + (leadPrice * this.lead) + (oilPrice * this.oil) + (uraniumPrice * this.uranium) + (bauxitePrice * this.bauxite) + (munitionsPrice * this.munitions) + (gasolinePrice * this.gasoline) + (steelPrice * this.steel) + (aluminumPrice * this.aluminum);

        this.netProfit = this.netProfit - rssExpenses;
    }

}
