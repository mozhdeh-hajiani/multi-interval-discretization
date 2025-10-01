package pkg1;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;



public class mdl {
	public static ArrayList<String> classifySet(Nod nod, ArrayList<LearningObject> workingSet){
		ArrayList<String> resultClasses = new ArrayList<String>();
		System.out.println("Start classify " + workingSet.size());
		for(int i=0; i<workingSet.size();i++){
			LearningObject workObject = workingSet.get(i);
			System.out.println(workObject.toString());
			Nod auxNod = nod;
			while(auxNod.attribute != null ){
			
					for(int j=0; j<auxNod.numericTranzitions.size();j++){
						if(auxNod.numericTranzitions.get(j).isValueInInterval((Double)workObject.values.get(auxNod.attribute.posInLearningSet).getValue())){
							
							auxNod = auxNod.childs.get(j);
							break;
						}
						
						
					}
					
					
				
			}
			System.out.println("Worn object" + i + "+>" +auxNod.classValue);
			resultClasses.add(auxNod.classValue);
			
		}
		
		return resultClasses;
	}

	public static Nod algorithm(ArrayList<LearningObject> learningSet, ArrayList<Attribute> attributes, ArrayList<ClassCategory> classes, Nod nod){
		
		if(learningSet.size() == 0){
			nod.classValue = "Failure";
			return nod;
		}
		
		String onlyOneClass = determineAllObjectSameClass(learningSet);
		
		if(onlyOneClass != null)
		{
			nod.classValue = onlyOneClass;
			return nod;
		}
		
		
		if(attributes.size() == 0){	
			nod.classValue = determineMostFrequentClass(learningSet);
			return nod;
		}
		
		
		double max = Double.NEGATIVE_INFINITY;
		int posChoseAttr = 0;
		
		for(int i=0;i<attributes.size();i++){
			double val = entropyCalc(learningSet) - entropyCalcExt(attributes.get(i), learningSet);
			if(val > max){
				max = val;
				posChoseAttr = i;
			}
		}
		
		nod.attribute = attributes.get(posChoseAttr);
		
		ArrayList<ArrayList<LearningObject>> lSets = new ArrayList<ArrayList<LearningObject>>();
		
		ArrayList<Attribute> newAttributeSet = new ArrayList<Attribute>();
		
		for(int t=0;t<attributes.size();t++)
			if(! attributes.get(t).name.equals(nod.attribute.name))
				newAttributeSet.add(attributes.get(t));
	
		{
			

			ArrayList<Interval> intervals = nod.attribute.numericIntervalBounds;
			
			if(intervals == null)
				System.out.println("Error, numeric intervals should no be null");	
			ArrayList<Interval> tranzitions = new ArrayList<Interval>();
			
			for(int i=0;i<intervals.size();i++){
				Interval interval = intervals.get(i);
				ArrayList<LearningObject> set = new ArrayList<LearningObject>();
				
				for(int j=0;j<learningSet.size();j++){
					if(interval.isValueInInterval((Double)learningSet.get(j).values.get(nod.attribute.posInLearningSet).getValue()))
						set.add(learningSet.get(j));
				}
				
				if(set.size()>0){
	
					tranzitions.add(interval);
	
					lSets.add(set);
				}

			}
			
			nod.numericTranzitions = tranzitions;
			
		}
		
		for(int i=0;i<lSets.size();i++){
			Nod new_nod = new Nod();
			nod.childs.add(new_nod);
			ArrayList<Double> disVal=new ArrayList<Double>();
			for (int i1 = 0; i1 < newAttributeSet.size(); i1++) {
				ArrayList<LearningObject> sorted=new ArrayList<LearningObject>();
				ArrayList<Double> cuts=new ArrayList<Double>();
				Attribute Att=newAttributeSet.get(i1);
				sorted=sort(lSets.get(i),Att.posInLearningSet);
				cuts=posible_cuts(sorted,Att.posInLearningSet);
				disVal=discret(lSets.get(i),newAttributeSet,i1,sorted,cuts);
				partition(disVal,Att,newAttributeSet,i1);
			}
			
			
			algorithm(lSets.get(i),newAttributeSet, classes, new_nod);
		}
		
		return nod;
	}
	public static double entropy(Attribute attr,ArrayList<LearningObject> set){
		

		Integer posCol = attr.posInLearningSet;
		double entropy = 0;
			ArrayList<Interval> intervals = attr.numericIntervalBounds;
			int totalNo = set.size();
			for(int i=0;i<intervals.size();i++){
				Interval interval = intervals.get(i);
				ArrayList<LearningObject> newSet = new ArrayList<LearningObject>();
				
				for(int j=0;j<set.size();j++){
					if(interval.isValueInInterval((double)set.get(j).values.get(posCol).getValue()))
						newSet.add(set.get(j));
				}
				int count = new HashSet<LearningObject>(newSet).size();
				Set<String> itemNames = new HashSet<String>();
				
				for (LearningObject item : newSet)
				    itemNames.add(item.classValue.name);
				 count = itemNames.size();
				if(newSet.size()>0){
										
					entropy += ((double)count/totalNo) * entropyCalc(newSet);
				}	
			}		
		
		
		return entropy;
	}
	public static double entropyCalcExt(Attribute attr,ArrayList<LearningObject> set){
		
		
		Integer posCol = attr.posInLearningSet;
		double entropy = 0;
			ArrayList<Interval> intervals = attr.numericIntervalBounds;
			int totalNo = set.size();
			for(int i=0;i<intervals.size();i++){
				Interval interval = intervals.get(i);
				ArrayList<LearningObject> newSet = new ArrayList<LearningObject>();
				
				for(int j=0;j<set.size();j++){
					if(interval.isValueInInterval((double)set.get(j).values.get(posCol).getValue()))
						newSet.add(set.get(j));
				}

				if(newSet.size()>0){
										
					entropy += ((double)newSet.size()/totalNo) * entropyCalc(newSet);
				}	
			}		
		
		
		return entropy;
	}
	public static double entropyCalc(ArrayList<LearningObject> set){
		
		double entropy = 0;
		HashMap<String,Integer> classes = new HashMap<String, Integer>();
		
		for(int i=0;i<set.size();i++){
			
			Integer val = classes.get(set.get(i).classValue.name);
			if(val == null)
				classes.put(set.get(i).classValue.name, 1);
			else
				classes.put(set.get(i).classValue.name,(val+1));	
		}
		
		int totalNo = set.size();
		
		Collection<Integer> values = classes.values();
		
		for(Integer i : values){
			Double v = (double)i;
			
			entropy -= ( v/totalNo)*log2((v/totalNo));	
		}	
		
		return entropy;
	}
	public static String determineMostFrequentClass(ArrayList<LearningObject> set){
		
		String fqValue = "notDef";
		
		HashMap<String,Integer> classes = new HashMap<String, Integer>();
				
			for(int i=0;i<set.size();i++){	
				Integer val = classes.get(set.get(i).classValue.name);
				if(val == null)
					classes.put(set.get(i).classValue.name, 1);
				else
					classes.put(set.get(i).classValue.name,(val+1));	
				}
				
			Set<Entry<String,Integer>> setVal =  classes.entrySet();
			
			Iterator<Entry<String,Integer>> it = setVal.iterator();
			
			int max = 0;
			while(it.hasNext()){
				Entry<String,Integer> entry =  it.next();
				if(max <= entry.getValue()){
					max = entry.getValue();
					fqValue = entry.getKey();
				}
			}
				
		return fqValue;
	}
	public static String determineAllObjectSameClass(ArrayList<LearningObject> learningSet){
		
		String classValue = learningSet.get(0).classValue.name;
		
		for(int i=1; i< learningSet.size();i++){
			if(!classValue.equals(learningSet.get(i).classValue.name))
				return null;
		}
		
		
		return classValue;
		
	}
	public static double log2(double num)
	{
		if(num == 0)
			return 0;
		
		return (Math.log(num)/Math.log(2));
	} 	
	public static ArrayList<LearningObject> sort(ArrayList<LearningObject> input,int index)
	{
		for (int i = 0; i < input.size(); i++) {
			LearningObject obj1=input.get(i);
			double attV=obj1.values.get(index).getValue();
			for (int j = 0; j < input.size(); j++) {
				LearningObject obj2=input.get(j);
				double attV2=obj2.values.get(index).getValue();
				if(attV<=attV2)
				{
					Collections.swap(input, i,j);
					attV=attV2;

				}
			}
		}
		return input;
	}
	public static ArrayList<Double> posible_cuts(ArrayList<LearningObject> input,int index)
	{
		ArrayList<Double> possible_cuts=new ArrayList<Double>();
		double k=0;
		double attV;
		double attV2;
		String class1,class2;
		for (int i = 0; i < input.size(); i++) {
			LearningObject obj1=input.get(i);
			attV2=obj1.values.get(index).getValue();
			class1=obj1.classValue.name;
			if(i+1==input.size())
			{
				break;
			}
			else
			{
				
				LearningObject obj2=input.get(i+1);
				 attV=obj2.values.get(index).getValue();
				 class2=obj2.classValue.name;
			}
			if(!class1.equals(class2))
				{
					 k=(double)(attV+attV2)/2;
					 possible_cuts.add(k);
						
				}
				
			}
		HashSet hs = new HashSet();
		hs.addAll(possible_cuts);
		possible_cuts.clear();
		possible_cuts.addAll(hs);
		Collections.sort(possible_cuts);
		
		return possible_cuts;
	}
	public static double bestPatition(ArrayList<LearningObject> learn,ArrayList<Double> cuts,Attribute Att){
		double val=Double.NEGATIVE_INFINITY;
	
		
		double max=Double.NEGATIVE_INFINITY;
		
		// add elements to al, including duplicates
		
		for (int j = 0; j < cuts.size(); j++) 	
			{
				Interval interval=new Interval();
				interval.lowBound=Double.NEGATIVE_INFINITY;
				interval.highBound=cuts.get(j);
				Att.numericIntervalBounds.add(interval);
				Interval interval2=new Interval();
				interval2.lowBound=cuts.get(j);
				interval2.highBound=Double.POSITIVE_INFINITY;
				Att.numericIntervalBounds.add(interval2);
			double valu=entropyCalc(learn)-entropyCalcExt(Att,learn);
			if(valu>=max)
			{
				max=valu;
				val=cuts.get(j);
			
			}
			Att.numericIntervalBounds.clear();
			
		}
		int count = new HashSet<LearningObject>(learn).size();
		Set<String> itemNames = new HashSet<String>();
		itemNames=classcount(learn);
		 count = itemNames.size();
	
	
		double num=learn.size();
		double val2=((log2(num-1))/num)+((log2(Math.pow(3, count)-2))/num)-((count*entropyCalc(learn))/num)+entropy(Att,learn);
		if(max>val2 )
		{
			return val;	
			
		}
		else
			return Double.NEGATIVE_INFINITY;
	}
	public static ArrayList<Double> discret(ArrayList<LearningObject> learn,ArrayList<Attribute> attributes,int index,ArrayList<LearningObject>sorted,ArrayList<Double> cuts)
	{
		ArrayList<Double> Tpart=new ArrayList<Double>();
		ArrayList<Double> cut1=new ArrayList<Double>();
		ArrayList<Double> cut2=new ArrayList<Double>();
		ArrayList<Double> part1=new ArrayList<Double>();
		ArrayList<Double> part2=new ArrayList<Double>();
		double arr=0;
		ArrayList<Double> n=new ArrayList<Double>();
		if(learn.size()==0)
			return n;
		if(cuts.size()==0)
			return n;
		String onlyOneClass = determineAllObjectSameClass(learn);
		if(onlyOneClass != null)
		{
			return n ;
		}
		
			Attribute Att=new Attribute(attributes.get(index).name,attributes.get(index).posInLearningSet);
			Att=attributes.get(index);
			arr=bestPatition(learn, cuts, Att);
			if(arr==Double.NEGATIVE_INFINITY)
			{
				return n;
			}
				
			Tpart.add(arr);
			
				cuts.remove(cuts.indexOf(arr));
					
		
			Att.PartitionValue.add(arr);
				
				ArrayList<LearningObject> newSet = new ArrayList<LearningObject>();
				ArrayList<LearningObject> newSet2 = new ArrayList<LearningObject>();
					for(int j=0;j<learn.size();j++){
						if((double)learn.get(j).values.get(index).getValue()<Att.PartitionValue.get(0))
							newSet.add(learn.get(j));
						else if((double)learn.get(j).values.get(index).getValue()>=Att.PartitionValue.get(0))
							newSet2.add(learn.get(j));
					}
					for (int i = 0; i < cuts.size(); i++) {
						if(cuts.get(i)<Att.PartitionValue.get(0))
							cut1.add(cuts.get(i));
						else
							cut2.add(cuts.get(i));
					}
				
				Att.PartitionValue.clear();
					part1=discret(newSet,attributes,index,sorted,cut1);
					  if (part1 != null) {  
		                    Tpart.addAll(Tpart.size(), part1);  
		                }
					part2=discret(newSet2,attributes,index,sorted,cut2);
					 if (part2 != null) {  
		                    Tpart.addAll(Tpart.size(), part2);  
		                }
					 
					 return Tpart;
				}
	public static Set<String> classcount(ArrayList<LearningObject>learn){
		
		Set<String> itemNames = new HashSet<String>();
		
		for (LearningObject item : learn)
		    itemNames.add(item.classValue.name);
		
		return itemNames;
	}
	public static void partition(ArrayList<Double> disVal,Attribute att,ArrayList<Attribute> AttArr,int index){
		Collections.sort(disVal);
		Interval interval1=new Interval();
		Interval interval=new Interval();
		if(disVal.size()==0)
		{
			Interval in=new Interval();
			in.lowBound=Double.NEGATIVE_INFINITY;
			in.highBound=Double.POSITIVE_INFINITY;
			att.numericIntervalBounds.add(in);
			return;
		}
		interval.lowBound=Double.NEGATIVE_INFINITY;
		interval.highBound=disVal.get(0);
		att.numericIntervalBounds.add(interval);
		for (int i = 0; i < disVal.size()-1; i++) {
			Interval interval2=new Interval();
			interval2.lowBound=disVal.get(i);
			interval2.highBound=disVal.get(i+1);
			att.numericIntervalBounds.add(interval2);
		}
		
		interval1.lowBound=disVal.get(disVal.size()-1);
		interval1.highBound=Double.POSITIVE_INFINITY;
		att.numericIntervalBounds.add(interval1);
		
		
	}
	public static void main(String[] args) {
		int totalTP=0;
		int totalFP=0;
		int totalFN=0;
		int totalaccTrain=0;
		int totalAccTest=0;
		int T=0;
		ArrayList<ClassCategory> classes=new ArrayList<ClassCategory>();
		ArrayList<Attribute> attributes=new ArrayList<Attribute>();
		ArrayList<LearningObject> learningSet=new ArrayList<LearningObject>();

		try{
		int k=0;
		int m=0;
		int n=0;
		FileInputStream fstream = new FileInputStream("glass.names.txt"); 
		FileInputStream fstream2 = new FileInputStream("glass.data.txt");
		Scanner s=new Scanner(fstream);
		k=s.nextInt();
		for (int i = 0; i < k; i++) {
			String name=s.next();
			ClassCategory classObject=new ClassCategory(name);
			classes.add(classObject);
		}
		m = s.nextInt();
		for(int i=0;i<m;i++){
			String name=s.next();
			attributes.add(new Attribute(name,i));
		}
		s.close();
		Scanner scanSet = new Scanner(fstream2);
		n = scanSet.nextInt();

		for(int i=0; i<n; i++){
			
			ArrayList<AttributeValue> values = new ArrayList<AttributeValue>();
			String classValue= "";
			for(int j=0; j<m; j++){
			
					values.add(new AttributeValue(scanSet.next()));
			}
			
			classValue = scanSet.next();
			LearningObject obj = new LearningObject(values,new ClassCategory(classValue));	
			learningSet.add(obj);
			
			
		}
	
		ArrayList<LearningObject> testSet=new ArrayList<LearningObject>();
		ArrayList<ArrayList<LearningObject>> classobj=new ArrayList<ArrayList<LearningObject>>();
		
		for (int i = 0; i < classes.size(); i++) {
			ArrayList<LearningObject> temp=new ArrayList<LearningObject>();
			for (int j = 0; j < learningSet.size(); j++) {
				if(learningSet.get(j).classValue.name.equals(classes.get(i).name))
					temp.add(learningSet.get(j));
				
			}
			classobj.add(temp);
		}
		double min=Double.POSITIVE_INFINITY;
		int index=0;
		for (int i = 0; i < classobj.size(); i++) {
			if(classobj.get(i).size()<min)
			{
				min=classobj.get(i).size();
				index=i;
			}
		}
		String minorityClass=classes.get(index).name;
		for (int g = 0; g< 10; g++) {
			ArrayList<LearningObject> learningSet2=new ArrayList<LearningObject>();
			ArrayList<LearningObject> test=new ArrayList<LearningObject>();
			ArrayList<LearningObject> test2=new ArrayList<LearningObject>();
		ArrayList<Integer> random=new ArrayList<Integer>();
		ArrayList<String> lable=new ArrayList<String>();
		learningSet2.clear();
		test.clear();
		lable.clear();
			for (int j = 0; j < classobj.size(); j++) {
				random.clear();
				if(classobj.get(j).size()==0)
					continue;
				int testsize=(int)Math.round(0.2*classobj.get(j).size());
				int trainsize=classobj.get(j).size()-testsize;
				if(testsize==0 && trainsize==0)
					continue;
				Random rand=new Random();
				random.add(rand.nextInt(classobj.get(j).size()-1));
				LearningObject learn=new LearningObject(classobj.get(j).get(random.get(0)).values,new ClassCategory(""));
				lable.add(classobj.get(j).get(random.get(0)).classValue.name);
				test.add(learn);
				int Rand=0;
				for (int i = 1; i < testsize; i++) {
					boolean flag=true;
					while(flag==true){
						int count=0;
						Rand=rand.nextInt(classobj.get(j).size()-1);
					for (int l = 0; l < random.size(); l++) {
						if(Rand==random.get(l))
							count++;
					}
					if(count==0)
						flag=false;
					}
					random.add(Rand);
					LearningObject learn2=new LearningObject(classobj.get(j).get(Rand).values,new ClassCategory(""));
					lable.add(classobj.get(j).get(Rand).classValue.name);
					test.add(learn2);
				}
				////////////////////////////////////////
				
					for (int l = 0; l < classobj.get(j).size(); l++) {
						int c=0;
						for (int l2 = 0; l2 < random.size(); l2++) {
							if(l==random.get(l2))
							{
								c++;
								
							}
						}
						if(c==0)
							learningSet2.add(classobj.get(j).get(l));
					
				}
			
				
				
			}
		
	/**
	 * 
	 */
		
		
			
		
		ArrayList<Double> disVal=new ArrayList<Double>();
		for (int i = 0; i < attributes.size(); i++) {
			ArrayList<LearningObject> sorted=new ArrayList<LearningObject>();
			ArrayList<Double> cuts=new ArrayList<Double>();
			Attribute Att=attributes.get(i);
			sorted=sort(learningSet2,Att.posInLearningSet);
			cuts=posible_cuts(sorted,Att.posInLearningSet);
			disVal=discret(learningSet2,attributes,i,sorted,cuts);
			partition(disVal,Att,attributes,i);
		}
		
		/*Nod nod = new Nod();
		nod=algorithm(learningSet, attributes, classes, nod);
		ArrayList<String> class_lable=new ArrayList<String>();
		for (int i = 0; i < learningSet.size(); i++) {
			class_lable.add(learningSet.get(i).classValue.name);
			LearningObject learn=new LearningObject(learningSet.get(i).values,new ClassCategory(""));
			testSet.add(learn);
		}
		ArrayList<String> result = classifySet(nod, testSet);
		
		for (int i = 0; i < result.size(); i++) {
			if(result.get(i).equals(class_lable.get(i)))
				T++;
		}
		System.out.print("accuracy on train data=");
		System.out.println(((float)T*100/(float)learningSet.size())+"%");
		System.out.println("______________________________________");*/
		
		////////////////////////////////////
		Nod nod2 = new Nod();
		nod2=algorithm(learningSet2, attributes, classes, nod2);
		ArrayList<String> class_lable=new ArrayList<String>();
		for (int i = 0; i < learningSet2.size(); i++) {
			class_lable.add(learningSet2.get(i).classValue.name);
			LearningObject learn=new LearningObject(learningSet2.get(i).values,new ClassCategory(""));
			test2.add(learn);
		}
		ArrayList<String> result2 = classifySet(nod2, test2);
		System.out.println("**********************************************");
		System.out.println("**********************************************");
		System.out.println("**********************************************");
		System.out.println("**********************************************");
		T=0;
		for (int i = 0; i < result2.size(); i++) {
			if(result2.get(i).equals(class_lable.get(i)))
				T++;
		}
		totalaccTrain+=((float)T*100/(float)learningSet2.size());
		//System.out.print("accuracy on train data=");
		//System.out.println(((float)T*100/(float)learningSet2.size())+"%");
		//System.out.println("**********************************************");
	
		ArrayList<String> result3 = classifySet(nod2, test);
		System.out.println("**********************************************");
		int T2=0;
		for (int i = 0; i < result3.size(); i++) {
			if(result3.get(i).equals(lable.get(i)))
				T2++;
		}
		//System.out.print("accuracy on test data=");
		//System.out.println(((float)T2*100/(float)test.size())+"%");
		totalAccTest+=((float)T2*100/(float)test.size());
		//System.out.println("**********************************************");
		System.out.println("**********************************************");
		System.out.println("**********************************************");
		int tp=0;
		int fp=0;
		int fn=0;
		for (int i = 0; i < result3.size(); i++) {
			if(result3.get(i).equals(lable.get(i)) && lable.get(i).equals(minorityClass))
			{
				tp++;
				
			}
			else if(result3.get(i).equals(minorityClass) && !lable.get(i).equals(minorityClass))
				fp++;
			else if(!result3.get(i).equals(lable.get(i)) && lable.get(i).equals(minorityClass))
				fn++;
		}
		totalTP+=tp;
		totalFN+=fn;
		totalFP+=fp;
		/*double t1=0;
		double t2=0;
		for (int j = 0; j < classes.size(); j++) {
		for (int i = 0; i < result3.size(); i++) {
			if(result3.get(i).equals(lable.get(i)) && lable.get(i).equals(classes.get(j).name))
				tp++;
			else if(result3.get(i).equals(classes.get(j).name) && !lable.get(i).equals(classes.get(j).name))
				fp++;
			else if(!result3.get(i).equals(lable.get(i)) && lable.get(i).equals(classes.get(j).name))
				fn++;
		}
		t1=tp+fp;
		t2=tp+fn;
		}*/
		/*double precision=0;
		double recall=0;
		double f_measure=0;
		if(tp!=0)
		{
		 precision=((double)(tp)/(double)(tp+fp))*100;
		 
		 //precision=((double)(tp)/(double)(t1))*100;
		}else
		{
			precision=0;
		}
		if(tp!=0){
			recall=((double)(tp)/(double)(tp+fn))*100;
			//recall=((double)(tp)/(double)(t2))*100;
		}else
		{
			recall=0;
		}*/
		/*if(precision==0 || recall==0)
		{
			f_measure=0;
		}
		else
		{
			f_measure=(double)(2*recall*precision)/(double)(recall+precision);
		}
		System.out.println("precision:"+precision);
		System.out.println("recall:"+recall);
		System.out.println("f_measure:"+f_measure);*/
		System.out.println("*******************************");
		System.out.println("*******************************");
		
		
		}
		double precision=0;
		double recall=0;
		double f_measure=0;
		if(totalTP!=0)
		{
		 precision=((double)(totalTP)/(double)(totalTP+totalFP))*100;
		 
		 //precision=((double)(tp)/(double)(t1))*100;
		}else
		{
			precision=0;
		}
		if(totalTP!=0){
			recall=((double)(totalTP)/(double)(totalTP+totalFN))*100;
			//recall=((double)(tp)/(double)(t2))*100;
		}else
		{
			recall=0;
		}
		if(precision==0 || recall==0)
		{
			f_measure=0;
		}
		else
		{
			f_measure=(double)(2*recall*precision)/(double)(recall+precision);
		}
		System.out.println(":::::::::::::::::::::::::::::::::");
		System.out.println("precision:"+precision);
		System.out.println("recall:"+recall);
		System.out.println("f_measure:"+f_measure);
		System.out.println("::::::::::::::::::::::::::::::::::");
		System.out.println("accuracy train:"+totalaccTrain/10);
		System.out.println("accuracy test:"+totalAccTest/10);
		System.out.println("::::::::::::::::::::::::::::::::::");
		}
		catch(Exception e)
		{
			System.out.println("exception e:"+e.getMessage());
		}
		
	}
	
	
}
