package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rtc.RecursiveTreeBuilder;

public class PhylogeneticTree {
	private Map<Integer, List<Integer>> adjListArray; 
	private Map<Integer, Integer> ancestors;
	private int root; 
	
	
	
	public PhylogeneticTree(Set<Integer> labels, int root) {
		adjListArray = new HashMap<>();
		ancestors = new HashMap<>(); 
		
		
		for (int label : labels) {
			adjListArray.put(label, new ArrayList<>()); 
		}
		
		adjListArray.put(root, new ArrayList<>());
		
		this.root = root; 
		
	}
	
	public PhylogeneticTree(Set<Integer> labels) {
		this(labels, 0); 
	}
	
	public PhylogeneticTree(PhylogeneticTree tree) {
		this.adjListArray = new HashMap<Integer, List<Integer>>(tree.getAdjList());
		this.ancestors = new HashMap<Integer, Integer>(tree.getAncestors());
		this.root = tree.getRoot(); 
	}
	
	public void addEdge(int src, int dest) {
		if (adjListArray.get(src) == null) {
			adjListArray.put(src, new ArrayList<>());
		}
		
		if (adjListArray.get(dest) == null) {
			adjListArray.put(dest, new ArrayList<>());
		}
		
		adjListArray.get(src).add(dest);
		ancestors.put(dest, src); 
	}
	
	public List<Integer> getAncestors(int a) {
		ArrayList<Integer> allAncestors = new ArrayList<>(); 
		
		int current = a; 
		while (current != root) { 
			if (ancestors.get(current) == null) break;
			int nextAncestor = ancestors.get(current); 
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
		return root; 
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
	
	public Map<Integer, List<Integer>> getAdjList() {
		return adjListArray; 
	}
	
	public boolean isLeaf(int a) { 
		return adjListArray.get(a).size() == 0; 
	}
	
	public List<RootedTriplet> findAllTriplets() {
		List<RootedTriplet> triplets = new ArrayList<>(); 
		
		for(int i : adjListArray.keySet()) {
			if (ancestors.get(i) == null || ancestors.get(i) == root) continue; 
			for (int j : adjListArray.keySet()) {
				if (ancestors.get(j) == null || ancestors.get(j) == root) continue; 
				for (int k : adjListArray.keySet()) { 
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
		return adjListArray.keySet(); 
	}
	
	public Map<Integer, Integer> getAncestors() {
		return ancestors; 
	}
	
	public PhylogeneticTree merge(PhylogeneticTree tree, int src) {
		PhylogeneticTree newTree = new PhylogeneticTree(this); 
		
		System.out.println("Merging trees " + tree.toString() + "\n and " + this.toString() +"\n to node " + src);
		
		for (int key : tree.getAdjList().keySet()) {
			for (int con : tree.getAdjList().get(key)) {
				if (key != tree.getRoot())
					newTree.addEdge(key, con);
				else 
					newTree.addEdge(src, con);
			}
		}
		
		newTree.addEdge(this.root, src);
		
		System.out.println("merge complete: " + newTree.toString() + " root: " + this.getRoot());
	
		return newTree; 	
	}
	
	public String toString() {
		String s = ""; 
		for (int key : this.getAdjList().keySet()) {
			s += key + ": " + this.getAdjList().get(key) + ", ";
		}
		
		return s;
	}
	
	public int getRoot() {
		return root; 
	}
	
	
	
	
		
	public static void main(String[] args) {
		Set<Integer> labels = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
		
		PhylogeneticTree t = new PhylogeneticTree(labels);
		t.addEdge(0, 1);
		t.addEdge(0, 2);
		t.addEdge(1, 3);
		t.addEdge(1, 4);
		t.addEdge(2, 5);
		t.addEdge(2, 6);
		t.addEdge(3, 7);
		t.addEdge(3, 8);
		
		Map<Integer, List<Integer>> adjlist = t.getAdjList(); 
		
		for (int key : adjlist.keySet()) {
			System.out.println(key + ": " + adjlist.get(key));
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
		
		
		
		Set<Integer> labels2 = new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15));

		PhylogeneticTree t2 = new PhylogeneticTree(labels2); 
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
		
		
		RecursiveTreeBuilder m = new RecursiveTreeBuilder(); 
		m.rec(t2.findAllTriplets(), t2.getLabels(), 0);
		
		PhylogeneticTree t3 = new PhylogeneticTree(new HashSet<>(Arrays.asList(0, 14, 15)));
		t3.addEdge(0, 14);
		t3.addEdge(0, 15);
		
		PhylogeneticTree t4 = t.merge(t3, 8);
		
		for (int key : t4.getAdjList().keySet()) {
			System.out.println(key + ": " + t4.getAdjList().get(key));
		}
		
//		PhylogeneticTree = 
	}
	
	

}
