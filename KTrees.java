import java.io.*;
import java.util.*;

import data.TripsReader;
import ilog.concert.*;
import ilog.cplex.*;

/**
 * @author leovaniersel
 */
public class KTrees {
    
    static String INFILE;
    int filecounter = 0; 

    public static void main(String[] args) {
        
        int num_trees = 0;
        int num_caterpillars = 0;
        int num_non_caterpillars = 0;
        List<int[]> triplets = new LinkedList();
        
        boolean startsatzero = false;
//        if(args.length == 1) {
        if (true) {
            // input file specified
//            String filename = "t6c60.trips";
        	String filename = "canontests/t8_c30_4.trips";
        	TripsReader tripsreader = new TripsReader(filename); 
        	try {
				tripsreader.write(tripsreader.getTriplets());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	filename = filename+"mapped";
            INFILE = filename;
            int num = 0;
            try {
                String line = null;
                BufferedReader reader = new BufferedReader(new FileReader(filename));
                while ((line = reader.readLine()) != null && line.length() > 0) {
                    String[] split = line.split(" ");
                    int[] triplet = new int[3];
                    if(split.length != 3) {
                        System.out.println("Incorrect input format!");
                        return;
                    }
                    for(int i = 0; i < 3; i++) {
                        if(split[i].equals("|")) {
                            triplet[i] = Integer.parseInt(split[i+1]);
                            if(triplet[i] > num) {
                                num = triplet[i];
                            }
                            if(triplet[i] == 0) {
                                startsatzero = true;
                            }
                            break;
                        } else {
                            triplet[i] = Integer.parseInt(split[i]);
                            if(triplet[i] > num) {
                                num = triplet[i];
                            }
                            if(triplet[i] == 0) {
                                startsatzero = true;
                            }
                        }
                    }
//                    for(int i = 0; i < 3 ; i++) {
//                        triplet[i] = Integer.parseInt(split[i]);
//                    }
                    triplets.add(triplet);
                }
                reader.close();
            } catch (FileNotFoundException e) {
                System.out.println("Error reading file: " + filename);
                return;
            } catch (IOException e) {
            }
            
            if(startsatzero) {
                num++;
            }
            
            // solve triplets in file
            if (triplets.isEmpty()) {
                System.out.println("No triplets");
                return;
            }
            System.out.println("Determining minimum number of trees...");
            num_trees = solve(num, triplets, "trees", false);
            if (num_trees == -1) {
                return;
            }
            System.out.println("Minimum number of trees is " + num_trees);
            System.out.println("Determining minimum number of caterpillars...");
            num_caterpillars = solve(num, triplets, "caterpillars", false);
            if (num_caterpillars == -1) {
                return;
            }
            System.out.println("Minimum number of caterpillars is " + num_caterpillars);
            
            System.out.println("Determining minimum number of non-caterpillar-trees...");
            num_non_caterpillars = solve(num, triplets, "non-caterpillars", false);
            if (num_non_caterpillars == -1) {
                return;
            }
            System.out.println("Minimum number of trees is " + num_trees);
            System.out.println("Minimum number of caterpillars is " + num_caterpillars);
            System.out.println("Minimum number of non-caterpillar-trees is " + num_non_caterpillars);
            
            if (num_trees != num_caterpillars & num_trees > 1) {
                System.out.println("Number of trees and caterpillars is NOT equal!");
            }
            return;
        }
        
        int N = 10; // the maximum number of leaves n
        double stepsize = 1; // probability to include a triplet is increased by stepsize in each iteration
        int repeats = 1; // number of times to repeat the experiment for same n, p
        int found = 0;
        String eol = System.getProperty("line.separator");
        try {
            BufferedWriter out;
            out = new BufferedWriter(new FileWriter("triplets_trees_neq_caterpillars.txt"));
            for (int n = 3; n <= N; n++) {
                for (double p = stepsize; p <= 1; p += stepsize) {
                    for (int it = 0; it < repeats; it++) {
                        System.out.println("-----------------------------------------------------------------");
                        System.out.println("Counter examples found so far: " + found);
                        System.out.println("-----------------------------------------------------------------");
                        // generate a set of triplets
                        System.out.println("Generating triplet set...");
                        System.out.println("Number of leaves: " + n);
                        System.out.println("Including each triplet with probability " + p);
                        triplets = new LinkedList();
                        for (int a = 0; a < n - 1; a++) {
                            for (int b = a + 1; b < n; b++) {
                                for (int c = 0; c < n; c++) {
                                    if (c == a || c == b) {
                                        continue;
                                    }
                                    if (Math.random() <= p) {
                                        int[] triplet = {a, b, c};
                                        triplets.add(triplet);
                                    }
                                }
                            }
                        }
                        if(triplets.isEmpty()) {
                            System.out.println("No triplets");
                            System.out.println("Skipping...");
                            continue;
                        }
                        System.out.println("Determining minimum number of trees...");
                        num_trees = solve(n, triplets, "trees", false);
                        if(num_trees == -1) {
                            return;
                        }
                        System.out.println("Minimum number of trees is " + num_trees);
                        System.out.println("Determining minimum number of caterpillars...");
                        num_caterpillars = solve(n, triplets, "caterpillars", false);
                        if(num_caterpillars == -1) {
                            return;
                        }
                        System.out.println("Minimum number of caterpillars is " + num_caterpillars);
                        if (num_trees != num_caterpillars & num_trees > 1) {
                            found++;
                            System.out.println("Number of trees and caterpillars is NOT equal!");
                            out.write("Number of trees: " + num_trees + eol);
                            out.write("Number of caterpillars: " + num_caterpillars + eol);
                            out.write("Number of leaves: " + n + eol);
                            out.write("Probability of including a triplet: " + p + eol);
                            out.write("Triplets:" + eol);
                            for (int[] triplet : triplets) {
                                out.write((triplet[0] + 1) + " " + (triplet[1] + 1) + " | " + (triplet[2] + 1) + eol);
                            }
                            out.write("-----------------------------------------------------------------------" + eol);
                        }
                        if (num_caterpillars > num_trees + 1) {
                            System.out.println("Difference is more than one!");
                            return;
                        }
                    }
                }
            }
            out.close();
        } catch (IOException e) {
            return;
        }
    }

    public static int solve(int n, List<int[]> triplets, String type, boolean full) {
        int k = 1;
        
        String filename = "ilp.tmp";
        
        String eol = System.getProperty("line.separator");

        boolean solved = false;
        while (!solved) {

            if(type.equals("caterpillars")) {
                System.out.println("Trying " + k + " caterpillars...");
            } else {
                System.out.println("Trying " + k + " trees...");
            }
            
            if( k > triplets.size() ) {
                System.out.println("Giving up");
                writeToFile(triplets, n, "no_caterpillar_solution.txt");
                return -1;
            }
            
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(filename));

                out.write("Minimize" + eol);

                out.write("Subject To" + eol);

                // each input triplet is assigned to at least one tree
                for (int[] triplet : triplets) {
                    String constr = "";
                    for (int i = 0; i < k; i++) {
                        constr += " + " + var(triplet[0], triplet[1], triplet[2], i);
                    }
                    constr += " >= 1";
                    out.write(constr + eol);
                }

                // each tree has for each combination of three leaves exactly one triplet
                for (int i = 0; i < k; i++) {
                    for (int a = 0; a < n - 2; a++) {
                        for (int b = a + 1; b < n - 1; b++) {
                            for (int c = b + 1; c < n; c++) {
                                out.write(var(a, b, c, i) + " + " + var(a, c, b, i) + " + " + var(b, c, a, i) + " = 1" + eol);
                            }
                        }
                    }
                }

                // ab|c => ad|c OR ab|d (for trees)
                // ab|c + cd|a <= 1 (for caterpillars)
                for (int i = 0; i < k; i++) {
                    for (int a = 0; a < n; a++) {
                        for (int b = 0; b < n; b++) {
                            if (b == a) {
                                continue;
                            }
                            for (int c = 0; c < n; c++) {
                                if (c == a || c == b) {
                                    continue;
                                }
                                for (int d = 0; d < n; d++) {
                                    if (d == a || d == b || d == c) {
                                        continue;
                                    }
                                    if (type.equals("caterpillars")) {
                                        out.write(var(a, b, c, i) + " + " + var(c, d, a, i) + " <= 1" + eol);
                                    } else {
                                        out.write(var(a, b, c, i) + " - " + var(a, d, c, i) + " - " + var(a, b, d, i) + " <= 0" + eol);
                                    }
                                }
                            }
                        }
                    }
                }
                
                if(type.equals("non-caterpillars")) {
                    // we are forcing the first tree to be not a caterpillar
                    // by forcing disjoint sets A,B with |A|>=2, |B|>=2
                    // and such that all triplets xy|z with x,y\in A z\notin A are consistent with the first tree
                    // and such that all triplets xy|z with x,y\in B z\notin B are consistent with the first tree
                    
                    // sum_x (a_x) >= 2
                    String constr = "";
                    for (int x = 0; x < n; x++) {
                        constr += " + a_" + x;
                    }
                    constr += " >= 2";
                    out.write(constr + eol);
                    
                    // sum_x (b_x) >= 2
                    constr = "";
                    for (int x = 0; x < n; x++) {
                        constr += " + b_" + x;
                    }
                    constr += " >= 2";
                    out.write(constr + eol);
                    
                    // a_x + b_x <= 1
                    for (int x = 0; x < n; x++) {
                        out.write("a_" + x + " + b_" + x + " <= 1" + eol);
                    }
                    
                    // in the first tree: a_x + a_y - a_z - x_{xy|z} <= 1
                    // and b_x + b_y - b_z - x_{xy|z} <= 1
                    for (int x = 0; x < n; x++) {
                        for (int y = x+1; y < n; y++) {
                            for (int z = 0; z < n; z++) {
                                if(z == y | z == x) {
                                    continue;
                                }
                                out.write("a_" + x + " + a_" + y + " - a_" + z + " - " + var(x, y, z, 0) + " <= 1" + eol);
                                out.write("b_" + x + " + b_" + y + " - b_" + z + " - " + var(x, y, z, 0) + " <= 1" + eol);
                            }
                        }
                    }
                }

                // if we're considering the full triplet set, we can assume a certain labelling wlog
                if (full & type.equals("caterpillars")) {
                    // the first caterpillar is forced to be labelled 0,1,2,3,4...
                    for (int a = 0; a < n - 2; a++) {
                        for (int b = a + 1; b < n - 1; b++) {
                            for (int c = b + 1; c < n; c++) {
                                out.write(var(a, b, c, 0) + " = 1" + eol);
                            }
                        }
                    }
                } else if (full) {
                    // the first tree is forced to have a cherry on 0 and 1
                    for (int c = 2; c < n; c++) {
                        out.write(var(0, 1, c, 0) + " = 1" + eol);
                    }
                    if (type.equals("trees")) {
                        // the first tree is forced to have no triplets of the form ac|b with a<b<c
                        for (int a = 0; a < n - 2; a++) {
                            for (int b = a + 1; b < n - 1; b++) {
                                for (int c = b + 1; c < n; c++) {
                                    out.write(var(a, c, b, 0) + " = 0" + eol);
                                }
                            }
                        }
                    }
                    if (type.equals("non-caterpillars")) {
                        // the first tree is forced to have a cherry on 2 and 3
                        out.write(var(2, 3, 0, 0) + " = 1" + eol);
                        out.write(var(2, 3, 1, 0) + " = 1" + eol);
                        for (int c = 4; c < n; c++) {
                            out.write(var(2, 3, c, 0) + " = 1" + eol);
                        }
                    }
                }

                out.write("Binary" + eol);

                for (int a = 0; a < n - 1; a++) {
                    for (int b = a + 1; b < n; b++) {
                        for (int c = 0; c < n; c++) {
                            if (c == a || c == b) {
                                continue;
                            }
                            for (int i = 0; i < k; i++) {
                                out.write(var(a, b, c, i) + eol);
                            }
                        }
                    }
                }
                if(type.equals("non-caterpillars")) {
                    for(int x = 0; x < n; x++) {
                        out.write("a_" + x + eol);
                        out.write("b_" + x + eol);
                    }
                }

                out.write("End" + eol);

                out.close();
            } catch (IOException e) {
                return -1;
            }

            // ----- run CPLEX -----
            try {

                IloCplex cplex = new IloCplex();

                // set the maximum number of threads to use
                cplex.setParam(IloCplex.IntParam.Threads, 4);

                // set the time limit
                // cplex.setParam(IloCplex.DoubleParam.TiLim, 120);

                //! filename is the name of the file where your ILP is
                cplex.importModel(filename);

                // cplex.readMIPStart("mipstart.tmp");

                //! uncomment this to suppress visual output from cplex
                cplex.setOut(null);

                //! this is the solving bit
                solved = cplex.solve();

                if (solved) {

                    int[][][][] out_triplets = new int[n][n][n][k];

                    IloNumVar[] var = parse(cplex);
                    double[] x = cplex.getValues(var);
                    for (int loop = 0; loop < x.length; loop++) {
                        String varname = var[loop].getName();
                        if (x[loop] < 0.5) {
                            // this variable is set to 0. Skip!
                            continue;
                        }
                        String[] splitVarName = varname.split("_");
                        if (!splitVarName[0].equals("x")) {
                            continue;
                        }
                        String[] indices = splitVarName[1].split(",");
                        int a = Integer.parseInt(indices[0]);
                        int b = Integer.parseInt(indices[1]);
                        int c = Integer.parseInt(indices[2]);
                        int i = Integer.parseInt(indices[3]);
                        out_triplets[a][b][c][i] = 1;
                    }

                    //  write to files
                    for (int i = 0; i < k; i++) {
                        try {
                            BufferedWriter out;
                            if (type.equals("caterpillars")) {
                                out = new BufferedWriter(new FileWriter(INFILE + "-caterpillar-" + i + ".txt"));
                            } else if (type.equals("trees")){
                                out = new BufferedWriter(new FileWriter(INFILE + "-trees-" + i + ".txt"));
                            } else { // if (type.equals("non-caterpillars")){
                                out = new BufferedWriter(new FileWriter(INFILE + "-non-caterpillars-" + i + ".txt"));
                            } 
                            for (int a = 0; a < n - 1; a++) {
                                for (int b = a + 1; b < n; b++) {
                                    for (int c = 0; c < n; c++) {
                                        if (c == a || c == b) {
                                            continue;
                                        }
                                        if (out_triplets[a][b][c][i] == 1) {
                                            out.write((a+1) + " " + (b+1) + " " + (c+1) + eol);
                                        }
                                    }
                                }
                            }
                            out.close();
                        } catch (IOException e) {
                            return -1;
                        }
                    }
                }

                System.out.println("CPLEX: " + cplex.getStatus());

                //! this gets the objective function value, rounded to an int
                double score = cplex.getObjValue();

                // save solution for future reference
                // cplex.writeMIPStart("mipstart.tmp");

                //! this deallocates the CPLEX resources
                cplex.end();

            } catch (IloException e) {
            }
            if (!solved) {
                k++;
                filename = filename + k;
            }
        }

//        if (caterpillar) {
//            System.out.println("Minimum number of caterpillars for " + n + " leaves is " + k);
//        } else {
//            System.out.println("Minimum number of trees for " + n + " leaves is " + k);
//        }
        
