import java.io.*;

public class Mediator {

	int contractSize;

	public Mediator(int contractSizeA, int contractSizeB) throws FileNotFoundException {
		if (contractSizeA != contractSizeB) {
			throw new FileNotFoundException("Negotiation cannot be performed because problem data is not compatible");
		}
		this.contractSize = contractSizeA;
	}

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

	// Two Mutation-Operators (Swap, Shift)
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
}
