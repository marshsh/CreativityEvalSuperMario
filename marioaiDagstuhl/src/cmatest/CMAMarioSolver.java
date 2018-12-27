package cmatest;

import static basicMap.Settings.DEBUG_MSG;

import java.io.IOException;
import java.util.Arrays;

import basicMap.Settings;
import fr.inria.optimization.cmaes.CMAEvolutionStrategy;
import fr.inria.optimization.cmaes.fitness.IObjectiveFunction;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.*; 
import java.util.*;

public class CMAMarioSolver {
	// Sebastian's Wasserstein GAN expects latent vectors of length 32
	public static final int Z_SIZE = 32; // length of latent space vector
	public static final int EVALS = 1000;

	
    public static void main(String[] args) throws IOException {
        Settings.setPythonProgram();
        int loops = 100;
        
        System.out.println(" \n \n \n Hemos iniciado corriendo la clase CMMarioSolver  \n \n \n");
        
        double[][] bestX = new double[loops][32];
        double[] bestY = new double[loops];
        
        MarioEvalFunction marioEvalFunction = new MarioEvalFunction();

        for(int i=0; i<loops; i++){
            System.out.println("Iteration:"+ i);

            CMAMarioSolver solver = new CMAMarioSolver(marioEvalFunction, Z_SIZE, EVALS);

            FileWriter write = new FileWriter("Evolved" + File.separator + "Timelines" + File.separator +  "timeline"+i+".txt", true);
            PrintWriter print_line = new PrintWriter(write);
            
            Object[] solutionObj = solver.run(print_line);
            print_line.close();
            double[] solutionBest = (double[]) solutionObj[0]; 
            double[] solutionMean = (double[]) solutionObj[1]; 
            
            System.out.println("Best solution writen to file ");

            bestX[i] = MarioEvalFunction.mapArrayToOne(solutionBest);
            bestY[i] = solver.fitFun.valueOf(MarioEvalFunction.mapArrayToOne(solutionBest));            

            
            
            FileWriter writeBest = new FileWriter("Evolved" + File.separator + "xBestEvolved.txt", true);
            PrintWriter pl_best = new PrintWriter(writeBest);

            FileWriter writeMean = new FileWriter("Evolved" + File.separator + "xMeanEvolved.txt", true);
            PrintWriter pl_mean = new PrintWriter(writeMean);

            
            pl_best.println(Arrays.toString(MarioEvalFunction.mapArrayToOne(solutionBest)));
            pl_best.close();
            
            pl_mean.println(Arrays.toString(MarioEvalFunction.mapArrayToOne(solutionMean)));
            pl_mean.close();
            
        }
        
        marioEvalFunction.exit();
        System.out.println("Done");
        FileWriter write = new FileWriter("ex_output.txt", true);
            try (PrintWriter print_line = new PrintWriter(write)) {
                for(int i=0;i<loops; i++){
                    print_line.println(Arrays.toString(bestX[i]));
                    System.out.println(Arrays.toString(bestX[i]));
                }
                print_line.println(Arrays.toString(bestY));
            }
        System.out.println(Arrays.toString(bestY));
        System.exit(0);
    }

    
    
    
    
    
    
    
    
    IObjectiveFunction fitFun;
    int nDim;
    CMAEvolutionStrategy cma;

    public CMAMarioSolver(IObjectiveFunction fitFun, int nDim, int maxEvals) {
        this.fitFun = fitFun;
        this.nDim = nDim;
        cma = new CMAEvolutionStrategy();
        cma.readProperties(); // read options, see file CMAEvolutionStrategy.properties
        cma.setDimension(nDim); // overwrite some loaded properties
        cma.setInitialX(-1,1); // set initial seach point xmean coordinate-wise uniform between l and u, dimension needs to have been set before
        cma.setInitialStandardDeviation(1/Math.sqrt(nDim)); // also a mandatory setting
        cma.options.stopFitness = -1e6; // 1e-14;       // optional setting
        // cma.options.stopMaxIter = 100;
        cma.options.stopMaxFunEvals = maxEvals;
        System.out.println("Diagonal: " + cma.options.diagonalCovarianceMatrix);

    }

    public void setDim(int n) {
        cma.setDimension(n);
    }

    /*public void setInitialX(double x) {
        cma.setInitialX(x);
    }*/

    public void setObjective(IObjectiveFunction fitFun) {
        this.fitFun = fitFun;
    }

    public void setMaxEvals(int n) {
        cma.options.stopMaxFunEvals = n;
    }

    public Object[] run(PrintWriter print_line) {

        // new a CMA-ES and set some initial values

        // initialize cma and get fitness array to fill in later
        double[] fitness = cma.init();  // new double[cma.parameters.getPopulationSize()];

        // initial output to files
        cma.writeToDefaultFilesHeaders(0); // 0 == overwrites old files

        // iteration loop
        while (cma.stopConditions.getNumber() == 0) {

            // --- core iteration step ---
            double[][] pop = cma.samplePopulation(); // get a new population of solutions
            for (int i = 0; i < pop.length; ++i) {    // for each candidate solution i
                // a simple way to handle constraints that define a convex feasible domain
                // (like box constraints, i.e. variable boundaries) via "blind re-sampling"
                // assumes that the feasible domain is convex, the optimum is
                while (!fitFun.isFeasible(pop[i])) {    //   not located on (or very close to) the domain boundary,
                    System.out.println(DEBUG_MSG + "Not in feasible domain. Will resample once.");
                    pop[i] = cma.resampleSingle(i);    //   initialX is feasible and initialStandardDeviations are
                    //   sufficiently small to prevent quasi-infinite looping here
                    // compute fitness/objective value
                }
                fitness[i] = fitFun.valueOf(pop[i]); // fitfun.valueOf() is to be minimized
                System.out.println(fitness[i]);
                print_line.println(Arrays.toString(pop[i])+ " : " + fitness[i]);
            }
            cma.updateDistribution(fitness);         // pass fitness array to update search distribution
            // --- end core iteration step ---

            // output to files and console
            cma.writeToDefaultFiles();
            int outmod = 150;
            if (cma.getCountIter() % (15 * outmod) == 1) {
                // cma.printlnAnnotation(); // might write file as well
            }
            if (cma.getCountIter() % outmod == 1) {
                // cma.println();
            }
        }
        // evaluate mean value as it is the best estimator for the optimum
        // cma.setFitnessOfMeanX(fitFun.valueOf(cma.getMeanX())); // updates the best ever solution

        // final output
        cma.writeToDefaultFiles(1);
        cma.println();
        cma.println("Terminated due to");
        for (String s : cma.stopConditions.getMessages())
            cma.println("  " + s);
        cma.println("best function value " + cma.getBestFunctionValue()
                + " at evaluation " + cma.getBestEvaluationNumber());

        // System.out.println("Best solution is: " + Arrays.toString(cma.getBestX()));
        // we might return cma.getBestSolution() or cma.getBestX()
        // return cma.getBestX();
        cma.setFitnessOfMeanX(fitFun.valueOf(cma.getMeanX())); // updates the best ever solution

        double[] best = cma.getBestX();
        double[] mean = cma.getMeanX();
        
        return new Object[] {best, mean};
        //return cma.getBestRecentX();

    }



}

