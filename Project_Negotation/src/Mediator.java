import java.io.*;

public class Mediator {

	int contractSize;

	public Mediator(int contractSizeA, int contractSizeB) throws FileNotFoundException {
		if (contractSizeA != contractSizeB) {
			throw new FileNotFoundException("Negotiation cannot be performed because problem data is not compatible");
		}
		this.contractSize = contractSizeA;
	}
// Part for Generate list of contracts at the beginning (David)
	public int[] initContract() {
		int[] contract = new int[contractSize];
		for (int i = 0; i < contractSize; i++)
			contract[i] = i;

		// we do a first shouffle of the contract
		for (int i = 0; i < contractSize; i++) {
			int index1 = (int) (Math.random() * contractSize);
			int index2 = (int) (Math.random() * contractSize);
			int value_index = contract[index1];
			contract[index1] = contract[index2];
			contract[index2] = value_index;
		}

		return contract;
	}

	// Two Mutation-Operators (Swap, Shift) (Norwell)
	public int[] constructProposal_SWAP(int[] contract) {
		int[] proposal = new int[contract.length];
		for (int i = 0; i < proposal.length; i++)
			proposal[i] = contract[i];

		int element = (int) ((proposal.length - 1) * Math.random());
		int wert1 = proposal[element];
		int wert2 = proposal[element + 1];
		proposal[element] = wert2;
		proposal[element + 1] = wert1;

		check(proposal);

		return proposal;
	}

	public int[] constructProposal_SHIFT(int[] contract) {
		int[] proposal = new int[contractSize];
		for (int i = 0; i < proposal.length; i++)
			proposal[i] = contract[i];

		int index1 = (int) ((proposal.length - 1) * Math.random());
		int index2 = (int) ((proposal.length - 1) * Math.random());
		// swap the index for have in the index2 the greater index
		if (index1 > index2) {
			int tmp = index1;
			index1 = index2;
			index2 = tmp;
		}
		if (Math.random() < 0.5) {
			// shift all to the left and insert the value1 in position index2
			int value1 = proposal[index1];
			for (int i = index1; i < index2; i++) {
				proposal[i] = proposal[i + 1];
			}
			proposal[index2] = value1;
		} else {
			// shift all to the right and insert the value2 in position index1
			int value2 = proposal[index2];
			for (int i = index2; i > index1; i--) {
				proposal[i] = proposal[i - 1];
			}
			proposal[index1] = value2;
		}
		check(proposal);
		return proposal;
	}

	public void check(int[] proposal) {
		// we check that the sum of the elements stay the same
		int sum = 0;
		int summe = proposal.length * (proposal.length - 1) / 2;
		for (int i = 0; i < proposal.length; i++) {
			sum += proposal[i];
		}
		if (sum != summe)
			System.out.println("Check the sum");
	}

	public int[] constructProposal(int[] contract) {
		int[] proposal;
		if (Math.random() < 0.5) {
			proposal = constructProposal_SHIFT(contract);
		} else {
			proposal = constructProposal_SWAP(contract);
		}
		return proposal;
	}

	// New method to calculate fitness score for a contract
	public double calculateFitnessScore(int[] contract, Agent agentA, Agent agentB) {
		int[] dummyContract = new int[contractSize];
		for (int j = 0; j < contractSize; j++) {
			dummyContract[j] = j; // Sequential contract as baseline
		}
	
		int score = 0;
		if (agentA.vote(dummyContract, contract)) score++;
		if (agentB.vote(dummyContract, contract)) score++;

		// Map vote count to fitness score
		return switch (score) {
		case 2 -> 1.0;
		case 1 -> 0.5;
		default -> 0.0;
		};
	}

	// New method to generate and rank multiple contracts (David)
	public int[][] generateRankedContract(Agent agentA, Agent agentB) {
		final int NUM_CONTRACTS = 10;
		int[][] contracts = new int[NUM_CONTRACTS][contractSize];
		double[] voteScores = new double[NUM_CONTRACTS];
		double[] fitnessScores = new double[NUM_CONTRACTS];
	
		// Generate contracts and vote-based scores
		for (int i = 0; i < NUM_CONTRACTS; i++) {
			contracts[i] = initContract();
			voteScores[i] = calculateFitnessScore(contracts[i], agentA, agentB); // returns 0–2
		}
	
		// Sort contracts based on voteScores (descending: 2 -> 1 -> 0)
		for (int i = 0; i < NUM_CONTRACTS - 1; i++) {
			for (int j = 0; j < NUM_CONTRACTS - i - 1; j++) {
				if (voteScores[j] < voteScores[j + 1]) {
					// Swap vote scores
					double tempScore = voteScores[j];
					voteScores[j] = voteScores[j + 1];
					voteScores[j + 1] = tempScore;
	
					// Swap contracts
					int[] tempContract = contracts[j];
					contracts[j] = contracts[j + 1];
					contracts[j + 1] = tempContract;
				}
			}
		}
	
		// Assign fitness scores: top = 1.0, middle = 0.5, others = 0.0
		for (int i = 0; i < NUM_CONTRACTS; i++) {
			if (i == 0)
				fitnessScores[i] = 1.0;
			else if (i == 1)
				fitnessScores[i] = 0.5;
			else
				fitnessScores[i] = 0.0;
		}
	
		return contracts;
	}
	
}

// Part for Selections (Nicolo)