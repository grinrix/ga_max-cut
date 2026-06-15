package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Logger zbierający i zapisujący do pliku CSV dane o przebiegu algorytmu genetycznego.
 *
 * Dla każdego punktu ewaluacji (#ev) rejestruje trzy uśrednione wartości fitness
 * ze wszystkich uruchomień (#runs):
 * <ul>
 *   <li><b>Current</b>     — średnia wartość FP nowo tworzonych osobników w danym kroku.</li>
 *   <li><b>Best-current</b> — wartość FP najlepszego osobnika w bieżącej populacji.</li>
 *   <li><b>Best-global</b>  — wartość FP najlepszego osobnika znalezionego do tej chwili.</li>
 * </ul>
 *
 * Format wyjściowy: plik CSV z nagłówkiem, jeden wiersz na punkt ewaluacji.
 * Wynikowy plik służy do budowy wykresów zbieżności algorytmu.
 */
public class ResultsLogger {

    /** Strumień zapisu do pliku CSV. */
    private PrintWriter writer;

    /** Ścieżka do aktualnie otwartego pliku — do wyświetlenia w logach. */
    private String currentFilename;

    /**
     * Otwiera plik CSV i zapisuje nagłówek kolumn.
     *
     * Jeśli plik już istnieje, zostanie nadpisany. Separatorem kolumn jest
     * średnik (zgodnie z polskimi ustawieniami Excela).
     *
     * @param filename ścieżka do pliku wynikowego CSV
     * @throws IOException gdy nie można otworzyć pliku do zapisu
     */
    public void initFile(String filename) throws IOException {
        this.currentFilename = filename;
        // Otwieramy plik z flagą append=false — każde initFile() zaczyna nowy plik
        this.writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, false)));
        // Nagłówek CSV (separator: średnik, kompatybilny z Excel/polskim locale)
        writer.println("#ev;Current;Best-current;Best-global");
    }

    /**
     * Zapisuje jeden wiersz danych dla danego punktu ewaluacji.
     *
     * Wszystkie trzy wartości powinny być już uśrednione z {@code #runs} uruchomień
     * przez wywołującą klasę ({@link experiments.ExperimentRunner}).
     *
     * @param evaluationCount    numer aktualnej ewaluacji (#ev)
     * @param currentAvg         średnia wartość FP nowo tworzonych osobników
     * @param bestCurrentAvg     wartość FP najlepszego osobnika w bieżącej populacji
     * @param bestGlobalAvg      wartość FP najlepszego osobnika globalnie (od startu)
     */
    public void logPoint(int evaluationCount, double currentAvg,
                         double bestCurrentAvg, double bestGlobalAvg) {
        // Wartości zmiennoprzecinkowe z kropką jako separatorem dziesiętnym
        writer.printf("%d;%.4f;%.4f;%.4f%n",
                evaluationCount, currentAvg, bestCurrentAvg, bestGlobalAvg);
    }

    /**
     * Opróżnia bufor i zamyka plik CSV.
     *
     * Należy wywołać po zakończeniu eksperymentu — inaczej ostatnie dane
     * mogą nie zostać zapisane na dysk.
     */
    public void closeFile() {
        if (writer != null) {
            writer.flush();
            writer.close();
            writer = null;
        }
    }
}
