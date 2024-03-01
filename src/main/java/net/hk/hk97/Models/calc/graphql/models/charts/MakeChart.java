package net.hk.hk97.Models.calc.graphql.models.charts;

import net.hk.hk97.Models.Bank.AllianceBankHistory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.DefaultValueDataset;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class MakeChart {



    public static File generatePieChart(String title, long infrastructure, long land, long cities, long projects) throws Exception {
        DecimalFormat format = new DecimalFormat("##,##,##,##,##,##,##0");

        DefaultPieDataset dataset = new DefaultPieDataset( );
        dataset.setValue("Infrastructure $" + format.format(infrastructure) , infrastructure );
        dataset.setValue("Land $" + format.format(land), land);
        dataset.setValue("Cities $" + format.format(cities), cities );
        dataset.setValue("Projects $" + format.format(projects), projects );

        JFreeChart chart = ChartFactory.createPieChart(
                title,   // chart title
                dataset,          // data
                true,             // include legend
                true,
                false);

        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */
        File pieChart = new File( "PieChart.jpeg" );
        TextTitle disclaimer = new TextTitle("Disclaimer: This chart assumes uniform land and infra levels.");
        chart.addSubtitle(disclaimer);
        ChartUtils.saveChartAsJPEG( pieChart , chart , width , height );

        return pieChart;
    }

    public static File generateOtherChart(String title, List<AllianceBankHistory> historyList) throws IOException {


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        final String cashVal = "CASH";


//        DecimalFormat df = new DecimalFormat("0.00M");

        for (AllianceBankHistory history : historyList) {
            String date = history.getDate().getDayOfMonth() + "";
            dataset.addValue((history.getCash()/ 1000000000), cashVal, date);
        }


        JFreeChart barchart = ChartFactory.createLineChart(
                title,
                "Day of Month",
                "Value (in Billions)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false

        );


        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */
        File pieChart = new File( "BarCashChart.jpeg" );
        TextTitle disclaimer = new TextTitle("Do not share this image outside of this channel under any circumstances.");
        barchart.addSubtitle(disclaimer);
        ChartUtils.saveChartAsJPEG(pieChart, barchart, width, height);

        return pieChart;
    }

    public static File generateRssOtherChart(String title, List<AllianceBankHistory> historyList) throws IOException {


        DefaultCategoryDataset dataset = new DefaultCategoryDataset();


        final String steelVal = "STEEL";
        final String gasVal = "GASOLINE";
        final String aluVal = "ALUMINUM";
        final String muniVal = "MUNITIONS";



        for (AllianceBankHistory history : historyList) {
            String date = history.getDate().getDayOfMonth() + "";

            dataset.addValue(history.getSteel(), steelVal, date);
            dataset.addValue(history.getGasoline(), gasVal, date);
            dataset.addValue(history.getAluminum(), aluVal, date);
            dataset.addValue(history.getMunitions(), muniVal, date);

        }


        JFreeChart barchart = ChartFactory.createBarChart(
                title,
                "Day of Month",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false

        );


        int width = 640;   /* Width of the image */
        int height = 480;  /* Height of the image */
        File pieChart = new File( "BarChart.jpeg" );
        TextTitle disclaimer = new TextTitle("Do not share this image outside of this channel under any circumstances.");
        barchart.addSubtitle(disclaimer);
        ChartUtils.saveChartAsJPEG(pieChart, barchart, width, height);

        return pieChart;
    }
}