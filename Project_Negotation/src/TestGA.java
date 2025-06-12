import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Map;

public class TestGA {
    // Parameter of negotiation
    static int maxRounds = 10;
    static int NUM_CONTRACTS = 10;
    // parameters for crossover & mutation
    static int array_size = 3;
    // parameters for selection
    static int selection_size = 4;
    static int mue = 2;
    static int lambda = 2;
    static double alpha = 0.9;
    static String selectionMethod = "RANK";
    static String crossoverMethod = "CYCLE";
    static String mutationMethod = "SCRAMBLE";

    public static void main(String[] args) throws FileNotFoundException {

        // Introduce two agents
        File supplierFile = new File(
                "Project_Negotation/data/daten3ASupplier_200.txt");
        File customerFile = new File(
                "Project_Negotation/data/daten3ACustomer_200_10.txt");
        Agent agentA = new SupplierAgent(supplierFile);
        Agent agentB = new CustomerAdvanced(customerFile);

        Mediator mediator = new Mediator(agentA.getContractSize(), agentB.getContractSize());

        // Generate Ranked contracts as input for the methods
        int[][] rankedContracts = mediator.generateRankedContract(agentA, agentB, NUM_CONTRACTS);
        double[] fitness = new double[rankedContracts.length];

        System.out.println("Top-Ranked Contracts:");
        for (int i = 0; i < rankedContracts.length; i++) {
            double f = mediator.calculateFitnessScore(rankedContracts[i], agentA, agentB);
            fitness[i] = f;
            System.out.println(
                    "[" + i + "] Fitness: " + f
                            + " " + Arrays.toString(rankedContracts[i]));

        }

        // data structure for save the result
        double[][] experiment_result = new double[maxRounds][2];

        // select the dummy contract by taking the one in the middle
        int[] dummyContract = rankedContracts[(rankedContracts.length / 2) - 1];
        // add temperature parameters (also if not use)
        double temperature = 10;

        // Start Iterations
        for (int epoch = 0; epoch < maxRounds; epoch++) {
            System.out.println("New generation: " + epoch);

            // if necessary compute the new temperature values
            if (selectionMethod == "TEMPERATURE") {
                temperature *= Math.pow(temperature, epoch);
            }

            // Selection Execution
            int[][] selected_contracts = applySelection(mediator, rankedContracts, fitness, selectionMethod,
                    selection_size, mue, lambda, temperature);

            // Crossover and Mutation Execution
            int j = 0;
            int i = j + 1;
            int count = 0;
            int[][] new_pop = new int[rankedContracts.length][contractSize];
            while (count != rankedContracts.length) {
                int[] mama = selected_contracts[j];
                int[] papa = selected_contracts[i];
                int[][] offspring = applyCrossover(mediator, mama, papa, crossoverMethod, array_size);
                System.out.println("Mama: " + Arrays.toString(mama));
                System.out.println("Papa: " + Arrays.toString(papa));
                System.out.println("Son: " + Arrays.toString(offspring[0]));
                System.out.println("Daughter: " + Arrays.toString(offspring[1]));
                // Mutation
                int[] mutatedSon = applyMutation(mediator, offspring[0], mutationMethod, array_size);
                int[] mutatedDaughter = applyMutation(mediator, offspring[0], mutationMethod, array_size);
                // add the offspring in the new population
                new_pop[count++] = mutatedSon;
                new_pop[count++] = mutatedDaughter;
                if (i == selected_contracts.length - 1) {
                    j++;
                    i = j;
                }
                i++;
            }

            System.out.println("New population after selection, crossover and mutation:");
            // sort the population and compute the fitness
            Map<String, Object> result = mediator.sorted_Contract(new_pop, agentA, agentB, dummyContract);
            rankedContracts = (int[][]) result.get("contracts");
            fitness = (double[]) result.get("fitness");

            // change the dummyContract
            dummyContract = rankedContracts[(rankedContracts.length / 2) - 1];

            // save the value of the top1
            int cost_agentA = getCostFromAgent(agentA, rankedContracts[0]);
            int cost_agentB = getCostFromAgent(agentB, rankedContracts[0]);

            System.out.println("top 1 contract: " + cost_agentA + " " + cost_agentB);

            experiment_result[epoch][0] = cost_agentA;
            experiment_result[epoch][1] = cost_agentB;
        }

        // save the result of the experimental
        try {
            java.io.FileWriter writer = new java.io.FileWriter("experiment_results.csv");
            // Write header
            writer.write("Round,Cost_AgentA,Cost_AgentB\n");

            // Write data
            for (int i = 0; i < maxRounds; i++) {
                writer.write(i + "," + experiment_result[i][0] + "," + experiment_result[i][1] + "\n");
            }

            writer.close();
            System.out.println("Results saved to experiment_results.csv");
        } catch (java.io.IOException e) {
            System.err.println("Error saving results: " + e.getMessage());
        }

        // Crossover Execution
        // System.out.println(" Crossover Applied to Top 2 Contracts");
        // int[] mama = rankedContracts[0];
        // int[] papa = rankedContracts[1];
        // int[][] offspring = applyCrossover(mediator, mama, papa, crossoverMethod,
        // array_size);
        // System.out.println("Mama: " + Arrays.toString(mama));
        // System.out.println("Papa: " + Arrays.toString(papa));
        // System.out.println("Son: " + Arrays.toString(offspring[0]));
        // System.out.println("Daughter: " + Arrays.toString(offspring[1]));

        // // Mutation Execution
        // System.out.println(" Mutation Applied to both childrens");
        // System.out.println("Original Son: " + Arrays.toString(offspring[0]));
        // System.out.println("Original Daughter: " + Arrays.toString(offspring[1]));
        // int[] mutatedSon = applyMutation(mediator, offspring[0], mutationMethod,
        // array_size);
        // int[] mutatedDaughter = applyMutation(mediator, offspring[1], mutationMethod,
        // array_size);
        // System.out.println("Mutated Son: " + Arrays.toString(mutatedSon));
        // System.out.println("Mutated Daughter: " + Arrays.toString(mutatedDaughter));

        // // Evaluate mutated Son & Daughter
        // System.out.println("\n--- Mutated Son Evaluation ---");

    }

    // List of Selection methods
    private static int[][] applySelection(Mediator mediator, int[][] contracts, double[] fitness, String method,
            int selection_size, int mue, int lambda, double temparature) {
        return switch (method) {
            case "RANK" -> mediator.rank_selection(contracts, selection_size);
            case "MUE_LAMBDA" -> mediator.mue_lamba_selection(contracts, selection_size, selection_size);
            case "TEMPERATURE" -> mediator.temperature_based_selection(contracts, fitness, selection_size, temparature);
            default -> throw new IllegalStateException("Unknown selection: " + method);
        };
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