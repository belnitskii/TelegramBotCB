package com.belnitskii.telegrambotcb.util;

import com.belnitskii.telegrambotcb.model.Record;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Генерация графика курса валют в стиле сайта ЦБ РФ.
 */
public class ChartUtil {

    /**
     * Генерирует красивый график валютного курса и сохраняет его в PNG.
     *
     * @param recordList список данных (дата, курс)
     * @return файл с изображением графика
     * @throws IOException если произошла ошибка при сохранении
     */
    public static File generateChart(String charCodeName, List<Record> recordList) throws IOException {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;

        for (Record record : recordList) {
            dataset.addValue(record.getValue(), "Курс", record.getDate().substring(0,5));
            minValue = (int) Math.min(minValue, record.getValue());
            maxValue = (int) Math.max(maxValue, record.getValue());
        }

        double lowerBound = Math.floor(minValue / 2.5) * 2.5;
        double upperBound = Math.ceil(maxValue / 2.5) * 2.5;
        // Создаем график (убираем легенду)
        JFreeChart chart = ChartFactory.createLineChart(
                MessageFormat.format("{0} ({1} — {2})", charCodeName, recordList.getFirst().getDate(), recordList.getLast().getDate()),
                null,
                null,
                dataset
        );
        chart.removeLegend(); // Убираем легенду (чтобы не было "EUR" снизу)

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);

        // Линии сетки
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

        // **Вертикальные линии
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setDomainGridlinesVisible(true);

        // Убираем ось X (названия дат останутся)
        plot.getDomainAxis().setLabel(null);
        plot.getDomainAxis().setTickLabelPaint(Color.BLACK);

        // Настраиваем ось Y
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setLabel(null);
        yAxis.setRange(lowerBound, upperBound);
        yAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(2.5));
        yAxis.setTickLabelPaint(Color.BLACK);

        // Стиль графика
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(0, 102, 204));
        renderer.setSeriesStroke(0, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, false);
        plot.setRenderer(renderer);


        File file = new File("Chart.png");
        ChartUtils.saveChartAsPNG(file, chart, 1920,500);
        return file;
    }
}
