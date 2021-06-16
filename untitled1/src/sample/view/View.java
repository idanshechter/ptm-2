package sample.view;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import sample.model.Connection;
import sample.model.Properties;
import sample.model.Stream;
import sample.model.anomaly_detection.TimeSeries;
import sample.view_model.ViewModel;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class View implements Initializable {

    @FXML
    Label fileLabel, connectionLabel;
    @FXML
    Button fileButton, connectButton, play, stop, backwards, forward;
    @FXML
    Slider rudder, throttle;
    @FXML
    LineChart<String, Number> featureLineChart;
    @FXML
    Canvas featureCanvas;
    @FXML
    ComboBox<String> comboBox;


    private String[] speeds = {"0.25", "0.5", "0.75", "1", "1.25", "1.5", "1.75", "2"};
    ViewModel viewModel;
    File CSV;

    SimpleFloatProperty[] values;
    SimpleBooleanProperty isStopped;

    public View() {
        this.viewModel = new ViewModel();
        values = new SimpleFloatProperty[42];
        this.isStopped = new SimpleBooleanProperty(true);
        this.isStopped.bind(viewModel.getSimpleBooleanProperty());
        bindValues();
    }

    public void bindValues() {
        for (int i = 0; i < 41; i++) {
            this.values[i] = new SimpleFloatProperty();
            this.values[i].bind(viewModel.getSimpleFloatProperty(i));
        }
    }

    public void chooseFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        CSV = fc.showOpenDialog(null);
        if (CSV != null) {
            if (viewModel.passCSV(CSV)) {
                fileLabel.setText("Opened file " + CSV.getName());
                play.setDisable(false);
                stop.setDisable(false);
                backwards.setDisable(false);
                forward.setDisable(false);
            } else {
                fileLabel.setText("Not a CSV file");
            }
        } else {
            fileLabel.setText("Error, null file.");
        }
    }

    public void paint() { // To do: attach joystick to features: aileron,elevators
        GraphicsContext gc = featureCanvas.getGraphicsContext2D();
        double mx = featureCanvas.getWidth()/2;
        double my = featureCanvas.getHeight()/2;
        gc.clearRect(0,0,featureCanvas.getWidth(), featureCanvas.getHeight());
        gc.strokeOval(mx-50,my-40,80,80); //painting a circle
    }

    public void stop() {
        this.viewModel.stop();
        System.out.println(this.isStopped.get());
    }
    public void paint(XYChart.Series<String, Number> series, String x, int y) {
        series.getData().add(new XYChart.Data(x, y));
        featureLineChart.getData().add(series);
    }
    public void play() {
        this.viewModel.play();
        int i = 0;

        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
        System.out.println(this.isStopped.get());
        while(!isStopped.get()) {
            paint(series, "bla", i);
            series = new XYChart.Series<>();
            i++;
        }


//        featureLineChart.setAnimated(true);
//        XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
//        series.getData().add(new XYChart.Data("1", 1));
//        series.getData().add(new XYChart.Data("2", 30));
//
//        featureLineChart.getData().add(series);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

