package pkg1;

public class AttributeValue {

	
	private double valueNumeric;
	
	public String valueString;
	
	
	public AttributeValue(String val){
	
	    this.valueNumeric = Double.parseDouble(val);
		this.valueString = val;
	}
	
	public void setValue(String val){
		
		this.valueNumeric = Double.parseDouble(val);
		this.valueString = val;
	}
	
	public void setValue(double val){
		this.valueNumeric = val;
	}
	
	public double getValue(){
		
			return this.valueNumeric;
		
		
	}
}
