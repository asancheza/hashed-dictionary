import java.io.*;
import java.util.*;

public class Sample {
  public static void main(String[] args) {
    File file = new File("alice.txt");
	  //Map<String, Integer> wc = new HashMap<String, Integer>();
	  DictionaryInterface wc = new HashedDictionary();

   	try {
			Scanner sc = new Scanner(new FileInputStream(file));
			int count=0;

			while(sc.hasNext()){
			    String myword = sc.next();
			    String word = myword.replaceAll("\\W", "").toLowerCase();
			    
			    //System.out.println(word);
			    
			    if (!wc.contains(word))
            wc.add(word, 0);

          wc.add(word, (int)wc.getValue(word) + 1);

			    count++;
			}

			System.out.println("Number of words: " + count);
			System.out.println("Number times of Alice: " + wc.getValue("alice"));
			System.out.println("Number times of Rabbit: " + wc.getValue("rabbit"));
			System.out.println("Number times of Cheshire: " + wc.getValue("cheshire"));
			System.out.println("Number times of Mad: " + wc.getValue("mad"));
			System.out.println("Number times of Hatter: " + wc.getValue("hatter"));
		
		} catch (FileNotFoundException e) {
        /* handle */
    } 

    /*for (String key: wc.keySet()) {
    	System.out.println(key+" repeated by: "+wc.get(key));
    }*/

    //Map sortedMap = sortByValue(wc);

		//for (Object key: sortedMap.keySet()) {
    //	System.out.println(key+" repeated by: "+wc.get(key));
    //}
  }

  public static Map sortByValue(Map unsortedMap) {
		Map sortedMap = new TreeMap(new ValueComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}
}

class ValueComparator implements Comparator {
	Map map;
 
	public ValueComparator(Map map) {
		this.map = map;
	}
 
	public int compare(Object keyA, Object keyB) {
		Comparable valueA = (Comparable) map.get(keyA);
		Comparable valueB = (Comparable) map.get(keyB);
		return valueB.compareTo(valueA);
	}
}