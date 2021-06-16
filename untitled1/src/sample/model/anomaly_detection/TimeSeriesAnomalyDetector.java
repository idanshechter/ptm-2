package sample.model.anomaly_detection;

import sample.model.anomaly_detection.AnomalyReport;
import sample.model.anomaly_detection.TimeSeries;

import java.util.List;

public interface TimeSeriesAnomalyDetector {
    void learnNormal(TimeSeries ts);
    List<AnomalyReport> detect(TimeSeries ts);
}
