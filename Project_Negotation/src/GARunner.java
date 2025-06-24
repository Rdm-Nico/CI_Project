import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GARunner {

    public static void main(String[] args) {
        String[] supplierFiles = {
                "Project_Negotation/data/daten3ASupplier_200.txt",
                "Project_Negotation/data/daten3BSupplier_200.txt",
                "Project_Negotation/data/daten4ASupplier_200.txt",
                "Project_Negotation/data/daten4BSupplier_200.txt"
        };

        String[] customerFiles = {
                "Project_Negotation/data/daten3ACustomer_200_10.txt",
                "Project_Negotation/data/daten3BCustomer_200_20.txt",
                "Project_Negotation/data/daten4ACustomer_200_5.txt",
                "Project_Negotation/data/daten4BCustomer_200_5.txt"
        };

        String[] selectionMethods = { "RANK", "TOURNAMENT", "TEMPERATURE" };
        String[] crossoverMethods = { "ORDER", "CYCLE", "POSITION_BASED" };
        String[] mutationMethods = { "INVERSE", "SCRAMBLE", "DISPLACEMENT" };

        String outputFile = "all_results.csv";


        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("Combination,SupplierFile,CustomerFile,Selection,Crossover,Mutation,Round,Cost_AgentA,Cost_AgentB\n");

            for (String supplierPath : supplierFiles) {
                for (String customerPath : customerFiles) {
                    String supplierLabel = getBaseName(supplierPath);
                    String customerLabel = getBaseName(customerPath);

                    int combinationId = 1; // Reset combination for each set of inputs

                    for (String selection : selectionMethods) {
                        for (String crossover : crossoverMethods) {
                            for (String mutation : mutationMethods) {
                                System.out.println("🔁 Running combination #" + combinationId + ": "
                                        + supplierLabel + " / " + customerLabel + " / "
                                        + selection + " / " + crossover + " / " + mutation);
                                try {
                                    double[][] result = GenericAlgorithm.runExperiment(selection, crossover, mutation,
                                            supplierPath, customerPath);

                                    for (int round = 0; round < result.length; round++) {
                                        writer.write(combinationId + "," +
                                                supplierLabel + "," +
                                                customerLabel + "," +
                                                selection + "," +
                                                crossover + "," +
                                                mutation + "," +
                                                round + "," +
                                                result[round][0] + "," +
                                                result[round][1] + "\n");
                                    }
                                } catch (Exception e) {
                                    System.err.println("❌ Error on combination #" + combinationId + ": " + e.getMessage());
                                    e.printStackTrace();
                                }

                                combinationId++;
                            }
                        }
                    }
                }
            }

            System.out.println("\n✅ All combinations completed. Results saved to: " + outputFile);
        } catch (IOException e) {
            System.err.println("❌ Failed to write results: " + e.getMessage());
        }

        // ✅ Post-analysis: best result per supplier-customer pair
        String bestPairsFile = "best_per_pair.csv";

        try (
                BufferedReader br = new BufferedReader(new FileReader(outputFile));
                FileWriter writer = new FileWriter(bestPairsFile)
        ) {
            String line;
            br.readLine(); // Skip header

            Map<String, String> bestLines = new HashMap<>();
            Map<String, Double> bestCosts = new HashMap<>();

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 9) continue;

                String supplier = parts[1];
                String customer = parts[2];
                double costA = Double.parseDouble(parts[7]);
                double costB = Double.parseDouble(parts[8]);
                double totalCost = costA + costB;

                String key = supplier + "|" + customer;

                if (!bestCosts.containsKey(key) || totalCost < bestCosts.get(key)) {
                    bestLines.put(key, line);
                    bestCosts.put(key, totalCost);
                }
            }

            writer.write("Combination,SupplierFile,CustomerFile,Selection,Crossover,Mutation,Round,Cost_AgentA,Cost_AgentB,TotalCost\n");

            System.out.println("\n⭐ Best result for each supplier-customer pair:");
            for (String key : bestLines.keySet()) {
                String bestLine = bestLines.get(key);
                double totalCost = bestCosts.get(key);

                System.out.println(bestLine + "," + totalCost);
                writer.write(bestLine + "," + totalCost + "\n");
            }

            System.out.println("\n✅ Best results per pair saved to: " + bestPairsFile);
        } catch (IOException e) {
            System.err.println("❌ Failed to analyze results: " + e.getMessage());
        }
    }

    private static String getBaseName(String path) {
        String fileName = new File(path).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex > 0) ? fileName.substring(0, dotIndex) : fileName;
    }
}
