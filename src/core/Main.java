package core;

import experiments.ExperimentConfig;
import experiments.ExperimentRunner;
import io.RudyGraphReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Punkt wejściowy programu — algorytm genetyczny rozwiązujący problem Max-Cut.
 *
 * Program przyjmuje ścieżkę do pliku instancji jako argument wiersza poleceń
 * (format Rudy / Biq Mac Library). Jeśli argument nie zostanie podany, używana
 * jest domyślna ścieżka do pliku testowego.
 *
 * Uruchamiane są eksperymenty badające wpływ czterech parametrów roboczych:
 * <ol>
 *   <li>Rozmiaru populacji (xP) — przy stałych pozostałych parametrach.</li>
 *   <li>Prawdopodobieństwa krzyżowania (pc).</li>
 *   <li>Prawdopodobieństwa mutacji (pm).</li>
 *   <li>Metody selekcji (turniejowa vs kołem ruletki).</li>
 * </ol>
 *
 * Dla każdego eksperymentu generowany jest plik CSV z danymi do wykresów
 * oraz plik tekstowy z najlepszym znalezionym rozwiązaniem.
 *
 * Pliki wynikowe trafiają do katalogu {@code data/output/}.
 *
 * Użycie:
 * <pre>
 *   java core.Main [ścieżka_do_pliku_instancji]
 * </pre>
 */
public class Main {

    // ── Stałe domyślnych parametrów bazowych ─────────────────────────────────

    /** Domyślny rozmiar populacji używany jako baza we wszystkich eksperymentach. */
    private static final int BASE_POP_SIZE    = 100;

    /** Domyślne prawdopodobieństwo krzyżowania. */
    private static final double BASE_PC       = 0.8;

    /** Domyślne prawdopodobieństwo mutacji (1/n jest typową wartością dla binarnego GA). */
    private static final double BASE_PM       = 0.01;

    /** Budżet ewaluacji (#ev_max) — taki sam we wszystkich eksperymentach. */
    private static final int EV_MAX           = 10_000;

    /** Liczba niezależnych uruchomień do uśrednienia (#runs). */
    private static final int RUNS             = 10;

    /** Domyślny rozmiar turnieju dla selekcji turniejowej. */
    private static final int TOURNAMENT_SIZE  = 3;

