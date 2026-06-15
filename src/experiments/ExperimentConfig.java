package experiments;

public class ExperimentConfig {

    // Pola oznaczona jako 'final' stają się stałymi po zainicjalizowaniu w konstruktorze
    private final int populationSize;
    private final double pc;          // prawdopodobieństwo krzyżowania
    private final double pm;          // prawdopodobieństwo mutacji
    private final int evMax;          // maksymalna liczba ewaluacji
    private final int runs;           // liczba uruchomień algorytmu

    // Konstruktor do ustawiania wszystkich parametrów eksperymentu
    public ExperimentConfig(int populationSize, double pc, double pm, int evMax, int runs) {
        this.populationSize = populationSize;
        this.pc = pc;
        this.pm = pm;
        this.evMax = evMax;
        this.runs = runs;
    }

    // --- Metody dostępowe (Gettery) ---

    public int getPopulationSize() {
        return populationSize;
    }

    public double getPc() {
        return pc;
    }

    public double getPm() {
        return pm;
    }

    public int getEvMax() {
        return evMax;
    }

    public int getRuns() {
        return runs;
    }
}