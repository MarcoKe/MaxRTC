package rtc;

import java.util.ArrayList;
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

public class MarcoTree {
	
	public PhylogeneticTree constructTree(List<RootedTriplet> triplets) {
		int numVertices = 9; // problematic: wouldnt know the # vertices in advance 
		
		Set<Integer> left = new HashSet<>(); 
		Set<Integer> right = new HashSet<>(); 
		
		for (RootedTriplet triplet : triplets) {
			left.add(triplet.a);
			left.add(triplet.b); 
			right.add(triplet.c); 
			
		}
		
		right.removeAll(left); 
		
		PhylogeneticTree tree = new PhylogeneticTree(numVertices);  
		for (int rootAdjecent : right) {
			tree.addEdge(0, rootAdjecent);
		}
		
		
		

		
		return null; 
	}
	
	public void rec(List<RootedTriplet> triplets, Set<Integer> labels, int lvl) {
		Set<Integer> rootAdjacent = findRootAdjacent(triplets); 
		
		if (labels.size() <= 2) {
			System.out.println("succesful termination");
			return; 
		}
		
		if (rootAdjacent.size() < 2) {
			rootAdjacent.addAll(finishup(triplets, labels));
			System.out.println("lvl " + lvl + ", root adjacent: " + rootAdjacent.toString() + ", triplets: " + triplets.toString());

		}
		
		Map<Integer, Set<Integer>> subtrees = findSubtrees(triplets, rootAdjacent); 
		
		for (int key : subtrees.keySet()) {
			System.out.println("lvl " + lvl + ", subtree of " + key + ": " + subtrees.get(key).toString());
			rec(findTripletsInSubtree(subtrees.get(key), triplets), subtrees.get(key), lvl+1);
		}
		
		
	}
	
	public Set<Integer> finishup(List<RootedTriplet> triplets, Set<Integer> labels) {
		
		Set<Integer> common = new HashSet<>(); 
		for (int label : labels) {
			for (RootedTriplet t : triplets) {
				if (t.a == label || t.b == label || t.c == label) {
					common.add(label); 
				}
			}
		}
		
		Set<Integer> notFound = new HashSet<>(labels); 
		notFound.removeAll(common);
		return notFound;
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
		Map<Integer, List<RootedTriplet>> subtrees = new HashMap<>(); 
		
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
		
		// find triplets in subtree 
		
		
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


}
