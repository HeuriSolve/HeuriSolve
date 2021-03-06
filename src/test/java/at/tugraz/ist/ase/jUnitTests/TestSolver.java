package at.tugraz.ist.ase.jUnitTests;
import at.tugraz.ist.ase.solvers.CSP;
import at.tugraz.ist.ase.solvers.Const;
import at.tugraz.ist.ase.solvers.Solver;
import at.tugraz.ist.ase.solvers.Var;
import at.tugraz.ist.ase.util.DiagnoserID;
import at.tugraz.ist.ase.util.SolverID;

import org.junit.Test;
import static org.junit.Assert.*;

/** Tests for CSP Solving
 * @author Seda Polat Erdeniz (AIG, TUGraz)
 * @author http://ase.ist.tugraz.at
 * @version 1.0
 * @since 1.0
*/

public class TestSolver {
	
	////////////////////////////////
	//  SETTINGS 				  //
	////////////////////////////////
	SolverID sid = SolverID.choco;
	////////////////////////////////

	////////////////////////////////
	//  INPUTS  				  //
	////////////////////////////////
	Var var1 = new Var(""+0, 0, 5);
	Var [] vars = new Var[]{var1};
	
	Const cons1 = new Const(0, ">", 3);
	Const [] consArray1 = new Const []{cons1};
	
	Const cons2 = new Const(0, ">", 6);
	Const [] consArray2 = new Const []{cons2};
	////////////////////////////////

	
	////////////////////////////////
	//  TESTS    				  //
	////////////////////////////////
	
    @Test
    public void testSolutionFound(){
		
		/////////////////////////////////
		Solver solver = new Solver();
		CSP csp = new CSP("test1",vars, consArray1,null,null);
		
		CSP soln = solver.solveCSP(csp,sid, null);
		
		assertTrue(soln.isSolved());
		///////////////////////////////
		
	}
    
    @Test
    public void testNoSolutionFound(){
		
		/////////////////////////////////
		Solver solver2 = new Solver();
		CSP csp2 = new CSP("test2",vars, consArray2,null,null);
		
		CSP soln2 = solver2.solveCSP(csp2,sid, null);
		
		assertFalse(soln2.isSolved());
		///////////////////////////////
		
	}
}
