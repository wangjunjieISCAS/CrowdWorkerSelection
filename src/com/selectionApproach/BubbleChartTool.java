package com.selectionApproach;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.BubbleChartBuilder;
import org.knowm.xchart.XChartPanel;

import com.data.TestTask;

public class BubbleChartTool {
	private static boolean SHOWLOG = true;
	private BubbleChart bubbleChart;
	XChartPanel<BubbleChart> chartPanel;
	
	public BubbleChartTool ( ) {
		bubbleChart = new BubbleChartBuilder().width(500).height(400).title("o0-o2").build();
		bubbleChart.addSeries("o0o2", null, new double[] { 0.1, 0.2, 0.3 }, new double[] { 0.1, 0.2, 0.3 });
		
		chartPanel = new XChartPanel<BubbleChart>(bubbleChart);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// Create and set up the window.
				JFrame frame = new JFrame("XChart");
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.add(chartPanel);

				// Display the window.
				frame.pack();
				if (SHOWLOG)
					frame.setVisible(true);
				else
					frame.dispose();
			}
		});
	}
	
	public void bubbleChartUpdateParetoFront ( ArrayList<double[]> xDataList, ArrayList<double[]> yDataList, ArrayList<double[]> bubbleDataList ) {
		for ( int i =0; i < xDataList.size(); i++) {
			double[] xData = xDataList.get( i );
			double[] yData = yDataList.get( i );
			double[] bubbleData = bubbleDataList.get( i);
			
			bubbleChart.updateBubbleSeries(  "o0o2", xData, yData, bubbleData );
			chartPanel.revalidate();
			chartPanel.repaint();
			
			try {
				Thread.sleep( 200 );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<ArrayList<double[]>> readParetoFrontData ( ) {
		ArrayList<double[]> xDataList = new ArrayList<double[]>();
		ArrayList<double[]> yDataList = new ArrayList<double[]>();
		ArrayList<double[]> bubbleDataList = new ArrayList<double[]>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader( new File ( "data/output/paretoFrontData.txt" )));
			String line = "";
			
			int index = 0;
			while ( ( line = br.readLine() ) != null ) {
				String[] temp = line.split( " ");
				
				double[] data = new double[temp.length];
				for ( int i =0; i < temp.length; i++ ) {
					data[i] = Double.parseDouble( temp[i]);
				}
				
				if ( index % 3 == 0 ) {
					xDataList.add( data );
				}
				else if ( index % 3 == 1 ) {
					yDataList.add( data );
				}
				else if ( index % 3 == 2) {
					bubbleDataList.add( data );
				}
				index++;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ArrayList<ArrayList<double[]>> result = new ArrayList<ArrayList<double[]>>();
		result.add( xDataList );
		result.add( yDataList );
		result.add( bubbleDataList );
		
		return result;
	}
	
	public static void main ( String args[] ) {
		BubbleChartTool chartTool = new BubbleChartTool();
		ArrayList<ArrayList<double[]>> result = chartTool.readParetoFrontData();
		chartTool.bubbleChartUpdateParetoFront( result.get( 0), result.get(1), result.get(2) );
	}
}