        return k;
        
//        // write to file
//        try {
//            BufferedWriter out = new BufferedWriter(new FileWriter("results.txt"));
//            for (int j = 3; j <= n; j++) {
//                out.write(trees[j] + " ");
//            }
//            out.close();
//        } catch (IOException e) {
//            return;
//        }
    }

    public static String var(int a, int b, int c, int i) {
        if (a < b) {
            return "x_" + a + "," + b + "," + c + "," + i;
        }
        return "x_" + b + "," + a + "," + c + "," + i;
    }

    private static IloNumVar[] parse(IloCplex cplex) throws IloException {
        HashSet<IloNumVar> vars = new HashSet<IloNumVar>();
        Iterator it = cplex.iterator();
        IloLinearNumExpr expr;
        IloLinearNumExprIterator it2;
        while (it.hasNext()) {
            IloAddable thing = (IloAddable) it.next();
            if (thing instanceof IloRange) {
                expr = (IloLinearNumExpr) ((IloRange) thing).getExpr();
                it2 = expr.linearIterator();
                while (it2.hasNext()) {
                    vars.add(it2.nextNumVar());
                }
            } else if (thing instanceof IloObjective) {
                expr = (IloLinearNumExpr) ((IloObjective) thing).getExpr();
                it2 = expr.linearIterator();
                while (it2.hasNext()) {
                    vars.add(it2.nextNumVar());
                }
            } else if (thing instanceof IloSOS1) {
                vars.addAll(Arrays.asList(((IloSOS1) thing).getNumVars()));
            } else if (thing instanceof IloSOS2) {
                vars.addAll(Arrays.asList(((IloSOS2) thing).getNumVars()));
            } else if (thing instanceof IloLPMatrix) {
                vars.addAll(Arrays.asList(((IloLPMatrix) thing).getNumVars()));
            }
        }
        IloNumVar[] varray = vars.toArray(new IloNumVar[1]);
        return varray;
    }
    
    public static void writeToFile(List<int[]> triplets, int n, String filename) {
        String eol = System.getProperty("line.separator");
        try {
            BufferedWriter out;
            out = new BufferedWriter(new FileWriter(filename));
            for (int[] triplet : triplets) {
                out.write((triplet[0] + 1) + " " + (triplet[1] + 1) + " " + (triplet[2] + 1) + eol);
            }
            out.close();
        } catch (IOException e) {
            return;
        }
    }
}
