package com.belnitskii.telegrambotcb.util;

import com.belnitskii.telegrambotcb.model.Record;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChartUtil {
    public static File generateChart(String charCodeName, List<Record> recordList) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;
        for (Record record : recordList) {
            dataset.addValue(record.getValue(), charCodeName, record.getDate());
            minValue = (int) Math.min(minValue, record.getValue());
            maxValue = (int) Math.max(maxValue, record.getValue());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "График валюты",
                "Дата",
                "Курс",
                dataset);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);
        plot.getRangeAxis().setLowerBound(minValue - 5);
        plot.getRangeAxis().setUpperBound(maxValue + 5);

        File file = new File("Chart.png");
        ChartUtils.saveChartAsPNG(file, chart, 800, 500);
        return file;
    }
}
