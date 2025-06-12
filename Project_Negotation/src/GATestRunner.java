import java.io.FileWriter;
import java.io.IOException;

public class GATestRunner {

    public static void main(String[] args) {
        String[] selectionMethods = {"RANK", "TOURNAMENT", "TEMPERATURE"};
        String[] crossoverMethods = {"ORDER", "POSITION_BASED", "CYCLE"};
        String[] mutationMethods = {"SCRAMBLE", "INVERSE", "DISPLACEMENT"};

        int combinationId = 1;

        try (FileWriter writer = new FileWriter("all_results.csv")) {
            writer.write("Combination,Selection,Crossover,Mutation,Round,Cost_AgentA,Cost_AgentB\n");

            for (String selection : selectionMethods) {
                for (String crossover : crossoverMethods) {
                    for (String mutation : mutationMethods) {

                        System.out.println("🔁 Running combination #" + combinationId + ": "
                                + selection + " / " + crossover + " / " + mutation);

                        try {
                            double[][] result = TestGA.runExperiment(selection, crossover, mutation);
                            System.out.println("We are out from combination #" + combinationId + ": ");

                            for (int round = 0; round < result.length; round++) {
                                writer.write(combinationId + "," +
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

            System.out.println("✅ All 27 combinations completed. Results saved to all_results.csv");

        } catch (IOException e) {
            System.err.println("❌ Failed to write results: " + e.getMessage());
        }
    }
}
