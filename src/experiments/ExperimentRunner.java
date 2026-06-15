package experiments;

import core.Evaluator;
import core.Graph;
import core.Individual;
import ga.GeneticEngine;
import io.ResultsLogger;
import io.SolutionWriter;

import java.io.IOException;

/**
 * Zarządza wielokrotnym uruchamianiem algorytmu genetycznego i uśrednianiem wyników.
 *
 * Dla każdej konfiguracji parametrów ({@link ExperimentConfig}) wykonuje
 * {@code #runs} niezależnych uruchomień GA, a następnie:
 * <ul>
 *   <li>Uśrednia wartości {@code current}, {@code bestCurrent} i {@code bestGlobal}
 *       dla każdego punktu ewaluacji (#ev) po wszystkich uruchomieniach.</li>
 *   <li>Zapisuje uśrednione wyniki do pliku CSV za pomocą {@link ResultsLogger}.</li>
 *   <li>Zapisuje najlepsze znalezione rozwiązanie do pliku tekstowego
 *       za pomocą {@link SolutionWriter}.</li>
 * </ul>
 *
 * Plik CSV służy jako źródło danych do wykresów zbieżności algorytmu.
 */
public class ExperimentRunner {

    /** Graf wejściowy przekazywany do każdego uruchomienia GA. */
    private final Graph graph;

    /**
     * Tworzy ExperimentRunner dla danego grafu.
     *
     * @param graph graf wejściowy (problem Max-Cut)
     */
    public ExperimentRunner(Graph graph) {
        this.graph = graph;
    }

    /**
     * Uruchamia eksperyment dla podanej konfiguracji i zapisuje wyniki.
     *
     * Wykonuje {@code config.getRuns()} niezależnych uruchomień algorytmu GA,
     * uśrednia trzy krzywe zbieżności (current, bestCurrent, bestGlobal)
     * dla każdego punktu ewaluacji i zapisuje je do pliku CSV.
     * Dodatkowo zapisuje najlepsze znalezione rozwiązanie do pliku tekstowego.
     *
     * @param config       konfiguracja parametrów algorytmu
     * @param csvFilename  ścieżka do wyjściowego pliku CSV z danymi do wykresów
     * @param solFilename  ścieżka do wyjściowego pliku tekstowego z najlepszym rozwiązaniem
     * @throws IOException gdy nie można otworzyć pliku wynikowego
     */
    public void run(ExperimentConfig config, String csvFilename, String solFilename)
            throws IOException {

        int evMax = config.getEvMax();
        int runs  = config.getRuns();

        // Tablice akumulatorów do uśredniania po #runs uruchomieniach
        double[] sumCurrent     = new double[evMax];
        double[] sumBestCurrent = new double[evMax];
        double[] sumBestGlobal  = new double[evMax];

        // Globalne najlepsze rozwiązanie ze wszystkich uruchomień
        Individual overallBest = null;

        System.out.printf("  Konfiguracja: xP=%d, pc=%.2f, pm=%.4f, selekcja=%s%n",
                config.getPopulationSize(), config.getPc(), config.getPm(),
                config.isUseTournamentSelection() ? "turniejowa (k=" + config.getTournamentSize() + ")" : "ruletka");

        // ── Pętla #runs uruchomień ────────────────────────────────────────────
        for (int run = 0; run < runs; run++) {
            Evaluator evaluator = new Evaluator();

            // Tworzymy nowy silnik GA dla każdego uruchomienia (różne ziarna losowości)
            GeneticEngine engine = new GeneticEngine(
                    graph, evaluator,
                    config.getPopulationSize(),
                    config.getPc(),
                    config.getPm(),
                    evMax,
                    config.getTournamentSize(),
                    config.isUseTournamentSelection()
            );

            engine.run();

            // Akumulujemy wyniki z tego uruchomienia
            double[] cur  = engine.getCurrentHistory();
            double[] bc   = engine.getBestCurrentHistory();
            double[] bg   = engine.getBestGlobalHistory();

            for (int ev = 0; ev < evMax; ev++) {
                sumCurrent[ev]     += cur[ev];
                sumBestCurrent[ev] += bc[ev];
                sumBestGlobal[ev]  += bg[ev];
            }

            // Aktualizacja globalnego najlepszego rozwiązania
            Individual runBest = engine.getBestSolution();
            if (overallBest == null || (runBest != null && runBest.getFitness() > overallBest.getFitness())) {
                overallBest = runBest;
            }

            System.out.printf("    Uruchomienie %d/%d — best fitness: %.2f%n",
                    run + 1, runs,
                    runBest != null ? runBest.getFitness() : 0.0);
        }

        // ── Zapis uśrednionych danych do CSV ─────────────────────────────────
        ResultsLogger logger = new ResultsLogger();
        logger.initFile(csvFilename);

        for (int ev = 0; ev < evMax; ev++) {
            logger.logPoint(
                    ev + 1,                          // numer ewaluacji (1-based)
                    sumCurrent[ev]     / runs,       // uśredniony current
                    sumBestCurrent[ev] / runs,       // uśredniony bestCurrent
                    sumBestGlobal[ev]  / runs        // uśredniony bestGlobal
            );
        }

        logger.closeFile();
        System.out.printf("  Dane do wykresów zapisane: %s%n", csvFilename);

        // ── Zapis najlepszego rozwiązania do pliku tekstowego ─────────────────
        if (overallBest != null) {
            SolutionWriter.write(overallBest, graph, solFilename);
            System.out.printf("  Najlepsze rozwiązanie (fitness=%.2f) zapisane: %s%n",
                    overallBest.getFitness(), solFilename);
        }
    }
}
