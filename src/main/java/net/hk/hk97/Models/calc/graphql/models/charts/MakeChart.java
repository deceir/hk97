package net.hk.hk97.Models.calc.graphql.models.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import java.io.File;
import java.text.DecimalFormat;

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
}