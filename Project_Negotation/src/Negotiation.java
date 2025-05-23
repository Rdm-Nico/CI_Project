import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import utils.Log;

// DO NOT USE EVALUATE-FUNCTIONS OF AGENTS WITHIN MEDIATOR OR NEGOTIATION!
// THESE OBLECTIVE-VALUES ARE NOT AVAILABLE IN REAL NEGOTIATIONS!!!!!!!!!!!!!!!!!!!!  
// IT IS ONLY ALLOWED TO PRINT THESE OBJECTIVE-VALUES IN THE CONSOLE FOR ANALYZING REASONS
// this is for testing github 

public class Negotiation {
	// Parameter of negotiation
	public static int maxRounds = 10;
	private static final Logger logger = new Log().InitLog();

	public static void main(String[] args) {
		int[] contract, proposal;
		Agent agA, agB;
		Mediator med;
		boolean voteA, voteB;

		try {
			// upload data
			String[] inSu200 = new String[4];
			String[] inCu200 = new String[4];
			inSu200[0] = "Project_Negotation/data/daten3ASupplier_200.txt";
			inSu200[1] = "Project_Negotation/data/daten3BSupplier_200.txt";
			inSu200[2] = "Project_Negotation/data/daten4ASupplier_200.txt";
			inSu200[3] = "Project_Negotation/data/daten4BSupplier_200.txt";
			inCu200[0] = "Project_Negotation/data/daten3ACustomer_200_10.txt";
			inCu200[1] = "Project_Negotation/data/daten3BCustomer_200_20.txt";
			inCu200[2] = "Project_Negotation/data/daten4ACustomer_200_5.txt";
			inCu200[3] = "Project_Negotation/data/daten4BCustomer_200_5.txt";

			for (int i = 0; i < inSu200.length; i++) {
				for (int j = 0; j < inCu200.length; j++) {
					logger.info("Instance: " + i + " " + j + "\n");
					agA = new SupplierAgent(new File(inSu200[i]));
					agB = new CustomerAdvanced(new File(inCu200[j]));
					med = new Mediator(agA.getContractSize(), agB.getContractSize());
					contract = med.initContract(); // Vertrag=Lsung=Jobliste
					output(agA, agB, 0, contract);

					for (int round = 1; round < maxRounds; round++) { // Mediator
						proposal = med.constructProposal(contract);
						voteA = agA.vote(contract, proposal); // Autonomie + Private Infos
						voteB = agB.vote(contract, proposal);
						if (voteA && voteB) {
							contract = proposal;
							output(agA, agB, round, contract);
						}
					}
				}
			}

			// logger.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public static void output(Agent a1, Agent a2, int i, int[] contract) {
		logger.info(i + " -> ");
		a1.printUtility(contract, logger);
		logger.info("  ");
		a2.printUtility(contract, logger);
		logger.info("\n");
	}
}