package a06;

/**
 * WordNet reads in a list of words, and group words into synsets and describe the relationship between them
 * @author Hai Le and Deokhee Kang
 * 11/25/19
 */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SeparateChainingHashST;

public class WordNet {
	//Create a SeparateChainingHashST with parameters String and a Queue of integers named wordToIntegerMap
	SeparateChainingHashST<String, Queue<Integer>> wordToIntegerMap; 
	//Create a SeparateChainingHashST with parameters int and string named integerToWordMap
	SeparateChainingHashST<Integer, String> integerToWordMap;
	//Create a Digraph named graph
	Digraph graph;
	//Create an SAP named sap
	SAP sap;
	
	/**
	 * Constructor that makes a WordNet with two parameters of two strings
	 * @param synsets is what group the word is in
	 * @param hypernyms is the connection of a word that relates to another
	 */
	public WordNet(String synsets, String hypernyms) {
		//Throw NullPointerException if synsets or hypernyms are null
		if (synsets == null || hypernyms == null) throw new NullPointerException("Arguments cannnot be null.");
		//Set wordToIntegerMap and integerToWordMap to a new SeparateChainHashST with no parameters
		wordToIntegerMap = new SeparateChainingHashST<>();
		integerToWordMap = new SeparateChainingHashST<>();
		int vertices = 0; //Create a new int named vertices with the value of 0
		In in = new In(synsets); //Create a new in with parameter synsets that will take in all the synsets
		
		//While there are still synsets to be read in
		while (in.hasNextLine()) {
			vertices++; //Increment the vertices by 1 each loop
			String[] line = in.readLine().split(","); //Create a string array named line that will split lines at comma
			String[] words = line[1].split(" "); //Create a string array named words that will split words at space
			Integer number = Integer.valueOf(line[0]); //Create an integer named number with the value of line index 0
			integerToWordMap.put(Integer.valueOf(line[0]), line[1]); //put the value of line 0 and line 1 into the integerToWordMap
			//Iterate from index 0 to last index of words
			for (int i = 0; i < words.length; i++) {
				//Create a new Queue of type Integer named wordToIntegerMapQueue with the value of words array at index i
				Queue<Integer> wordToIntegerMapQueue = wordToIntegerMap.get(words[i]); 
				//If wordToIntegerMapQueue is null, then create a new queue, and enqueue the value of variable number
				if (wordToIntegerMapQueue == null) {
					wordToIntegerMapQueue = new Queue<>();
					wordToIntegerMapQueue.enqueue(number);
					wordToIntegerMap.put(words[i], wordToIntegerMapQueue); //put index i of words, and wordToIntegerMapQueue onto wordToIntegerMap
				}
				//Else, if wordToIntegerMapQueue does not contain variable number, then enqueue the variable onto it
				else {
					if (!contains(wordToIntegerMapQueue, number)) {
						wordToIntegerMapQueue.enqueue(number);
					}
				}
			}
		} //End of while loop
		graph = new Digraph(vertices); //Set the value of graph to a new Digraph with parameter of vertices
		in = new In(hypernyms); //Set the variable in to the value of taking in hypernyms
		
		//While it is still reading in hypernyms
		while (in.hasNextLine()) {
			String[] line = in.readLine().split(","); //Create a String array named line with the value of splitting each line at comma
			//iterate from index 1 to the last index of length, and add edge onto the graph of integer at index 0 of line, and index i of line
			for (int i = 1; i < line.length; i++)
				graph.addEdge(Integer.parseInt(line[0]), Integer.parseInt(line[i]));
		}
		
		//Set the value of sap to a new SAP with parameter graph
		sap = new SAP(graph);
		//If sap is not a rooted DAG then throw an illegal argument exception
		if (!sap.isRootedDAG()) throw new IllegalArgumentException("hypernyms must be a rooted DAG");
	}

	/**
	 * Output all the nouns of the WordList
	 * @return all elements in WordList
	 */
	public Iterable<String> nouns(){
		return wordToIntegerMap.keys(); //Return the keys of wordToIntegerMap variable
	}

	/**
	 * Checks if the word is a noun
	 * @param word is the word to be checked
	 * @return true if it's a noun, and false if not
	 */
	public boolean isNoun(String word) {
		//If word is null, then throw a nullPointerException, and return if it's a noun or not
		if (word == null) throw new NullPointerException("Arguments cannot be null.");
		return wordToIntegerMap.contains(word);	//Return whether or not wordToIntegerMap contains the parameter word
	}
	
	/**
	 * Returns the distance between nounA and nounB
	 * @param nounA
	 * @param nounB
	 * @return the distance between the two passed nouns
	 */
	public int distance(String nounA, String nounB) {
		//If nounA or nounB are null, then throw a nullPointerException
		if (nounA == null || nounB == null) throw new NullPointerException("Arguments cannot be null.");
		//If nounA or nounB are not contained in wordToIntegerMap, throw an illegalArgumentException
		if (wordToIntegerMap.get(nounA) == null || wordToIntegerMap.get(nounB) == null)
			throw new IllegalArgumentException("Nouns are not found in WordNet.");
		
		//Create two new Iterable of type Integer with the value of nounA and nounB by getting it from wordToIntegerMap
		Iterable<Integer> integerA = wordToIntegerMap.get(nounA);
		Iterable<Integer> integerB = wordToIntegerMap.get(nounB);
		//Return the length between the two newly created variables of integerA and integerB
		return sap.length(integerA, integerB);
	}

	/**
	 * Returns the shortest ancestral path between nounA and nounB
	 * @param nounA
	 * @param nounB
	 * @returns a synset, which is a common ancestor of both nouns in shortest ancestral path
	 */
	public String sap(String nounA, String nounB) {
		//If nounA or nounB is null, then throw a NullPointerException
		if (nounA == null || nounB == null) throw new NullPointerException("Arguments cannot be null.");
		//If nounA or nounB are not contained in wordToIntegerMap, throw an illegalArgumentException
		if (wordToIntegerMap.get(nounA) == null || wordToIntegerMap.get(nounB) == null)
			throw new IllegalArgumentException("Nouns are not found in WordNet.");
		
		//Create two new Iterable of type Integer with the value of nounA and nounB by getting it from wordToIntegerMap
		Iterable<Integer> integerA = wordToIntegerMap.get(nounA);
		Iterable<Integer> integerB = wordToIntegerMap.get(nounB);
		//Return the ancestor that's commmon between integerA and integerB
		return integerToWordMap.get(sap.ancestor(integerA, integerB));
	}
	
	/**
	 * Create a private method named contains that will check if an iterable list contains the item
	 * @param iterable
	 * @param item
	 * @return true if it does, and false otherwise
	 */
	private <Item> boolean contains (Iterable<Item> iterable, Item item) {
		for (Item query : iterable)
			if (query == item) return true;
		return false;
	}
}
