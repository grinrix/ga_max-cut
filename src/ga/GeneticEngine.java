package ga;

import core.Graph;
import core.Individual;
import core.Evaluator;
import java.util.Random;

public class GeneticEngine {
    private final Graph graph;
    private final Evaluator evaluator;
    private final Random rand;

    // Parametry, do przekazywania w konfiguracji testów
    private final int populationSize; // xP
    private final double crossoverProbability; // pc
    private final double mutationProbability; // pm
    private final int maxEvaluations; // #ev_max

    public GeneticEngine(Graph graph, Evaluator evaluator, int populationSize,
                         double crossoverProbability, double mutationProbability, int maxEvaluations) {
        this.graph = graph;
        this.evaluator = evaluator;
        this.populationSize = populationSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.maxEvaluations = maxEvaluations;
        this.rand = new Random();
    }

    public void run() {
        evaluator.resetCounter();

        // 1. Inicjalizacja populacji (Wykorzystuje Twój konstruktor losowy)
        // CZĘŚĆ 2: Tutaj stwórz tablicę/listę Individual o rozmiarze populationSize

        // 2. Ocena populacji startowej
        // CZĘŚĆ 2: Wywołaj evaluator.evaluate() dla każdego osobnika

        // 3. Główna pętla algorytmu genetycznego
        while (evaluator.getEvaluationCount() < maxEvaluations) {

            // CZĘŚĆ 2: Selekcja (np. turniejowa lub koło ruletki)

            // CZĘŚĆ 2: Krzyżowanie jednorodne (Uniform Crossover) z prawd. crossoverProbability

            // CZĘŚĆ 2: Mutacja bit-flip z prawd. mutationProbability

            // CZĘŚĆ 2: Ewaluacja nowych osobników za pomocą evaluator.evaluate()

            // CZĘŚĆ 2: Sukcesja / Elityzm (zastąpienie populacji nową)

            // CZĘŚĆ 3: Tutaj wpięty zostanie Logger zbierający wartości 'Current' oraz 'Best-current'
            // dla bieżącego punktu ewaluacji w celu wygenerowania wykresów do sprawozdania.
        }
    }
}