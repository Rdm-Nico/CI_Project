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
        int[][] rankedContracts = mediator.generateRankedContract(agentA, agentB);
        for (int i = 0; i < rankedContracts.length; i++) {
            double fitness = mediator.calculateFitnessScore(rankedContracts[i], agentA, agentB);
            System.out.print("Rank " + (i + 1) + ": ");
            printArray(rankedContracts[i]);
            System.out.printf(" => Fitness: %.2f\n", fitness);
        }
    }

    public static void printArray(int[] array) {
        System.out.print("[");
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]);
            if (i < array.length - 1) System.out.print(", ");
        }
        System.out.print("]");
    }
}
