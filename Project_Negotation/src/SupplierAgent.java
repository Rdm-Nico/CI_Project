import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SupplierAgent extends Agent {

	private int[][] costMatrix;

	public SupplierAgent(File file) throws FileNotFoundException {
		// upload the file and read
		Scanner scanner = new Scanner(file);
		// first element is the dimension
		int dim = scanner.nextInt();
		costMatrix = new int[dim][dim];
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				int x = scanner.nextInt();
				costMatrix[i][j] = x;
			}
		}
		scanner.close();
	}

	public boolean vote(int[] contract, int[] proposal) {
		// greedy selection -> minimize the cost of the production
		int costContract = evaluate(contract);
		int costProposal = evaluate(proposal);
		if (costProposal < costContract)
			return true;
		else
			return false;
	}

	public int getContractSize() {
		return costMatrix.length;
	}

	public void printUtility(int[] contract) {
		System.out.print(evaluate(contract));
	}

	private int evaluate(int[] contract) {
		// We compute the set-up cost from the CostMatrix of the current contract
		int result = 0;
		for (int i = 0; i < contract.length - 1; i++) {
			int precedessor = contract[i];
			int successor = contract[i + 1];
			result += costMatrix[precedessor][successor];
		}

		return result;
	}

}