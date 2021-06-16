package sample.model;

import javafx.beans.property.SimpleFloatProperty;

public class Properties {
    private SimpleFloatProperty[] values;

    public Properties() {
        this.values = new SimpleFloatProperty[41];
        for (int i = 0; i < 41; i++) {
            values[i] = new SimpleFloatProperty();
        }
    }

    public SimpleFloatProperty getValue(int index) {
        return this.values[index];
    }

    public void setValue(int index, float value) {
        this.values[index].setValue(value);
    }

    public void setValues(String line) {
        for (int i = 0; i < 41; i++) {
            //System.out.print(line.split(",",0)[i] + " ");
            setValue(i,Float.parseFloat(line.split(",",0)[i]));
        }
    }
}
