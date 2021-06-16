package sample.model;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import sample.model.anomaly_detection.TimeSeries;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class Model extends Observable {
    public Stream stream;
    public File CSV;
    int step;
    Thread player;
    boolean stopPlayer;
    Properties properties;

    public Model() {
        this.stream = new Stream();
        this.CSV = null;
        this.step = 0;
        this.stopPlayer = false;
        properties = new Properties();

        Runnable play = () -> {
            //System.out.println("test");
            //this.stream.playStream();
            Socket fg = null;
            try {
                fg = new Socket("localhost", 5400);
                BufferedReader in = new BufferedReader(new FileReader(this.CSV.getAbsolutePath()));
                PrintWriter out = new PrintWriter(fg.getOutputStream());

                String line;
                line = in.readLine();

                if (this.step > 0) { // Fast move to last step
                    for (int i = 0; i < step; i++) {
                        line = in.readLine();
                    }
                }

                while (((line = in.readLine()) != null)) {
                    if (getStopPlayer()) {
                        freeze();
                    }
                    updateProperties(line);
                    out.println(line);
//                    for (int i = 0; i < 41; i++) {
//                        System.out.print(properties[i].getValue() + ", ");
//                    }

                    out.flush();
                    step++;
                   // System.out.println("Step: " + step);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                this.step = step;
                out.close();
                in.close();
                fg.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        this.player = new Thread(play);
    }


    public void setStopPlayer(boolean stopPlayer) {
        this.stopPlayer = stopPlayer;
    }

    public boolean getStopPlayer() {
        return stopPlayer;
    }

    public void updateProperties(String line) {
        this.properties.setValues(line);
        setChanged();
        notifyObservers(properties);
    }

    public void freeze() {
        System.out.println("Thread stopped");
        while (getStopPlayer()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void setCSV(File CSV) {
        this.CSV = CSV;
    }

    public boolean Connect() {
        return this.stream.Connect();
    }

    public void play() {
        if(!getStopPlayer())
            this.player.start();
        else
            setStopPlayer(false);
    }

    public void stop() {
        setStopPlayer(true);
    }


}
