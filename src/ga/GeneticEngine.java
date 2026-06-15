package ga;

import core.Evaluator;
import core.Graph;
import core.Individual;
import java.util.Random;

// Klasy tego samego pakietu (ga.*) nie wymagają importu w Javie,
// ale dla czytelności zostawiamy komentarz o ich użyciu:
// Population, Selection, Crossover, Mutation — wszystkie z pakietu ga

/**
 * Silnik algorytmu genetycznego rozwiązującego problem Max-Cut.
 *
 * Implementuje pętlę ewolucyjną zgodną ze schematem:
 * <ol>
 *   <li>Inicjalizacja losowej populacji startowej.</li>
 *   <li>Ewaluacja wszystkich osobników.</li>
 *   <li>Główna pętla (do wyczerpania budżetu #ev_max):
 *     <ul>
 *       <li>Selekcja rodziców (turniejowa lub kołem ruletki).</li>
 *       <li>Krzyżowanie jednorodne (uniform crossover) z prawdopodobieństwem pc.</li>
 *       <li>Mutacja bit-flip z prawdopodobieństwem pm.</li>
 *       <li>Ewaluacja potomków.</li>
 *       <li>Sukcesja generacyjna z elityzmem (najlepszy osobnik zawsze przechodzi).</li>
 *     </ul>
 *   </li>
 * </ol>
 *
 * Klasa zbiera trzy serie danych do analizy zbieżności:
 * {@code current}, {@code bestCurrent} i {@code bestGlobal}.
 */
public class GeneticEngine {

    // ── Graf i ewaluator ──────────────────────────────────────────────────────
    private final Graph graph;
    private final Evaluator evaluator;
    private final Random rand;

    // ── Operatory GA ──────────────────────────────────────────────────────────
    private final Selection selection;
    private final Crossover crossover;
    private final Mutation mutation;

    // ── Parametry algorytmu ───────────────────────────────────────────────────

    /** Rozmiar populacji (xP). */
    private final int populationSize;

    /** Prawdopodobieństwo krzyżowania dwóch rodziców. */
    private final double crossoverProbability;

    /** Prawdopodobieństwo mutacji jednego bitu chromosomu. */
    private final double mutationProbability;

    /** Maksymalna liczba ewaluacji funkcji przystosowania (#ev_max). */
    private final int maxEvaluations;

    /**
     * Rozmiar turnieju selekcji turniejowej.
     * Wartość 2 (domyślna) oznacza minimalną presję selekcyjną.
     */
    private final int tournamentSize;

    /** Tryb selekcji: true = turniejowa, false = kołem ruletki. */
    private final boolean useTournamentSelection;

    // ── Wyniki do zebrania przez ExperimentRunner ─────────────────────────────

    /**
     * Tablica skumulowanych wartości {@code current} (fitness nowo tworzonych osobników)
     * indeksowana numerem ewaluacji. Używana do uśredniania po #runs przez ExperimentRunner.
     */
    private double[] currentHistory;

    /**
     * Tablica skumulowanych wartości {@code bestCurrent} (najlepszy w populacji)
     * indeksowana numerem ewaluacji.
     */
    private double[] bestCurrentHistory;

    /**
     * Tablica skumulowanych wartości {@code bestGlobal} (najlepszy od startu algorytmu)
     * indeksowana numerem ewaluacji.
     */
    private double[] bestGlobalHistory;

    /** Najlepszy osobnik znaleziony w ostatnim uruchomieniu run(). */
    private Individual bestSolution;

    /**
     * Tworzy silnik GA z pełną konfiguracją parametrów.
     *
     * @param graph                  graf wejściowy (problem Max-Cut)
     * @param evaluator              ewaluator funkcji przystosowania
     * @param populationSize         rozmiar populacji (xP)
     * @param crossoverProbability   prawdopodobieństwo krzyżowania (pc)
     * @param mutationProbability    prawdopodobieństwo mutacji bitu (pm)
     * @param maxEvaluations         budżet ewaluacji (#ev_max)
     * @param tournamentSize         rozmiar turnieju (jeśli selekcja turniejowa)
     * @param useTournamentSelection true = turniejowa, false = kołem ruletki
     */
    public GeneticEngine(Graph graph, Evaluator evaluator,
                         int populationSize, double crossoverProbability,
                         double mutationProbability, int maxEvaluations,
                         int tournamentSize, boolean useTournamentSelection) {
        this.graph = graph;
        this.evaluator = evaluator;
        this.populationSize = populationSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        this.maxEvaluations = maxEvaluations;
        this.tournamentSize = tournamentSize;
        this.useTournamentSelection = useTournamentSelection;
        this.rand = new Random();
        this.selection = new Selection();
        this.crossover = new Crossover();
        this.mutation = new Mutation();
    }

