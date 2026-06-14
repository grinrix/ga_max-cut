package io;

public class ResultsLogger {

    public void initFile(String filename) {
        // TODO: Utworzyć plik CSV z odpowiednimi nagłówkami (np. Iteracja/Ewaluacja, Current, Best-Current)
    }

    public void logGeneration(int evaluationCount, double currentAvgFitness, double bestCurrentFitness) {
        // TODO: Zaimplementować dopisywanie wiersza z uśrednionymi z '#runs' wynikami do pliku CSV.
        // potrzebne wartości Current i Best-current.
    }

    public void closeFile() {
        // TODO: Zamknąć strumień zapisu do pliku.
    }
}