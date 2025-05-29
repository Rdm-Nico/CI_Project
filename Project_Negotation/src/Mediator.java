import java.io.*;
import java.util.Arrays;

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

	public int[][] rank_selection(int[][] pop_contract, int selection_size) {
		// method for do a rank selection of the population contracts
		// @return a new population of contracts

		// set up the rank list
		double[] rank_list = new double[pop_contract.length];
		int sum = 0;
		for (int i = rank_list.length; i > 0; i--) {
			rank_list[rank_list.length - i] = i;
			sum += i;
		}
		for (int i = 0; i < rank_list.length; i++) {
			rank_list[i] /= sum;
		}
		// set up the selected population
		int[][] selected = new int[selection_size][pop_contract[0].length];

		// associate rank to contract
		// TODO: here is more efficient if the population of cotracts is already ordered
		// based on fitness
		// supp that that the list is ordered based on descending fitness
		// for(int i = 0; i < rank_list.length; i++){
		// }

		// choose random contracts based on rank
		// create a vector for make sure to select only the individual one time
		boolean[] isfree = new boolean[pop_contract.length];
		Arrays.fill(isfree, true);

		for (int i = 0; i < selection_size; i++) {
			double value = Math.random();
			double current = 0;
			int win_index = 0;
			for (int j = 0; j < pop_contract.length; j++) {
				if (value >= current && value <= current + rank_list[j]) {
					// we save the index
					win_index = j;
					break;
				}
				current += rank_list[j];
			}
			// peak the individual if we didnt alredy peak
			if (isfree[win_index]) {
				selected[i] = pop_contract[win_index];
				// we make unselectable the individual
				isfree[win_index] = false;
			} else {
				// we continue to search by decresing the index
				i--;
			}

		}

		// TODO: the selected list is not ordered based on fitness

		return selected;
	}

	public int[][] mue_lamba_selection(int[][] pop_contract, int mue, int lambda) {
		// method for do a (mue,lambda) selection of the population of contracts
		// @return a new population of contracts

		// set up selected population
		int[][] selected = new int[mue][pop_contract[0].length];

		// generate lambda offsrping from parents
		// TODO: modify with the correct implementation of crossover methods
		int[][] offspring = new int[lambda][pop_contract[0].length];

		// TODO: do fitness calculation of the offspring

		// select only the best mue best offspring

		// Take the first mue individuals
		for (int i = 0; i < mue; i++) {
			selected[i] = offspring[i].clone();
		}

		return selected;
	}

	public int[][] temperature_based_selection(int[][] pop_contract, int[] fitness, int selection_size,
			double temparature) {
		// method for do a temperature based selection of the population of contracts
		// @return a new population of contracts

		// set up selected population
		int[][] selected = new int[selection_size][pop_contract[0].length];
		// set up the probabilities
		double[] probs = new double[pop_contract.length];

		// compute the selection probability for individual i based on softmax-like
		// function
		// compute the denominator
		double denominator = 0;
		for (int i = 0; i < pop_contract.length; i++) {
			denominator += Math.exp(fitness[i] / temparature);
		}

		// compute for each individual the probs
		for (int i = 0; i < pop_contract.length; i++) {
			probs[i] = Math.exp(fitness[i] / temparature) / denominator;
		}
		// choose random contracts based on rank
		// create a vector for make sure to select only the individual one time
		boolean[] isfree = new boolean[pop_contract.length];
		Arrays.fill(isfree, true);

		for (int i = 0; i < selection_size; i++) {
			double value = Math.random();
			double current = 0;
			int win_index = 0;
			for (int j = 0; j < pop_contract.length; j++) {
				if (value >= current && value <= current + probs[j]) {
					// we save the index
					win_index = j;
					break;
				}
				current += probs[j];
			}
			// peak the individual if we didnt alredy peak
			if (isfree[win_index]) {
				selected[i] = pop_contract[win_index];
				// we make unselectable the individual
				isfree[win_index] = false;
			} else {
				// we continue to search by decresing the index
				i--;
			}

		}

		// TODO: the selected list is not ordered based on fitness
		return selected;
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

// Part for Selections (Nicolo)