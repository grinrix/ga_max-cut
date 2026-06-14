package core;

public class Evaluator {
    // Licznik ewaluacji potrzebny Osobie 3 do kontroli parametru #ev_max
    private int evaluationCount = 0;

    public void evaluate(Individual individual, Graph graph) {
        double totalCutWeight = 0.0;
        int[] genes = individual.getChromosome();

        // Iterujemy po wszystkich krawędziach grafu
        for (Edge edge : graph.getEdges()) {
            // Jeśli końce krawędzi mają różne bity, oznacza to, że krawędź leży w przekroju
            if (genes[edge.u] != genes[edge.v]) {
                totalCutWeight += edge.weight;
            }
        }

        individual.setFitness(totalCutWeight);
        evaluationCount++;
    }

    public int getEvaluationCount() { return evaluationCount; }
    public void resetCounter() { this.evaluationCount = 0; }
}