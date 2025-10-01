package pkg1;

import java.util.ArrayList;

public class Nod {

	public ArrayList<Nod> childs;
	
	public Attribute attribute = null;
	
	
	public String classValue= null;
	
	
	public ArrayList<Interval> numericTranzitions = null;
	
	
	
	public Nod(){
		this.childs = new ArrayList<Nod>();
	}
}