    /**
     * Uruchamia jedno pełne przebieg algorytmu genetycznego.
     *
     * Resetuje ewaluator i historię, inicjalizuje populację, a następnie
     * wykonuje pętlę ewolucyjną do wyczerpania budżetu {@code maxEvaluations}.
     * Po zakończeniu wyniki są dostępne przez {@link #getCurrentHistory()},
     * {@link #getBestCurrentHistory()}, {@link #getBestGlobalHistory()}
     * i {@link #getBestSolution()}.
     */
    public void run() {
        evaluator.resetCounter();

        // Przygotowanie tablic historii (rozmiar = #ev_max)
        currentHistory     = new double[maxEvaluations];
        bestCurrentHistory = new double[maxEvaluations];
        bestGlobalHistory  = new double[maxEvaluations];

        // ── 1. Inicjalizacja populacji startowej ──────────────────────────────
        Population population = new Population(populationSize);
        for (int i = 0; i < populationSize; i++) {
            Individual ind = new Individual(graph.getVerticesCount(), rand);
            population.setIndividual(i, ind);
        }

        // ── 2. Ewaluacja populacji startowej ─────────────────────────────────
        for (int i = 0; i < populationSize; i++) {
            evaluator.evaluate(population.getIndividual(i), graph);
            recordHistory(population, population.getIndividual(i));
            if (evaluator.getEvaluationCount() >= maxEvaluations) break;
        }

        // Inicjalizacja najlepszego globalnego osobnika po populacji startowej
        bestSolution = population.getBestIndividual().copy();

        // ── 3. Główna pętla ewolucyjna ────────────────────────────────────────
        while (evaluator.getEvaluationCount() < maxEvaluations) {

            // Nowa populacja potomków — wypełniana parami
            Population newPopulation = new Population(populationSize);
            int filled = 0;

            // Elityzm: najlepszy osobnik przechodzi do następnej generacji bez zmian
            newPopulation.setIndividual(filled++, population.getBestIndividual().copy());

            // Wypełniamy pozostałe miejsca potomkami
            while (filled < populationSize) {
                // Selekcja dwóch rodziców
                Individual parent1 = selectParent(population);
                Individual parent2 = selectParent(population);

                // Krzyżowanie jednorodne z prawdopodobieństwem pc
                Individual[] offspring = crossover.uniformCrossover(
                        parent1, parent2, crossoverProbability);

                // Mutacja bit-flip z prawdopodobieństwem pm na gen
                mutation.bitFlipMutation(offspring[0], mutationProbability);
                mutation.bitFlipMutation(offspring[1], mutationProbability);

                // Ewaluacja potomka 1
                evaluator.evaluate(offspring[0], graph);
                recordHistory(population, offspring[0]);
                newPopulation.setIndividual(filled++, offspring[0]);
                if (filled >= populationSize || evaluator.getEvaluationCount() >= maxEvaluations) break;

                // Ewaluacja potomka 2
                evaluator.evaluate(offspring[1], graph);
                recordHistory(population, offspring[1]);
                newPopulation.setIndividual(filled++, offspring[1]);
                if (evaluator.getEvaluationCount() >= maxEvaluations) break;
            }

            // Sukcesja generacyjna: nowa populacja zastępuje starą
            population = newPopulation;

            // Aktualizacja globalnego najlepszego osobnika
            Individual currentBest = population.getBestIndividual();
            if (currentBest != null && currentBest.getFitness() > bestSolution.getFitness()) {
                bestSolution = currentBest.copy();
            }
        }
    }

    /**
     * Wybiera jednego rodzica metodą selekcji skonfigurowaną w konstruktorze.
     *
     * @param population bieżąca populacja
     * @return           wybrany osobnik-rodzic
     */
    private Individual selectParent(Population population) {
        if (useTournamentSelection) {
            return selection.tournamentSelection(population, tournamentSize);
        } else {
            return selection.rouletteWheelSelection(population);
        }
    }

    /**
     * Rejestruje wartości historii dla bieżącego punktu ewaluacji.
     *
     * Zapisuje fitness nowo ocenianego osobnika (current), fitness najlepszego
     * w populacji (bestCurrent) oraz fitness globalnego najlepszego (bestGlobal)
     * pod indeksem odpowiadającym numerowi ewaluacji.
     *
     * @param population    bieżąca populacja (do odczytu bestCurrent)
     * @param newIndividual nowo oceniony osobnik (current)
     */
    private void recordHistory(Population population, Individual newIndividual) {
        int ev = evaluator.getEvaluationCount() - 1; // indeks od 0
        if (ev < 0 || ev >= maxEvaluations) return;

        // Current: fitness właśnie ocenionego osobnika
        currentHistory[ev] = newIndividual.getFitness();

        // Best-current: fitness najlepszego osobnika w aktualnej populacji
        Individual popBest = population.getBestIndividual();
        bestCurrentHistory[ev] = (popBest != null) ? popBest.getFitness() : 0.0;

        // Best-global: fitness najlepszego osobnika od początku algorytmu
        double globalBestFitness = (bestSolution != null) ? bestSolution.getFitness() : 0.0;
        // Porównujemy z aktualnym bestCurrent na wypadek, że bestSolution jeszcze nie jest aktualne
        bestGlobalHistory[ev] = Math.max(globalBestFitness, bestCurrentHistory[ev]);
    }

    // ── Gettery wyników ───────────────────────────────────────────────────────

    /**
     * Zwraca tablicę wartości {@code current} (fitness nowo tworzonych osobników)
     * z ostatniego uruchomienia run(), indeksowaną numerem ewaluacji [0..#ev_max-1].
     */
    public double[] getCurrentHistory()     { return currentHistory; }

    /**
     * Zwraca tablicę wartości {@code bestCurrent} (najlepszy w populacji)
     * z ostatniego uruchomienia run().
     */
    public double[] getBestCurrentHistory() { return bestCurrentHistory; }

    /**
     * Zwraca tablicę wartości {@code bestGlobal} (najlepszy od startu)
     * z ostatniego uruchomienia run().
     */
    public double[] getBestGlobalHistory()  { return bestGlobalHistory; }

    /**
     * Zwraca najlepszego osobnika (rozwiązanie) znalezionego w ostatnim uruchomieniu run().
     */
    public Individual getBestSolution()     { return bestSolution; }
}
