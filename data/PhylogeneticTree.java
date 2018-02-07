package data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rtc.MarcoTree;

public class PhylogeneticTree {
	private int numVertices; 
	private List<Integer> adjListArray[]; 
	private int ancestors[];
	
	
	
	public PhylogeneticTree(int numVertices) {
		this.numVertices = numVertices; 
		adjListArray = new ArrayList[numVertices];
		ancestors = new int[numVertices]; 
		
		
		for (int i = 0; i < numVertices; i++) {
			adjListArray[i] = new ArrayList<>(); 
		}
		
	}
	
	public void addEdge(int src, int dest) {
		adjListArray[src].add(dest);
		ancestors[dest] = src; 
	}
	
	public List<Integer> getAncestors(int a) {
		ArrayList<Integer> allAncestors = new ArrayList<>(); 
		
		int current = a; 
		while (current != 0) { 
			int nextAncestor = ancestors[current]; 
			allAncestors.add(nextAncestor);
			current = nextAncestor; 			
		}
		
		return allAncestors; 
	}
	
	// lowest common ancestor of a and b 
	public int lca(int a, int b) {
		List<Integer> ancestorsA = getAncestors(a); 
		List<Integer> ancestorsB = getAncestors(b); 
		
		for (int i = 0; i < ancestorsA.size(); i++) {
			for (int j = 0; j < ancestorsB.size(); j++) {
				if (ancestorsA.get(i) == ancestorsB.get(j)) {
					return ancestorsA.get(i); 
				}
			}
		}
		return 0; 
	}
	
	public boolean isConsistent(RootedTriplet triplet) {	
		int lcaAB = lca(triplet.a, triplet.b); 
		int lcaAC = lca(triplet.a, triplet.c); 
		
		if (getAncestors(triplet.a).contains(triplet.c) || getAncestors(triplet.b).contains(triplet.c)			// c cannot be an ancestor of a or b
			|| getAncestors(triplet.a).contains(triplet.b) || getAncestors(triplet.b).contains(triplet.a)) {    // a and b cannot be ancestors of each other 
			return false; 
		}
		
		
		for (int ancestor : getAncestors(lcaAB)) {
			if (lcaAC == ancestor) 
				return true; 
		}
		
		return false; 
	}
	
	public List<Integer>[] getAdjList() {
		return adjListArray; 
	}
	
	public boolean isLeaf(int a) { 
		return adjListArray[a].size() == 0; 
	}
	
	public List<RootedTriplet> findAllTriplets() {
		List<RootedTriplet> triplets = new ArrayList<>(); 
		
		for(int i = 1; i < numVertices-1; i++) {
			if (ancestors[i] == 0) continue; 
			for (int j = i+1; j < numVertices; j++) {
				if (ancestors[j] == 0) continue; 
				for (int k = 0; k < numVertices; k++) { 
					RootedTriplet t = new RootedTriplet(i, j, k); 
					if (isConsistent(t)) {
						triplets.add(t); 
					}
				}
			}
		}
		
		return triplets; 
	}
	
	public Set<Integer> getLabels() {
		Set<Integer> labels = new HashSet<>(); 
		for (int i = 0; i < numVertices; i++) {
			labels.add(i);
		}
		
		return labels; 
	}
	
	
	
	
	
		
	public static void main(String[] args) {
		PhylogeneticTree t = new PhylogeneticTree(9);
		t.addEdge(0, 1);
		t.addEdge(0, 2);
		t.addEdge(1, 3);
		t.addEdge(1, 4);
		t.addEdge(2, 5);
		t.addEdge(2, 6);
		t.addEdge(3, 7);
		t.addEdge(3, 8);
		
		List<Integer>[] adjlist = t.getAdjList(); 
		
		for (int i = 0; i < adjlist.length; i++) {
			System.out.println(i + ": " + adjlist[i]);
		}
		
		System.out.println("lca " + t.lca(7, 8));
		System.out.println("lca 0 " + t.lca(0,0));
		
		System.out.println("ancestors " + t.getAncestors(7));
		
		System.out.println("consistent " + t.isConsistent(new RootedTriplet(7, 8, 4)));
		System.out.println("consistent " + t.isConsistent(new RootedTriplet(7, 4, 5)));
		System.out.println("consistent " + t.isConsistent(new RootedTriplet(5, 6, 8)));
		System.out.println("consistent " + t.isConsistent(new RootedTriplet(3, 4, 1)));

		RootedTriplet triplet = new RootedTriplet(8, 7, 4); 
		System.out.println(triplet.toString());
		
		
		
		
		PhylogeneticTree t2 = new PhylogeneticTree(16); 
		t2.addEdge(0, 1);
		t2.addEdge(0, 2);
		t2.addEdge(0, 9);
		t2.addEdge(1, 3);
		t2.addEdge(1, 4);
		t2.addEdge(3, 7);
		t2.addEdge(3, 8);
		t2.addEdge(8, 14);
		t2.addEdge(8, 15);
		t2.addEdge(2, 5);
		t2.addEdge(2, 6);
		t2.addEdge(9, 10);
		t2.addEdge(9, 11);
		t2.addEdge(11, 12);
		t2.addEdge(11, 13);
		
		
		System.out.println("number of triplets: " + t.findAllTriplets().size());
		System.out.println(t2.findAllTriplets().toString());
		
		
		MarcoTree m = new MarcoTree(); 
		m.rec(t2.findAllTriplets(), t2.getLabels(), 0);
		
		
	}

}
