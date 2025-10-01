package pkg1;

import java.util.ArrayList;

public class Attribute {

	public String name;
	

	public int posInLearningSet;
	public ArrayList<Double> PartitionValue=new ArrayList<Double>();
	
	public ArrayList<Interval> numericIntervalBounds;
	

	public Attribute(String name, int posInLearningSet){
		
		this.numericIntervalBounds = new ArrayList<Interval>();
		this.name = name;
		this.posInLearningSet = posInLearningSet;
	}
	
}
