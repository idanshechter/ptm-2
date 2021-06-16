package sample.model.anomaly_detection;


import java.util.ArrayList;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {

    private ArrayList<CorrelatedFeatures> arrCorFeatures = new ArrayList<CorrelatedFeatures>();
    float threshold;

    @Override
    public void learnNormal(TimeSeries ts) {

        String[] str_features = new String[ts.getTable().length];

        for (int i = 0; i < ts.getTable().length; i++) {
            str_features[i] = ts.getTable()[i].getName();
        }

        float max_pearson = -2;
        int index = 0;
        float pearson;

        for (int i = 0; i < str_features.length; i++) {

            ArrayList<Float> col1 = ts.getTable()[i].getValues();
            float[] arr_col1 = ts.valuesToArr(col1);
            for (int j = i + 1; j < str_features.length; j++) {
                if (!(str_features[i].equals(str_features[j]))) {
                    ArrayList<Float> col2 = ts.getTable()[j].getValues();
                    float[] arr_col2 = ts.valuesToArr(col2);
                    pearson = Math.abs(StatLib.pearson(arr_col1, arr_col2));
                    if (max_pearson < pearson && pearson > 0.9) {
                        max_pearson = pearson;
                        index = j;
                    }
                }
            }
            if (max_pearson > 0) {
                float[] arr_col2 = ts.valuesToArr(ts.getTable()[index].getValues());
                Point[] arr_points = ts.ArrToPoint(arr_col1, arr_col2);
                Line line = StatLib.linear_reg(arr_points);
                float max_threshold = 0;

                for (int j = 0; j < arr_points.length; j++) {
                    if (max_threshold < StatLib.dev(arr_points[j], line)) {
                        max_threshold = StatLib.dev(arr_points[j], line);
                    }
                }
                //max_threshold += 0.0089;
                max_threshold *= 1.1f;
                arrCorFeatures.add(new CorrelatedFeatures(str_features[i], str_features[index], max_pearson, line, (float)max_threshold));
            }
            max_pearson=-2;
            index=0;
        }
    }

    public float getThreshold(){
        return threshold;
    }

    public void setThreshold(float threshold){
        this.threshold=threshold;
    }
    public SimpleAnomalyDetector(float newThreshold){
        setThreshold(newThreshold);
    }

    @Override
    public List<AnomalyReport> detect(TimeSeries ts) {
        List<AnomalyReport> list = new ArrayList<AnomalyReport>();

        for (int i = 0; i < arrCorFeatures.size(); i++) {

            String feature1 = arrCorFeatures.get(i).feature1;
            String feature2 = arrCorFeatures.get(i).feature2;
            ArrayList<Float> col1 = ts.valuesByName(feature1);
            ArrayList<Float> col2 = ts.valuesByName(feature2);

            float[] arr_col1 = ts.valuesToArr(col1);
            float[] arr_col2 = ts.valuesToArr(col2);
            Point[] arr_points = ts.ArrToPoint(arr_col1, arr_col2);

            for (int j = 0; j < arr_points.length; j++) {

                if (StatLib.dev(arr_points[j], arrCorFeatures.get(i).lin_reg) > arrCorFeatures.get(i).threshold) {
                    AnomalyReport ar = new AnomalyReport(feature1 + "-" + feature2, j + 1);

                    list.add(ar);
                }
            }
        }
        return list;
    }

    public List<CorrelatedFeatures> getNormalModel() {
        return arrCorFeatures;
    }
}
