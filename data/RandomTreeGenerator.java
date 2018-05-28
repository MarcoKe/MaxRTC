package data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomTreeGenerator {
	
	public PhylogeneticTree generateCaterpillar(int numTaxa) {
		Random random = new Random();
		PhylogeneticTree tree = new PhylogeneticTree();
		
		int label = 0; 
		
		List<Integer> leaves = new ArrayList<>();
		leaves.add(label); 
		int currentNode = 0; 
		label++; 
		
		while(leaves.size() < numTaxa) {
			tree.addEdge(leaves.get(currentNode), label);
			tree.addEdge(leaves.get(currentNode), label+1);
			leaves.remove(currentNode);
			leaves.add(label); 
			leaves.add(label+1); 
			if (random.nextDouble() <= 0.5) {
				currentNode = leaves.size()-1; 
			}
			else {
				currentNode = leaves.size()-2; 
			}
			
			label += 2; 
		}
		
		return tree; 
	}
	
	public PhylogeneticTree generateTreeOld(int numTaxa) {
		Random random = new Random();
		PhylogeneticTree tree = new PhylogeneticTree();
		
		int label = 0; 
		
		Set<Integer> leaves = new HashSet<>();
		leaves.add(label); 
		label++; 
		
		while (leaves.size() < numTaxa) {
			for (int leaf : new ArrayList<>(leaves)) {
				if (leaves.size() >= numTaxa) break;
				if (random.nextFloat() <= 1.0/leaves.size()) {
					tree.addEdge(leaf, label);
					tree.addEdge(leaf, label+1);
					leaves.add(label); 
					leaves.add(label+1);
					label += 2; 
					leaves.remove(leaf); 
				}
			}
		}
		
		return tree; 
	}
	
	public PhylogeneticTree generateTree(int numTaxa) {
		Random random = new Random();
		PhylogeneticTree tree = new PhylogeneticTree();
		
		int label = 0; 
		
		List<Integer> leaves = new ArrayList<>();
		leaves.add(label); 
		label++; 
		
		while(leaves.size() < numTaxa) {
			int index = random.nextInt(leaves.size()); 
			tree.addEdge(leaves.get(index), label);
			tree.addEdge(leaves.get(index), label+1);
			leaves.remove(index);
			leaves.add(label); 
			leaves.add(label+1); 
			label += 2; 

		}
		
		return tree; 
	}
	
	public List<RootedTriplet> getRandomTreeTriplets(int numTaxa) {
		return generateTree(numTaxa).findAllTriplets(); 
	}
	
	public List<RootedTriplet> getNonDenseTreeTriplets(int numTaxa) { 
		List<RootedTriplet> triplets = getRandomTreeTriplets(numTaxa);
		double removalRate = 0.1; 
		
		List<RootedTriplet> nonDenseTriplets = new ArrayList<>(); 
		Random random = new Random(); 
		for (RootedTriplet t : triplets) {
			if (random.nextDouble() > removalRate) {
				nonDenseTriplets.add(t); 
			}
		}
		
		return nonDenseTriplets; 
	}
	
	public static void main(String[] args) {
		RandomTreeGenerator gen = new RandomTreeGenerator(); 
		PhylogeneticTree tree = gen.generateTree(7);
		System.out.println(tree.getLeaves().size());
		System.out.println(tree.toString());
//		System.out.println(tree.toString());
	}

}
