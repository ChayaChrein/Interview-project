import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.regex.Matcher;
	
public class BigID {
	//initialize global variables to be accessed across all methods
	static String[] names = {"James","John","Robert","Michael","William","David","Richard","Charles","Joseph","Thomas","Christopher","Daniel","Paul","Mark","Donald","George","Kenneth","Steven","Edward","Brian","Ronald","Anthony","Kevin","Jason","Matthew","Gary","Timothy","Jose","Larry","Jeffrey", "Frank","Scott","Eric","Stephen","Andrew","Raymond","Gregory","Joshua","Jerry","Dennis","Walter","Patrick","Peter","Harold","Douglas","Henry","Carl","Arthur","Ryan","Roger"};
	static ArrayList<Mapping>[] mapList = (ArrayList<Mapping>[]) new ArrayList[50];
	
/*
 * Mapping class creates an object to store the location of the matches	
 */
public class Mapping{
		public int lineOffset;
		public int charOffset;
		
		public Mapping(int lo, int co) {
			lineOffset=lo;
			charOffset=co;
		}
		
		public String toString() {
			return "[LineOffset: " + lineOffset + ", CharOffset: " + charOffset + "]";
		}
	}

/*
 * readFile reads the txt file and stores every thousand lines in a new index of the arrayList		
 */
public static ArrayList<String> readFile(String myFile) {
		
		BufferedReader br;
		StringBuilder s = new StringBuilder("");
		ArrayList<String> al=new ArrayList<String>();
		try {
			br = new BufferedReader (new FileReader(myFile));
			String line;
			line = br.readLine();
			int lineCount=1;
			while (line!=null) {
				if ((lineCount+1)%1000==0) {
					al.add(s.toString());
					s = new StringBuilder("");
				}
				s.append(line+"\n");
				line=br.readLine();
				lineCount++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 return al;
	}

/*
 * ALtoString converts combines all the entires into a single String object
 */
public static String ALtoString(ArrayList<Mapping> al){
		StringBuilder s = new StringBuilder("[");
		for(int i=0; i<al.size(); i++) {
			s.append(al.get(i).toString());
		}
		s.append("]");
		return s.toString();
	}

/*
 * sendToMatcher implements the Runnable class in order to run threads. It creates a matcher based on the name passed to it and checks through the arrayList of the txt file and stores the location of the objects in the mapList global object	
 */
public class sendToMatcher implements Runnable{
		public ArrayList<String> arrList;
		public String name;
		public int i;
		
		public sendToMatcher(ArrayList<String> arrList, String name, int i) {
			this.arrList=arrList;
			this.name=name;
			this.i=i;
		}
		
		public void run() {
			String patternString = name;
			Pattern pattern = Pattern.compile(patternString);
			BigID.mapList[i]=new ArrayList<Mapping>();
			for (int j=0; j<arrList.size();j++) {
				Matcher matcher = pattern.matcher(arrList.get(j));
				while (matcher.find()) {
					BigID.mapList[i].add(new Mapping(j*1000,matcher.start()));
				}
			}
		}	
	}

/*
* Once mapList has all the values, aggregate combines the data into a String and prints it.
*/
public static void aggregate() {
	StringBuilder s = new StringBuilder ();
		for(int i=0; i<50; i++) {
			s.append(names[i]+"--> [");
			int j=0;
			try {
			while (BigID.mapList[i].get(j)!=null) {
				s.append(BigID.mapList[i].get(j).toString());
				j++;
			}
			} catch (Exception e) {}
			s.append("]\n" );
		}
		System.out.print(s);
	}
	
	public static void main(String args[]) {
		//store the txt file in groups of 1000 lines
		ArrayList<String> thousandLines = new ArrayList<String>();
		thousandLines =	readFile("Big.txt");
		
		//create an array of threads so there can be a separate thread for each name/matcher
		Thread[] t = new Thread[50];
		BigID b=new BigID();
		for(int i=0;i<50;i++) {
			BigID.sendToMatcher stm = b.new sendToMatcher(thousandLines, names[i], i);
			t[i] = new Thread(stm);
			t[i].start();
		}
		
		//make sure all the threads have finished
		for(int i=0;i<50;i++) {
			try {
				t[i].join();
			}catch (InterruptedException e) {}
		}
		
		aggregate();
	}

}
