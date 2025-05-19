import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CustomerAdvanced extends Agent {

	private int[][] timeMatrix;
	// maybe the delay matrix tells you how much delays there is if we compute a
	// job1 and after a job2, but i need to thing
	private int[][] delayMatrix;// pre-calculated based on timeMatrix for efficiency reason

	public CustomerAdvanced(File file) throws FileNotFoundException {

		Scanner scanner = new Scanner(file);
		int jobs = scanner.nextInt();
		int machines = scanner.nextInt();
		timeMatrix = new int[jobs][machines];
		// the timeMatrix is a 2x2 mat that describe for each columns (machine) and each
		// rows(jobs) how much time a machine make that job
		for (int i = 0; i < timeMatrix.length; i++) {
			for (int j = 0; j < timeMatrix[i].length; j++) {
				int x = scanner.nextInt();
				timeMatrix[i][j] = x;
			}
		}
		calculateDelay(timeMatrix.length);
		scanner.close();
	}

	public boolean vote(int[] contract, int[] proposal) {
		// greedy selection -> minimize time of production
		int timeContract = evaluate(contract);
		int timeProposal = evaluate(proposal);
		if (timeProposal < timeContract)
			return true;
		else
			return false;
	}

	public int getContractSize() {
		return timeMatrix.length;
	}

	public void printUtility(int[] contract) {
		System.out.print(evaluate(contract));
	}

	private void calculateDelay(int jobNr) {
		delayMatrix = new int[jobNr][jobNr];
		for (int h = 0; h < jobNr; h++) {
			for (int j = 0; j < jobNr; j++) {
				delayMatrix[h][j] = 0;
				if (h != j) {
					int maxWait = 0;
					for (int machine = 0; machine < timeMatrix[0].length; machine++) {
						int wait_h_j_machine;

						int time1 = 0;
						for (int k = 0; k <= machine; k++) {
							time1 += timeMatrix[h][k];
						}
						int time2 = 0;
						for (int k = 1; k <= machine; k++) {
							time2 += timeMatrix[j][k - 1];
						}
						wait_h_j_machine = Math.max(time1 - time2, 0);
						if (wait_h_j_machine > maxWait)
							maxWait = wait_h_j_machine;
					}
					delayMatrix[h][j] = maxWait;
				}
			}
		}
	}

	private int evaluate(int[] contract) {
		int result = 0;
		for (int i = 1; i < contract.length; i++) {
			int predecessor_job = contract[i - 1];
			int job = contract[i];
			result += delayMatrix[predecessor_job][job];
		}
		int lastjob = contract[contract.length - 1];
		for (int machine = 0; machine < timeMatrix[0].length; machine++) {
			result += timeMatrix[lastjob][machine];
		}
		return result;
	}

}