    public static void main(String[] args) {

        // Ścieżka do pliku instancji — z argumentu lub domyślna
        String inputFile = (args.length > 0) ? args[0] :  "data/input/rudy_all/g05_60.0";
        // Katalog wyjściowy — tworzymy jeśli nie istnieje
        String outputDir = "data/output/";
        try {
            Files.createDirectories(Paths.get(outputDir));
        } catch (IOException e) {
            System.err.println("Ostrzeżenie: nie można utworzyć katalogu wyjściowego: " + e.getMessage());
        }

        // Nazwa bazowa pliku (bez ścieżki i rozszerzenia) do nazewnictwa wyników
        String baseName = Paths.get(inputFile).getFileName().toString()
                .replaceAll("\\.[^.]+$", "");

        System.out.println("════════════════════════════════════════════════════");
        System.out.println(" GA Max-Cut — algorytm genetyczny");
        System.out.println("════════════════════════════════════════════════════");
        System.out.println("Plik instancji : " + inputFile);
        System.out.println("ev_max         : " + EV_MAX);
        System.out.println("#runs          : " + RUNS);
        System.out.println();

        // ── Wczytanie grafu ───────────────────────────────────────────────────
        Graph graph;
        try {
            RudyGraphReader reader = new RudyGraphReader();
            graph = reader.readGraph(inputFile);
            System.out.println("Wczytano graf: " + graph.getVerticesCount()
                    + " wierzchołków, " + graph.getEdges().size() + " krawędzi.");
        } catch (IOException e) {
            System.err.println("BŁĄD: Nie można wczytać pliku instancji: " + inputFile);
            System.err.println("Szczegóły: " + e.getMessage());
            return;
        }

        ExperimentRunner runner = new ExperimentRunner(graph);

        // ── Eksperyment 1: Wpływ rozmiaru populacji (xP) ─────────────────────
        System.out.println("\n▶ Eksperyment 1: Wpływ rozmiaru populacji (xP)");
        int[] popSizes = {20, 50, 100, 200, 500};
        for (int xP : popSizes) {
            ExperimentConfig cfg = new ExperimentConfig(
                    xP, BASE_PC, BASE_PM, EV_MAX, RUNS, TOURNAMENT_SIZE, true);
            String tag = baseName + "_exp1_" + cfg;
            try {
                runner.run(cfg,
                        outputDir + tag + ".csv",
                        outputDir + tag + "_solution.txt");
            } catch (IOException e) {
                System.err.println("  Błąd zapisu: " + e.getMessage());
            }
        }

        // ── Eksperyment 2: Wpływ prawdopodobieństwa krzyżowania (pc) ─────────
        System.out.println("\n▶ Eksperyment 2: Wpływ prawdopodobieństwa krzyżowania (pc)");
        double[] pcValues = {0.5, 0.7, 0.9};
        for (double pc : pcValues) {
            ExperimentConfig cfg = new ExperimentConfig(
                    BASE_POP_SIZE, pc, BASE_PM, EV_MAX, RUNS, TOURNAMENT_SIZE, true);
            String tag = baseName + "_exp2_" + cfg;
            try {
                runner.run(cfg,
                        outputDir + tag + ".csv",
                        outputDir + tag + "_solution.txt");
            } catch (IOException e) {
                System.err.println("  Błąd zapisu: " + e.getMessage());
            }
        }

        // ── Eksperyment 3: Wpływ prawdopodobieństwa mutacji (pm) ─────────────
        System.out.println("\n▶ Eksperyment 3: Wpływ prawdopodobieństwa mutacji (pm)");
        double[] pmValues = {0.001, 0.01, 0.05};
        for (double pm : pmValues) {
            ExperimentConfig cfg = new ExperimentConfig(
                    BASE_POP_SIZE, BASE_PC, pm, EV_MAX, RUNS, TOURNAMENT_SIZE, true);
            String tag = baseName + "_exp3_" + cfg;
            try {
                runner.run(cfg,
                        outputDir + tag + ".csv",
                        outputDir + tag + "_solution.txt");
            } catch (IOException e) {
                System.err.println("  Błąd zapisu: " + e.getMessage());
            }
        }

        // ── Eksperyment 4: Porównanie metod selekcji ─────────────────────────
        System.out.println("\n▶ Eksperyment 4: Porównanie metod selekcji");

        // Selekcja turniejowa (k=2)
        ExperimentConfig cfgT2 = new ExperimentConfig(
                BASE_POP_SIZE, BASE_PC, BASE_PM, EV_MAX, RUNS, 2, true);
        // Selekcja turniejowa (k=5)
        ExperimentConfig cfgT5 = new ExperimentConfig(
                BASE_POP_SIZE, BASE_PC, BASE_PM, EV_MAX, RUNS, 5, true);
        // Selekcja kołem ruletki
        ExperimentConfig cfgR = new ExperimentConfig(
                BASE_POP_SIZE, BASE_PC, BASE_PM, EV_MAX, RUNS, 0, false);

        for (ExperimentConfig cfg : new ExperimentConfig[]{cfgT2, cfgT5, cfgR}) {
            String tag = baseName + "_exp4_" + cfg;
            try {
                runner.run(cfg,
                        outputDir + tag + ".csv",
                        outputDir + tag + "_solution.txt");
            } catch (IOException e) {
                System.err.println("  Błąd zapisu: " + e.getMessage());
            }
        }

        System.out.println("\n════════════════════════════════════════════════════");
        System.out.println(" Wszystkie eksperymenty zakończone.");
        System.out.println(" Pliki wynikowe zapisane w: " + outputDir);
        System.out.println("════════════════════════════════════════════════════");
    }
}
