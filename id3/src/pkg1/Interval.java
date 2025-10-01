package pkg1;

public class Interval {

	public double lowBound;
	public double highBound;
	

	public Interval(double lowBound, double highBound){
		this.lowBound = lowBound;
		this.highBound = highBound;
	}
	

	public Interval(){
		
	}
	
	
	public boolean isValueInInterval(double val){
		if(lowBound< val && val<=highBound)
			return true;
		
		return false;
	}
}
