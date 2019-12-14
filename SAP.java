package a06;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.DepthFirstDirectedPaths;
import edu.princeton.cs.algs4.DepthFirstOrder;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Provides a data type SAP 
 * 
 * @author Hai Le and Deokhee Kang
 * 
 * 11/25/19
 */

public final class SAP {
	private final Digraph G; //Create a private final variable of type Digraph called G
	private final boolean isDAG; //Create a private final boolean named isDAG
	private boolean isRootedDAG = false; //Create a private boolean named isRootedDAG with the value of false
	
	/**
	 * Constructs a Sap taking a digraph (not necessarily a DAG)
	 * @param G which is a digraph
	 */
	public SAP(Digraph G) {
		//If parameter passed is null, then throw a NullPointerException
		if (G == null) throw new NullPointerException("Digraph G cannot be null.");
		
		int root = -1;
		this.G = new Digraph(G); //Set this object's passed parameter to the value of a new digraph with G as parameter
		this.isDAG = !(new DirectedCycle(G)).hasCycle(); //Set the value of this object's isDAG to not new DirectedCycle(G)).hasCycle()
		DepthFirstOrder dfo = new DepthFirstOrder(this.G); //Set the value of dfo to a new DepthFirstOrder with the parameter of this object's G
		
		//If this is a DAG
		if (this.isDAG) {
			root = dfo.post().iterator().next();
			//Set dfp's value to a new DepthFirstDirectedPaths with parameters G.reverse and root
			DepthFirstDirectedPaths dfp = new DepthFirstDirectedPaths(G.reverse(), root); //Set the root of this object to dfo.post().iterator().next()
			isRootedDAG = true; //Set the value of this objecct's isRootedDAG to true
			//Iterate through all the elements of G, and if dfp has a path to the current element i, then set this object's isRootedDAG to false
			for(int i = 0; i < G.V(); i++){
				if(!dfp.hasPathTo(i)) isRootedDAG = false;
			}
		}
	}

	/**
	 * Returns true if this digraph a directed acyclic graph
	 * 
	 * @return {@code true} if this digraph is acyclic; {@code false} otherwise
	 */
	public boolean isDAG() {
        
		return isDAG;
	}

	/**
	 * Returns true if this digraph a rooted DAG
	 * 
	 * @return {@code true} if this digraph is a rooted DAG; {@code false} otherwise
	 */
	public boolean isRootedDAG() {
				
		return isRootedDAG;
	}

	/**
	 * Returns the length of shortest ancestral path between v and w; -1 if no such path
	 * 
	 * @param v
	 * @param w
	 * @return length or -1 if no such path
	 */
	public int length(int v, int w) {
		//Validate both vertex v and w, which are the two passed parameters
		validateVertex(v);
		validateVertex(w);
		
		//Create two new Queues of type Integer, and set the value to a new queue
		Queue<Integer> vQueue = new Queue<>();
		Queue<Integer> wQueue = new Queue<>();
		
		vQueue.enqueue(v);
		wQueue.enqueue(w);
		
		//Return the ancestor of vQueue and wQueue
		return length(vQueue, wQueue);
	}
	
	/**
	 * Returns a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
	 * 
	 * @param v
	 * @param w
	 * @return ancestor or -1 if no such path
	 */
	public int ancestor(int v, int w) {
		validateVertex(v);
		validateVertex(w);
		
		Queue<Integer> vQueue = new Queue<>();
		Queue<Integer> wQueue = new Queue<>();
				
		vQueue.enqueue(v);
		wQueue.enqueue(w);
		
		return ancestor(vQueue, wQueue);
	}
	
	/**
	 * Returns the length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
	 * 
	 * @param v
	 * @param w
	 * @return length or -1 if no such path
	 */
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		//Validate both vertex v and w, which are the two passed parameters
		validateVertices(v);
		validateVertices(w);
		
		int length = -1; //Create a new int named length with the value of -1
		int ancestor = ancestor(v, w); //Create a new int named ancestor with the value of the ancestor of v and w
		
		if (ancestor < 0) return length; //If ancestor's value is less than 0, then return the length
		
		//Create a new BreadthFirstDirectedPaths with parameters G and v named bfs
		BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(G, v);
		length = bfs.distTo(ancestor); //Set the value of length to the distance of bfs to ancestor
		bfs = new BreadthFirstDirectedPaths(G, w); //Set the value of bfs to a new BreathFirstDirectedPaths with paramters G and w
		length += bfs.distTo(ancestor); //Set length to length + the distance of bfs to ancestor
		
		//Return the length
		return length;
	}

	/**
	 * Returns a common ancestor that participates in shortest ancestral path; -1 if no such path
	 * 
	 * @param v
	 * @param w
	 * @return ancestor or -1 if no such path
	 */
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		//Validate both vertex v and w, which are the two passed parameters
		validateVertices(v);
		validateVertices(w);
		
		int ancestor = -1;
		int length = -1;

		//Create a new BreadthFirstDirectedPaths with parameters G and v named vBfs
		BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(G, v);
		//Create a new BreadthFirstDirectedPaths with parameters G and v named wBfs
		BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(G, w);
		DepthFirstOrder dfo = new DepthFirstOrder(this.G);
		//Create two new stacks of type integer named vPath and wPath
		Stack<Integer> vPath = new Stack<>();
		Stack<Integer> wPath = new Stack<>();
		
		for (int i:dfo.reversePost()) {
			if (vBfs.hasPathTo(i) && wBfs.hasPathTo(i)) {
				// if there is a shorter pass, then replace
				if (ancestor == -1 || vBfs.distTo(i) + wBfs.distTo(i) < length) { 
					ancestor = i;
					length = vBfs.distTo(i) + wBfs.distTo(i);

					for (int x:vBfs.pathTo(i)) {
						vPath.push(x);
					}

					for (int x:wBfs.pathTo(i)) {
						wPath.push(x);
					}
				}
			}
		}

		//Set the value of integer size to the minimum of parameters size of vPath and size of wPath
		int size = Integer.min(vPath.size(), wPath.size());
		
		//Iterate through every element up to the size
		for (int i = 0; i < size; i++) {
			if (vPath.peek().equals(wPath.peek())) {
				vPath.pop();
				ancestor = wPath.pop();
			}
			else break; //Else, break away from the loop
		}
		
		return ancestor;
	}
    
	/**
	 * Validate the vertex passed
	 * throw an IllegalArgumentException unless {@code 0 <= v < V}
	 * @param v is the vertex being validated
	 */
    private void validateVertex(int v) {
        if (v < 0 || v >= G.V())
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (G.V()-1));
    }

    /**
     * Validate the vertices passed
     * throw an IllegalArgumentException unless {@code 0 <= v < V}
     * @param vertices which is the Iterable Integer passed
     */
    private void validateVertices(Iterable<Integer> vertices) {
        if (vertices == null) {
            throw new IllegalArgumentException("argument is null");
        }
        int V = G.V();
        for (int v : vertices) {
            if (v < 0 || v >= G.V()) {
                throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
            }
        }
    }
    
    /**
     * Main method utilized for testing this class
     * @param args
     */
	public static void main(String[] args) {
//		in = new In(args[0]);
//		String fileName = "digraph-wordnet.txt";
		String fileName = "digraph2.txt";
		In in = new In("src\\a06\\resources\\" + fileName);
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);
		while (!StdIn.isEmpty()) {
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length   = sap.length(v, w);
			int ancestor = sap.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		}
	}
}
