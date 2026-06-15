package experiments;

/**
 * Niezmienialny obiekt przechowujący pełną konfigurację jednego eksperymentu GA.
 *
 * Zawiera wszystkie parametry robocze algorytmu potrzebne do jednorazowego
 * uruchomienia {@link ExperimentRunner}: rozmiar populacji, prawdopodobieństwa
 * operatorów, budżet ewaluacji, liczbę uruchomień i ustawienia selekcji.
 *
 * Użycie konstruktora fluent-builder ({@link Builder}) pozwala tworzyć konfiguracje
 * czytelnie, bez ryzyka pomyłki kolejności argumentów.
 */
public class ExperimentConfig {

    /** Rozmiar populacji (xP). */
    private final int populationSize;

    /** Prawdopodobieństwo krzyżowania (pc). */
    private final double pc;

    /** Prawdopodobieństwo mutacji pojedynczego bitu (pm). */
    private final double pm;

    /** Maksymalna liczba ewaluacji funkcji przystosowania (#ev_max). */
    private final int evMax;

    /** Liczba niezależnych uruchomień algorytmu do uśrednienia wyników (#runs). */
    private final int runs;

    /** Rozmiar turnieju przy selekcji turniejowej (domyślnie 2). */
    private final int tournamentSize;

    /** Tryb selekcji: true = turniejowa, false = kołem ruletki. */
    private final boolean useTournamentSelection;

    /**
     * Główny konstruktor przyjmujący wszystkie parametry naraz.
     *
     * @param populationSize         rozmiar populacji
     * @param pc                     prawdopodobieństwo krzyżowania
     * @param pm                     prawdopodobieństwo mutacji bitu
     * @param evMax                  budżet ewaluacji
     * @param runs                   liczba uruchomień
     * @param tournamentSize         rozmiar turnieju
     * @param useTournamentSelection tryb selekcji
     */
    public ExperimentConfig(int populationSize, double pc, double pm,
                            int evMax, int runs,
                            int tournamentSize, boolean useTournamentSelection) {
        this.populationSize = populationSize;
        this.pc = pc;
        this.pm = pm;
        this.evMax = evMax;
        this.runs = runs;
        this.tournamentSize = tournamentSize;
        this.useTournamentSelection = useTournamentSelection;
    }

    /** Zwraca rozmiar populacji (xP). */
    public int getPopulationSize()         { return populationSize; }

    /** Zwraca prawdopodobieństwo krzyżowania (pc). */
    public double getPc()                  { return pc; }

    /** Zwraca prawdopodobieństwo mutacji bitu (pm). */
    public double getPm()                  { return pm; }

    /** Zwraca budżet ewaluacji (#ev_max). */
    public int getEvMax()                  { return evMax; }

    /** Zwraca liczbę niezależnych uruchomień (#runs). */
    public int getRuns()                   { return runs; }

    /** Zwraca rozmiar turnieju selekcji turniejowej. */
    public int getTournamentSize()         { return tournamentSize; }

    /** Zwraca true jeśli używana jest selekcja turniejowa, false dla ruletki. */
    public boolean isUseTournamentSelection() { return useTournamentSelection; }

    /**
     * Zwraca czytelną reprezentację konfiguracji do logów i nazw plików.
     *
     * @return string w formacie "xP{rozmiar}_pc{pc}_pm{pm}_{selekcja}"
     */
    @Override
    public String toString() {
        String sel = useTournamentSelection ? "tour" + tournamentSize : "roulette";
        return String.format("xP%d_pc%.2f_pm%.4f_%s", populationSize, pc, pm, sel);
    }
}
