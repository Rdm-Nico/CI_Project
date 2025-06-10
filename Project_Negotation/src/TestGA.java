import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class TestGA {

    public static void main(String[] args) throws FileNotFoundException {
        int contractSize = 5;
        Mediator mediator = new Mediator(contractSize, contractSize);

        // Introduce two agents
        File supplierFile = new File("/Users/davidkang/Desktop/HfT Stuttgart/Concept of Programming Language/CI_Project/Project_Negotation/data/daten3ASupplier_200.txt");
        File customerFile = new File("/Users/davidkang/Desktop/HfT Stuttgart/Concept of Programming Language/CI_Project/Project_Negotation/data/daten3ACustomer_200_10.txt");
        Agent agentA = new SupplierAgent(supplierFile);
        Agent agentB = new CustomerAdvanced(customerFile);

        // Select mutation and crossover (for user to change different methods)
        int n = 3;
        String mutationMethod = "SCRAMBLE";
        String crossoverMethod = "CYCLE";

        // Generate Ranked contracts as input for the methods
        int[][] rankedContracts = mediator.generateRankedContract(agentA, agentB);

        System.out.println("Top-Ranked Contracts:");
        for (int i = 0; i < rankedContracts.length; i++) {
            System.out.println("[" + i + "] Fitness: " + mediator.calculateFitnessScore(rankedContracts[i], agentA, agentB)
                                + " " + Arrays.toString(rankedContracts[i]));
        }

        // Crossover Execution
        System.out.println(" Crossover Applied to Top 2 Contracts");
        int[] mama = rankedContracts[0];
        int[] papa = rankedContracts[1];
        int[][] offspring = applyCrossover(mediator, mama, papa, crossoverMethod, n);
        System.out.println("Mama: " + Arrays.toString(mama));
        System.out.println("Papa: " + Arrays.toString(papa));
        System.out.println("Son: " + Arrays.toString(offspring[0]));
        System.out.println("Daughter: " + Arrays.toString(offspring[1]));

        // Mutation Execution
        System.out.println(" Mutation Applied to both childrens");
        System.out.println("Original Son: " + Arrays.toString(offspring[0]));
        System.out.println("Original Daughter: " + Arrays.toString(offspring[1]));
        int[] mutatedSon = applyMutation(mediator, offspring[0], mutationMethod, n);
        int[] mutatedDaughter = applyMutation(mediator, offspring[1], mutationMethod, n);
        System.out.println("Mutated Son: " + Arrays.toString(mutatedSon));
        System.out.println("Mutated Daughter: " + Arrays.toString(mutatedDaughter));

        // Evaluate mutated Son & Daughter
        System.out.println("\n--- Mutated Son Evaluation ---");

    }

    // List of Crossover methods
    private static int[] applyMutation(Mediator mediator, int[] contract, String method, int n) {
        return switch (method) {
            case "SCRAMBLE" -> mediator.scrambleMutation(contract, n);
            case "INVERSE" -> mediator.inverseMutation(contract, n);
            case "DISPLACEMENT" -> mediator.displacementMutation(contract, n);
            default -> throw new IllegalStateException("Unknown mutation: " + method);
        };
    }


    // List of Crossover methods
    private static int[][] applyCrossover(Mediator mediator, int[] mama, int[] papa, String method, int n) {
        return switch (method) {
            case "ORDER" -> mediator.oderCrossover(mama, papa, n);
            case "POSITION_BASED" -> mediator.positionBasedCrossover(mama, papa, n);
            case "CYCLE" -> mediator.cycleCrossover(mama, papa, 0);
            default -> throw new IllegalStateException("Unknown crossover: " + method);
        };
    }

    // Get the cost from both agents
    private static int getCostFromAgent(Agent agent, int[] proposal) {
        try {
            var method = agent.getClass().getDeclaredMethod("evaluate", int[].class);
            method.setAccessible(true); // bypass private
            return (int) method.invoke(agent, (Object) proposal);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access cost from agent: " + agent.getClass().getSimpleName(), e);
        }
    }
}