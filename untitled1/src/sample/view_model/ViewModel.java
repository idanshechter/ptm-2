package sample.view_model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import sample.model.Model;
import sample.model.Properties;
import sample.model.Stream;
import sample.model.anomaly_detection.TimeSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;


public class ViewModel implements Observer {
    Model model;
    File timeSeries;
    Properties flight_values;

    SimpleBooleanProperty isStopped;



    //public final Runnable play, pause, stop;

    public ViewModel() {
        this.model = new Model();
        this.model.addObserver(this);
        this.flight_values = new Properties();
        this.isStopped = new SimpleBooleanProperty(true);
    }

    public SimpleBooleanProperty getSimpleBooleanProperty() {
        return isStopped;
    }

    public SimpleFloatProperty getSimpleFloatProperty(int index) {
        return flight_values.getValue(index);
    }
    public boolean passCSV(File selectedFile) {
        if (!selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".") + 1).equals("csv")) {
            return false;
        } else {
            this.model.setCSV(selectedFile);
            return true;
        }
    }

    public boolean Connect() {
        if(checkFlightGearProcess()) {
            return this.model.Connect();
        } else {
            return false;
        }
    }

    public boolean checkFlightGearProcess() {
        String line;
        String pidInfo ="";
        Process p = null;

        try {
            p = Runtime.getRuntime().exec(System.getenv("windir") +"\\system32\\"+"tasklist.exe");
            BufferedReader input =  new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                pidInfo+=line;
            }
            input.close();

            if(pidInfo.contains("fgfs")) {
                System.out.println("Found FlightGear process.");
                return true;
            } else {
                System.out.println("Couldn't find FlightGear process");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stop() {
        this.model.stop();
        this.isStopped.set(true);
    }

    public void setProperties(Properties properties) {
        this.flight_values = properties;
        //System.out.println();
    }

    @Override
    public void update(Observable o, Object arg) {
        this.setProperties((Properties) arg);
    }

    public void play() {
        if (this.isStopped.getValue()) {
            this.isStopped.set(false);
            this.model.play();
        }
        System.out.println("test");
    }
}
