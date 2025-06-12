import java.io.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	// Randomly select a subsequence and shuffle its contents (not reverse, but
	// randomize the order).
	// n is the size of the random array.
	public int[] scrambleMutation(int[] contract, int n) {
		int randnum = (int) (Math.random() * (contractSize - n + 1));
		int start = randnum, end = randnum + n;
		System.out.println("start: " + start + " end: " + end);
		List<Integer> list = new ArrayList<>();

		for (int i = start; i < end; i++) {
			list.add(contract[i]);
		}
		System.out.println(list);
		Collections.shuffle(list);

		int count = 0;
		for (int i = start; i < end; i++) {
			contract[i] = list.get(count);
			count++;
		}
		check(contract);
		return contract;

	}

	// Randomly select a subsequence and reverse its contents
	// n is the size of the inverse array.
	public int[] inverseMutation(int[] contract, int n) {
		int randnum = (int) (Math.random() * (contractSize - n + 1));
		int start = randnum, end = randnum + n;
		System.out.println("start: " + start + " end: " + end);
		List<Integer> list = new ArrayList<>();

		for (int i = start; i < end; i++) {
			list.add(contract[i]);
		}
		System.out.println(list);

		Collections.reverse(list);
		System.out.println("Reversed: " + list);

		int count = 0;
		for (int i = start; i < end; i++) {
			contract[i] = list.get(count);
			count++;
		}
		check(contract);
		return contract;

	}

	// Randomly select a subsequence and insert it to another position
	public int[] displacementMutation(int[] contract, int n) {
		int randnum = (int) (Math.random() * (contractSize - n + 1)); // sequence start position
		int position = (int) (Math.random() * (contractSize - n)); // insert position
		int start = randnum, end = randnum + n;
		System.out.println("start: " + start + " end: " + end + " position: " + position);
		List<Integer> list = new ArrayList<>();
		List<Integer> seq = new ArrayList<>();

		for (int i = start; i < end; i++) { // put all the sequence number inside
			seq.add(contract[i]);
		}
		System.out.println("seq: " + seq);
		for (int i = 0; i < contractSize; i++) { // put only the number not in seq
			if (i >= start && i < end)
				continue;
			list.add(contract[i]);
		}
		// insert the seq into list at the position
		for (int i = 0; i < n; i++) {
			list.add(position + i, seq.get(i)); // put before the position ex: 234 -> position 1, it will put like
												// this2[]34
		}
		System.out.println("List: ");
		System.out.println(list);

		// put them into contract
		for (int i = 0; i < contractSize; i++) {
			contract[i] = list.get(i);
		}

		return contract;

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

		return selected;
	}

	public int[][] tournament_selection(int[][] pop_contract, double[] fitness, int selection_size) {
		// method for do tournament selection of the population of contracts

		// set up the selected population
		int[][] selected = new int[selection_size][pop_contract[0].length];
		for (int i = 0; i < selected.length; i++) {
			// we select two random individual and compare the fitness of this two
			int index = 0;

			// Tournement Selection for maximization!
			int index1 = (int) (Math.random() * pop_contract.length);
			int index2 = (int) (Math.random() * pop_contract.length);

			if (fitness[index1] > fitness[index2]) {
				index = index1;
			} else {
				index = index2;
			}

			// save the individual in the new population
			selected[i] = pop_contract[index];
		}
		return selected;
	}

	public int[][] temperature_based_selection(int[][] pop_contract, double[] fitness, int selection_size,
			double temparature) {
		// method for do a temperature based selection of the population of contracts
		// @return a new population of contracts

		// set up selected population
		int[][] selected = new int[selection_size][pop_contract[0].length];
		// set up the probabilities
		double[] probs = new double[pop_contract.length];

		// compute the selection probability for individual i based on softmax-like
		// function
		// compute before the denominator
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
	// Crossver oderCrossover + positionBasedCrossover + Cyclecrossover
	// oderCrossover EX: MAMA: [1,2,3,4,5], PAPA: [5,2,3,4,1], start

	// Randomly select a substring of length 'n' from 'mama' to retain, and fill
	// the remaining positions with characters from 'papa' in their original order.
	// n is the length of the sequence
	public int[][] oderCrossover(int[] mama, int[] papa, int n) {
		int crosspoint = (int) (Math.random() * (contractSize - n + 1));
		int start = crosspoint, end = crosspoint + n - 1;

		System.out.println("start: " + start + " end: " + end);
		int son[] = new int[contractSize];
		int doughter[] = new int[contractSize];
		int seqson[] = new int[n];
		int seqdougter[] = new int[n];
		int posson[] = new int[contractSize - n];
		int posdoughter[] = new int[contractSize - n];

		// store all the seq
		int count = 0;
		for (int i = 0; i < contractSize; i++) {
			if (i >= start && i <= end) {
				seqson[count] = mama[i];
				seqdougter[count++] = papa[i];
			}
		}

		// find not repeat index in order
		boolean sonrepeat = false, dourepeat = false;
		int soncount = 0, doucount = 0;
		for (int i = 0; i < contractSize; i++) {
			for (int j = 0; j < n; j++) {
				if (papa[i] == seqson[j]) {
					sonrepeat = true;
					System.out.println("sonreppeat: " + papa[i]);
				}
				if (mama[i] == seqdougter[j]) {
					System.out.println("dourepeat: " + mama[i]);
					dourepeat = true;
				}
			}
			if (!sonrepeat) {
				System.out.println("sonrepeat record");
				posson[soncount++] = papa[i];
			}
			if (!dourepeat) {
				System.out.println("dourepear record");
				posdoughter[doucount++] = mama[i];
			}
			sonrepeat = false;
			dourepeat = false;
		}
		// put not repeat index in order together with the stable sequence
		soncount = 0;
		doucount = 0;
		int possoncount = 0, posdoucount = 0;
		for (int i = 0; i < contractSize; i++) {
			if (i >= start && i <= end) {
				son[i] = seqson[soncount];
				doughter[i] = seqdougter[doucount];
				soncount++;
				doucount++;
			} else {
				son[i] = posson[possoncount++];
				doughter[i] = posdoughter[posdoucount++];
			}
		}

		return new int[][] { son, doughter };
	}

	// It will choose n position form mama, and take the value also the position
	// The remaining positions are then supplemented with unique values from 'papa
	// n is how many position to select from one parent
	public int[][] positionBasedCrossover(int[] mama, int[] papa, int n) {
		int resindex[] = new int[n];
		int posson[] = new int[contractSize - n];
		int posdoughter[] = new int[contractSize - n];
		int son[] = new int[contractSize];
		int doughter[] = new int[contractSize];
		int seqson[] = new int[n];
		int seqdougter[] = new int[n];

		// initial the position of the remaining index
		boolean key = false;
		for (int i = 0; i < n; i++) {
			key = false;
			int rand = (int) (Math.random() * contractSize);
			for (int j = 0; j < i; j++) {
				if (rand == resindex[j]) {
					key = true;
					break;
				}
			}
			if (key == false) {
				resindex[i] = rand;
				System.out.println("value son: " + mama[resindex[i]]);
				System.out.println("value papa: " + papa[resindex[i]]);
			} else {
				i--;
			}

		}

		Arrays.sort(resindex);
		// store all the seq
		int count = 0, indexcount = 0;
		for (int i = 0; i < contractSize; i++) {
			if (resindex[indexcount] == i) {
				seqson[count] = mama[i];
				seqdougter[count++] = papa[i];
				indexcount++;
				if (indexcount >= n)
					break;
			}
		}
		// find not repeat index in order
		boolean sonrepeat = false, dourepeat = false;
		int soncount = 0, doucount = 0;
		for (int i = 0; i < contractSize; i++) {
			for (int j = 0; j < n; j++) {
				if (papa[i] == seqson[j]) {
					sonrepeat = true;
					// System.out.println("sonreppeat: "+papa[i]);
				}
				if (mama[i] == seqdougter[j]) {
					// System.out.println("dourepeat: "+ mama[i]);
					dourepeat = true;
				}
			}
			if (!sonrepeat) {
				// System.out.println("son record");
				posson[soncount++] = papa[i];
			}
			if (!dourepeat) {
				// System.out.println("doughter record");
				posdoughter[doucount++] = mama[i];
			}
			sonrepeat = false;
			dourepeat = false;
		}

		// put not repeat index in order together with the stable sequence
		soncount = 0;
		doucount = 0;
		indexcount = 0;
		int possoncount = 0, posdoucount = 0;
		for (int i = 0; i < contractSize; i++) {
			if (indexcount < n && resindex[indexcount] == i) {
				son[i] = seqson[soncount++];
				doughter[i] = seqdougter[doucount++];
				indexcount++;
			} else {
				son[i] = posson[possoncount++];
				doughter[i] = posdoughter[posdoucount++];
			}
		}

		return new int[][] { son, doughter };
	}

	// it will find a cycle and put the position of that cycle
	// pos is the position to start
	public int[][] cycleCrossover(int[] mama, int[] papa, int pos) {
		int son[] = new int[contractSize];
		int doughter[] = new int[contractSize];
		Arrays.fill(son, -1);
		Arrays.fill(doughter, -1);
		List<Integer> cycleindex = new ArrayList<>();

		boolean key = true;
		int originvalue = -1, valuepapa = -1, posmama = -1;
		while (key) {
			if (originvalue == -1) {
				originvalue = mama[pos];
				posmama = pos;
				valuepapa = papa[pos];
				cycleindex.add(pos);
			} else {
				posmama = findIndex(mama, valuepapa);
				valuepapa = papa[posmama];
				cycleindex.add(posmama);
			}
			if (valuepapa == originvalue) {
				key = false;
			}
		}
		System.out.println(cycleindex);
		Collections.sort(cycleindex);

		// find not repeat index in order
		int[] posson;
		int[] posdoughter;
		int[][] childs;

		childs = getRemainingElements(cycleindex, mama, papa);
		posson = childs[0];
		posdoughter = childs[1];

		// complete son and doughter
		int soncount = 0, doucount = 0;
		for (int i = 0; i < cycleindex.size(); i++) {
			son[cycleindex.get(i)] = mama[cycleindex.get(i)];
			doughter[cycleindex.get(i)] = papa[cycleindex.get(i)];
		}
		soncount = 0;
		doucount = 0;
		for (int i = 0; i < contractSize; i++) {
			if (son[i] == -1) {
				son[i] = posson[soncount++];
			}
			if (doughter[i] == -1) {
				doughter[i] = posdoughter[doucount++];
			}
		}

		return new int[][] { son, doughter };
	}

	// functions for crossover
	public int findIndex(int[] array, int value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == value)
				return i;
		}
		return -1;
	}

	public int[][] getRemainingElements(List<Integer> cycleindex, int[] mama, int[] papa) {
		Set<Integer> mamaCycleValues = new HashSet<>();
		Set<Integer> papaCycleValues = new HashSet<>();
		int posson[] = new int[contractSize - cycleindex.size()];
		int posdoughter[] = new int[contractSize - cycleindex.size()];
		int soncount = 0, doucount = 0;

		for (int idx : cycleindex) {
			mamaCycleValues.add(mama[idx]);
			papaCycleValues.add(papa[idx]);
		}
		for (int i = 0; i < contractSize; i++) {
			if (!mamaCycleValues.contains(papa[i])) {
				posson[soncount++] = papa[i];
			}
			if (!papaCycleValues.contains(mama[i])) {
				posdoughter[doucount++] = mama[i];
			}
		}
		return new int[][] { posson, posdoughter };

	}

	// New method to calculate fitness score for a contract
	public double calculateFitnessScore(int[] contract, Agent agentA, Agent agentB) {
		int[] dummyContract = new int[contractSize];
		for (int j = 0; j < contractSize; j++) {
			dummyContract[j] = j; // Sequential contract as baseline
		}

		int score = 0;
		if (agentA.vote(dummyContract, contract))
			score++;
		if (agentB.vote(dummyContract, contract))
			score++;

		// Map vote count to fitness score
		return switch (score) {
			case 2 -> 1.0;
			case 1 -> 0.5;
			default -> 0.0;
		};
	}

	// New method to calculate fitness score for a contract
	public double calculateFitnessScore_WithdDummy(int[] contract, Agent agentA, Agent agentB, int[] dummyContract) {
		int score = 0;
		if (agentA.vote(dummyContract, contract))
			score++;
		if (agentB.vote(dummyContract, contract))
			score++;

		// Map vote count to fitness score
		return switch (score) {
			case 2 -> 1.0;
			case 1 -> 0.5;
			default -> 0.0;
		};
	}

	// New method to generate and rank multiple contracts (David)
	public int[][] generateRankedContract(Agent agentA, Agent agentB, int NUM_CONTRACTS) {
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

	public Map<String, Object> sorted_Contract(int[][] contracts, Agent agentA, Agent agentB, int[] dummyContract) {
		/*
		 * Function for sort the population of contracts based on fitness
		 */

		// compute the fitness for each child
		double[] fitness_list = new double[contracts.length];

		for (int i = 0; i < contracts.length; i++) {
			fitness_list[i] = calculateFitnessScore_WithdDummy(contracts[i], agentA, agentB, dummyContract);
		}

		// Sort contracts based on voteScores (descending: 2 -> 1 -> 0)
		for (int i = 0; i < contracts.length - 1; i++) {
			for (int j = 0; j < contracts.length - i - 1; j++) {
				if (fitness_list[j] < fitness_list[j + 1]) {
					// Swap vote scores
					double tempScore = fitness_list[j];
					fitness_list[j] = fitness_list[j + 1];
					fitness_list[j + 1] = tempScore;

					// Swap contracts
					int[] tempContract = contracts[j];
					contracts[j] = contracts[j + 1];
					contracts[j + 1] = tempContract;
				}
			}
		}
		Map<String, Object> result = new HashMap<>();
		result.put("contracts", contracts);
		result.put("fitness", fitness_list);
		return result;
	}
}