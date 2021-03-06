package at.tugraz.ist.ase.cspheuristix;

import at.tugraz.ist.ase.algorithms.Clustering;
import at.tugraz.ist.ase.algorithms.KNN;
import at.tugraz.ist.ase.algorithms.geneticAlgorithm.GeneticAlgorithm_CO;
import at.tugraz.ist.ase.algorithms.geneticAlgorithm.individual.Individual_CO;
import at.tugraz.ist.ase.diagnosers.Diagnoser;
import at.tugraz.ist.ase.solvers.CSP;
import at.tugraz.ist.ase.solvers.Const;
import at.tugraz.ist.ase.solvers.Solver;
import at.tugraz.ist.ase.util.ClusteringAlgorithmID;
import at.tugraz.ist.ase.util.DiagnoserID;
import at.tugraz.ist.ase.util.FileOperations;
import at.tugraz.ist.ase.util.HeuristicID;
import at.tugraz.ist.ase.util.PerformanceIndicator;
import at.tugraz.ist.ase.util.SolverID;

/** Represents Cluster Based Constraint Ordering Heuristics for Consistency Based Diagnosis
 * @author Seda Polat Erdeniz (AIG, TUGraz)
 * @author http://ase.ist.tugraz.at
 * @version 1.0
 * @since 1.0
*/

class CLCO extends Heuristics{
	
	//int [][] clusteredItems;
	CSP [][] trainingDatasetClustered;

	
	CLCO(HeuristicID heuristicsID, SolverID solverID, DiagnoserID diagnosisAlgorithmID, String inputFile,
			String outputFolder, PerformanceIndicator pi, String stoppingCriteria, ClusteringAlgorithmID cid,
			int numberOfClusters, int m) {
		super(heuristicsID, solverID, diagnosisAlgorithmID, inputFile, outputFolder, pi, stoppingCriteria, cid,
				numberOfClusters, m);
		// TODO Auto-generated constructor stub
	}

//	////////////////////////
//	// Default parameters //
//	int k;
//	ClusteringAlgorithmID cid = ClusteringAlgorithmID.kmeans;
//	SolverID sid;
//	DiagnoserID did;
//	CSP[][] trainingDataset;
//	Individual_CO[] learnedHeuristics;
//	int [][] clusteredItems;
//	String stoppingCriteria;
//	CSP [] pastCSPs;
//	int m;
//	////////////////////////

	@Override
	protected void learn() {
		// TODO Auto-generated method stub
		
		
		this.stoppingCriteria=stoppingCriteria;
		// get number of variables from inputFile
		int numberOfvars = FileOperations.readFile(pastSolutionsFile).get(0).split(",").length-1;
		
		
		// Step-1: Cluster past inconsistent user requirements of the same CSP tasks
		Clustering clustering = new Clustering();
		int [][] clusteredItems = clustering.cluster(cid, pastSolutionsFile, outputFolder, numberOfvars, k);
		trainingDatasetClustered = new CSP[k][];
		
		for (int i=0;i<k;i++){
			trainingDatasetClustered[i]=new CSP[clusteredItems[i].length];
			for(int j=0;j<clusteredItems[i].length;j++)
				trainingDatasetClustered[i][j]=trainingDataset[clusteredItems[i][j]];
		}
		learnedHeuristics = new Individual_CO[k];
			
		// Step-2: Learn Heuristics for Clusters
		for(int i=0;i<k;i++){
			learnedHeuristics[i] = (Individual_CO)(new GeneticAlgorithm_CO(numberOfvars, stoppingCriteria, pi, trainingDatasetClustered[i], hid, sid,did,m).getTheFittestIndividual());
		}
		
	}
	
	@Override
	protected CSP solveTask(CSP task) {
		// TODO Auto-generated method stub
		
		// This method is not supported yet
		
		return null;
	}

	@Override
	protected CSP diagnoseTask(CSP task) {
		// TODO Auto-generated method stub
		Const[] diagnosis = null;
		
		// Step-3: Find nearest cluster 
		KNN knn = new KNN();
		int index = knn.findClosestCluster(trainingDatasetClustered, task.getREQs());
		
		// Step-4: order REQs
		Const[] unsorted = task.getREQ().clone();
		Const[] sorted = new Const[unsorted.length];
		
		for(int i=0;i<unsorted.length;i++){
			for(int j=0;j<learnedHeuristics[index].variableOrdering.length;j++){
				int constIndex = learnedHeuristics[index].variableOrdering[j];
				if(unsorted[i].getVarID()==constIndex)
					sorted[constIndex]=unsorted[i];
			}
			//sorted[i]=learnedHeuristics[index].variableOrdering.getREQ()[i];
		}
		task.setREQ(sorted);
		
				
		// Step-5: Solve with the heuristics of the nearest cluster
		Diagnoser d = new Diagnoser();
		
		return d.diagnose(task, this.sid,this.did,m);
	}

	
}
