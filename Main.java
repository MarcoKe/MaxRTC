import java.util.List;

import data.PhylogeneticTree;
import data.RandomTreeGenerator;
import data.RootedTriplet;
import ilog.concert.*;
import ilog.cplex.*;

public class Main {
	public static void main(String[] args) {
		RandomTreeGenerator gen = new RandomTreeGenerator();
		PhylogeneticTree tree = gen.generateTree(10); 
		
		List<RootedTriplet> triplets = tree.findAllTriplets(); 
		triplets.remove(triplets.size()-1);
		tree.isDense(triplets);
	}
}
