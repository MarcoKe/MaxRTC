package rtc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.PhylogeneticTree;
import data.RootedTriplet;
import maxrtc.ILPSolver;

public class RecursiveTreeBuilder {	
	
	public PhylogeneticTree rec(List<RootedTriplet> triplets, Set<Integer> labels, int root) {
		Set<Integer> rootAdjacent = findRootAdjacent(triplets); 
		
		if (labels.size() <= 2) {
			PhylogeneticTree tree = new PhylogeneticTree(labels, root);
			for (int l : labels) {
				tree.addEdge(root, l);
			}
			
			return tree;
		}
		
		if (rootAdjacent.size() < 2) {
			rootAdjacent.addAll(findRootAdjacent(triplets, labels));
		}
		
		Map<Integer, Set<Integer>> subtrees = findSubtrees(triplets, rootAdjacent); 
		
		PhylogeneticTree tree = new PhylogeneticTree(new HashSet<>(Arrays.asList(root)), root);
		for (int key : subtrees.keySet()) {
			tree = tree.merge(rec(findTripletsInSubtree(subtrees.get(key), triplets), subtrees.get(key), key), key);
		}
		
		
		return tree; 
		
		
	}
	
	public Set<Integer> findRootAdjacent(List<RootedTriplet> triplets, Set<Integer> labels) {
		
		Set<Integer> common = new HashSet<>(); 
		for (int label : labels) {
			for (RootedTriplet t : triplets) {
				if (t.a == label || t.b == label || t.c == label) {
					common.add(label); 
				}
			}
		}
		
		Set<Integer> adjacent = new HashSet<>(labels); 
		adjacent.removeAll(common);
		return adjacent;
	}
	
	public Set<Integer> findRootAdjacent(List<RootedTriplet> triplets) {
		Set<Integer> left = new HashSet<>(); 
		Set<Integer> right = new HashSet<>(); 
		
		for (RootedTriplet triplet : triplets) {
			left.add(triplet.a);
			left.add(triplet.b); 
			right.add(triplet.c); 
			
		}
		
		right.removeAll(left); 
		
		return right;		
	}
	
	public List<RootedTriplet> findTripletsInSubtree(Set<Integer> subtree, List<RootedTriplet> triplets) {
		
		List<RootedTriplet> tripletsInSubtree = new ArrayList<>(); 
		
		for (RootedTriplet triplet : triplets) {
			if (subtree.contains(triplet.a) && subtree.contains(triplet.b) && subtree.contains(triplet.c)) {
				tripletsInSubtree.add(triplet); 
			}
		}
			
		return tripletsInSubtree;
		
	}
	
	public Map<Integer, Set<Integer>> findSubtrees(List<RootedTriplet> triplets, Set<Integer> rootAdjacent) {		
		// create list of which vertices cannot be in certain subtrees 
		Map<Integer, Set<Integer>> notInSubtree = new HashMap<>(); 		
		for (int subtreeRoot : rootAdjacent) {
			Set<Integer> notInSubtreePart = new HashSet<>(); 
			for (RootedTriplet triplet : triplets) {			
				if (triplet.c == subtreeRoot) {
					notInSubtreePart.add(triplet.a);
					notInSubtreePart.add(triplet.b); 
				}
			}
			notInSubtree.put(subtreeRoot, notInSubtreePart);
		}
		
		// create list of which vertices must be in certain subtrees 
		Map<Integer, Set<Integer>> inSubtree = new HashMap<>(); 
		
		for (int subtreeRoot : rootAdjacent) {
			Set<Integer> keys = new HashSet<>(notInSubtree.keySet()); 
			keys.remove(subtreeRoot); 
			
			List<Set<Integer>> remaining = new ArrayList<>(); 
			for (int key : keys) {
				remaining.add(notInSubtree.get(key));
			}
			
			Set<Integer> intersection = getCommonElements(remaining); 
			inSubtree.put(subtreeRoot, intersection);
			
		}		
		
		return inSubtree; 
	}
	
	public <T> Set<T> getCommonElements(Collection<? extends Collection<T>> collections) {

	    Set<T> common = new LinkedHashSet<T>();
	    if (!collections.isEmpty()) {
	       Iterator<? extends Collection<T>> iterator = collections.iterator();
	       common.addAll(iterator.next());
	       while (iterator.hasNext()) {
	          common.retainAll(iterator.next());
	       }
	    }
	    return common;
	}

	public static void main(String[] args) {
		PhylogeneticTree t = new PhylogeneticTree(new HashSet<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8)));
		t.addEdge(0, 1);
		t.addEdge(0, 2);
		t.addEdge(1, 3);
		t.addEdge(1, 4);
		t.addEdge(2, 5);
		t.addEdge(2, 6);
		t.addEdge(3, 7);
		t.addEdge(3, 8);
		
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
		
		RecursiveTreeBuilder solver = new RecursiveTreeBuilder();
		List<RootedTriplet> trippy = t2.findAllTriplets(); 
//		trippy.add(new RootedTriplet(9, 10, 13));
		PhylogeneticTree reconstructedTree = solver.rec(trippy, t2.getLabels(), 0);
		
		for (int key : reconstructedTree.getAdjList().keySet()) {
			System.out.println(key + ": " + reconstructedTree.getAdjList().get(key));
		}
	
	}

}
