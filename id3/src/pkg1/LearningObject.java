package pkg1;

import java.util.ArrayList;

public class LearningObject {
	
	public ArrayList<AttributeValue> values;
	
	
	public ClassCategory classValue;
	
	
	public LearningObject(ArrayList<AttributeValue> values,ClassCategory classValue){
		this.values = values;
		this.classValue = classValue;
	}
	
	public LearningObject(){
	}
	public String toString(){
		String result = "";
		for(int j=0;j<values.size();j++)
			result += values.get(j).valueString + " ";
		
		result += classValue.name;
		
		return result;
	}
}
