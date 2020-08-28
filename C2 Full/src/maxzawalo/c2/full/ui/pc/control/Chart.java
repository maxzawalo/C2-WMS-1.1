package maxzawalo.c2.full.ui.pc.control;

import java.util.List;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import maxzawalo.c2.base.utils.Format;
import maxzawalo.c2.free.ui.pc.resource.style.Style;
import maxzawalo.c2.full.analitics.DateNumber;

public class Chart extends JFXPanel {
	LineChart<String, Number> lineChart;
	String name;

	public Chart(String name) {
		this.name = name;

		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		lineChart = new LineChart<String, Number>(xAxis, yAxis);
		lineChart.setTitle(name);
		lineChart.setLegendVisible(false);

		// xAxis.setLabel("Number of Month");
		// creating the chart

	}

	public void setData(final List<DateNumber> data) {
		Platform.runLater(new Runnable() {
			public void run() {

				if (lineChart.getData().size() > 5)
					lineChart.getData().clear();

				// defining a series
				XYChart.Series series = new XYChart.Series();
				// series.setName(name);
				for (DateNumber value : data)
					series.getData().add(new XYChart.Data(Format.Show("dd.MM", value.date), value.number));

				lineChart.getData().add(series);

				Scene scene = new Scene(lineChart, getWidth(), getHeight());
				setScene(scene);
				scene.getStylesheets().add(Style.class.getResource("stylesheet.css").toExternalForm());

				//// series.nodeProperty()data..get().setStyle("-fx-stroke-width:
				//// 1px;");
				//// lineChart.setStyle(".default-color0.chart-series-line {
				//// -fx-stroke: #e9967a; }");
				// Node nodew = lineChart.lookup(".chart-series-area-line");
				// // Set the first series fill to translucent pale green
				// nodew.setStyle("-fx-stroke: #ff0000; -fx-stroke-width: 1px;
				//// ");

				// .thick-chart .chart-series-line {
				// -fx-stroke-width: 2px;
				// }
				// and use StyleClass instead of Style :
				//
				// lineChart.getStyleClass().add("thick-chart");

			}
		});
	}
}