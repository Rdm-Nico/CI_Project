import java.io.File;

public class MediatorTest {
    public static void main(String[] args) throws Exception {
        int contractSize = 5;
        Mediator mediator = new Mediator(contractSize, contractSize);
        SupplierAgent agentA = new SupplierAgent(new File("Project_Negotation/data/daten3ASupplier_200.txt"));
        CustomerAdvanced agentB = new CustomerAdvanced(new File("Project_Negotation/data/daten3ACustomer_200_10.txt"));

        // Print unsorted contracts and their fitness scores
        System.out.println("Unsorted Contracts (with fitness):");
        int[][] contracts = new int[10][contractSize];
        double[] fitnessScoresUnsorted = new double[10];
        for (int i = 0; i < contracts.length; i++) {
            contracts[i] = mediator.initContract();
            fitnessScoresUnsorted[i] = mediator.calculateFitnessScore(contracts[i], agentA, agentB);
            System.out.print("Contract " + (i + 1) + ": ");
            printArray(contracts[i]);
            System.out.printf(" => Fitness: %.2f\n", fitnessScoresUnsorted[i]);
        }

        // Print sorted (ranked) contracts and their fitness scores
        System.out.println("\nRanked Contracts (by fitness):");
        int[][] rankedContracts = mediator.generateRankedContract(agentA, agentB, 10);
        for (int i = 0; i < rankedContracts.length; i++) {
            double fitness = mediator.calculateFitnessScore(rankedContracts[i], agentA, agentB);
            System.out.print("Rank " + (i + 1) + ": ");
            printArray(rankedContracts[i]);
            System.out.printf(" => Fitness: %.2f\n", fitness);
        }

        // Feeding input to selection methods
        System.out.println("\nFeeding input to selection methods");
        int[][] selectedContracts = mediator.rank_selection(rankedContracts, contractSize);
        for (int i = 0; i < selectedContracts.length; i++) {
            double fitness = mediator.calculateFitnessScore(selectedContracts[i], agentA, agentB);
            System.out.print("Selected " + (i + 1) + ": ");
            printArray(selectedContracts[i]);
            System.out.printf(" => Fitness: %.2f\n", fitness);
        }
        int j = 0;
        int i = j + 1;
        int count = 0;
        int[][] new_pop = new int[10][contractSize];
        while (count != rankedContracts.length) {
            int[][] childrens = mediator.oderCrossover(selectedContracts[j], selectedContracts[i], 3);
            // mutation here
            new_pop[count++] = childrens[0];
            new_pop[count++] = childrens[1];
            if (i == selectedContracts.length - 1) {
                j++;
                i = j;
            }
            i++;
        }

        System.out.println("New population after selection and crossover:");
        // new_pop = mediator.sorted_Contract(new_pop, agentA, agentB);
        // print
        for (i = 0; i < new_pop.length; i++) {
            double fitness = mediator.calculateFitnessScore(new_pop[i], agentA, agentB);
            System.out.print("Rank " + (i + 1) + ": ");
            printArray(new_pop[i]);
            System.out.printf(" => Fitness: %.2f\n", fitness);
        }
    }

    public static void printArray(int[] array) {
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1)
                System.out.print(", ");
        }
        System.out.print("]");
    }
}
