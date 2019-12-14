package a06;

import edu.princeton.cs.algs4.MaxPQ;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

	private WordNet wordnet;

	/**
	 * constructor takes a WordNet object
	 * 
	 * @param wordnet is an object of type WordNet
	 */
	public Outcast(WordNet wordnet){
		this.wordnet = wordnet;
	}

	/**
	 * given an array of WordNet nouns, return an outcast
	 * 
	 * @param nouns array of WordNet nouns
	 * @return outcast
	 */
	public String outcast(String[] nouns){

		ST<Integer, String> st = new ST<>();
		MaxPQ<Integer> maxPQ = new MaxPQ<>();

		for(int i = 0; i < nouns.length; i++){
			int distance = 0;
			for(int j = 0; j < nouns.length; j++){
				distance += wordnet.distance(nouns[i], nouns[j]);
			}

			maxPQ.insert(distance);
			st.put(distance, nouns[i]);
		}

		return st.get(maxPQ.max());
	}
	
	/**
	 * Main method for testing this class
	 * 
	 * @param args (not used)
	 */
	public static void main(String[] args){
		String synsets = "synsets.txt";
		String hypernyms = "hypernyms.txt";
//	    WordNet wordnet = new WordNet(args[0], args[1]);
	    WordNet wordnet = new WordNet(synsets, hypernyms);
	    Outcast outcast = new Outcast(wordnet);
	    String strings = "outcast11.txt"; 
	    In in = new In("src\\a06\\resources\\"+ strings);
	    String[] nouns = in.readAllStrings();
	    StdOut.println(strings + ": " + outcast.outcast(nouns));
	}
}
