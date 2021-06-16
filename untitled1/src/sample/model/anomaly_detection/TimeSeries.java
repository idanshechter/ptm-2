package sample.model.anomaly_detection;

import sample.model.anomaly_detection.Point;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

// Wraps a csv file, taken from PTM1.

public class TimeSeries {

	private String csvFileName;

	public class column{
		private String name;
		private ArrayList<Float> values;

		public column(String name)
		{
			this.name = name;
			this.values = new ArrayList<Float>();
		}

		public String getName() {
			return name;
		}

		public ArrayList<Float> getValues() {
			return values;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setValues(ArrayList<Float> values) {
			this.values = values;
		}
	}

	private column[] table;

	public TimeSeries(String csvFileName){
		this.csvFileName = csvFileName;
		String line ="";

		try {
			@SuppressWarnings("resource")
			BufferedReader bf = new BufferedReader(new FileReader(csvFileName));
			line = bf.readLine();

			String [] line_value = line.split(",");
			table = new column[line_value.length];

			for( int i=0; i< line_value.length; i++){    //set the name of the column
				table[i] = new column(line_value[i]);
			}
			while ((line = bf.readLine()) != null)      //sets the values of each column, line by line
			{
				line_value = line.split(",");

				for (int j = 0; j < line_value.length; j++)
					table[j].getValues().add(Float.parseFloat(line_value[j]));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public column[] getTable() {
		return table;
	}

	public float[] valuesToArr(ArrayList<Float> values)
	{
		float[] arrFloat = new float[values.size()];

		for (int i = 0; i < values.size(); i++)
			arrFloat[i]=values.get(i);

		return arrFloat;

	}

	public Point[] ArrToPoint(float[] x, float[] y)
	{
		Point[] p = new Point[x.length];

		for (int i = 0; i < x.length; i++)
			p[i] = new Point(x[i],y[i]);

		return p;

	}

	public ArrayList<Float> valuesByName(String name){
		ArrayList<Float> col = new ArrayList<>();
		for (int j = 0; j < this.getTable().length; j++) {
			if (this.getTable()[j].getName().equals(name))
				col = this.getTable()[j].getValues();
		}
		return col;
	}

	public String getCsvFileName() {
		return this.csvFileName;
	}
}
