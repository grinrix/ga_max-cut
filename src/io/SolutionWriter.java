package io;

import core.Graph;
import core.Individual;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Zapisuje najlepsze znalezione rozwiązanie problemu Max-Cut do pliku tekstowego.
 *
 * Format pliku wynikowego (zgodnie z wymaganiami zadania):
 * <pre>
 *   Waga przekroju (fitness): 123.45
 *
 *   Przypisanie wierzchołków:
 *   Wierzchołek 1 -> V1
 *   Wierzchołek 2 -> V2
 *   ...
 * </pre>
 *
 * Wierzchołki numerowane są od 1 (indeksowanie 1-based jak w plikach wejściowych Rudy).
 */
public class SolutionWriter {

    /**
     * Zapisuje rozwiązanie (podział wierzchołków V1/V2) do pliku tekstowego.
     *
     * @param individual najlepszy znaleziony osobnik (rozwiązanie)
     * @param graph      graf wejściowy (do wypisania liczby krawędzi i wierzchołków)
     * @param filename   ścieżka do pliku wynikowego
     * @throws IOException gdy nie można otworzyć pliku do zapisu
     */
    public static void write(Individual individual, Graph graph, String filename)
            throws IOException {

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            int[] chromosome = individual.getChromosome();

            pw.printf("Waga przekroju (fitness): %.4f%n", individual.getFitness());
            pw.println();

            // Zliczamy wierzchołki w każdym podzbiorze
            int countV1 = 0, countV2 = 0;
            for (int bit : chromosome) {
                if (bit == 0) countV1++; else countV2++;
            }
            pw.printf("Liczba wierzchołków w V1: %d%n", countV1);
            pw.printf("Liczba wierzchołków w V2: %d%n", countV2);
            pw.println();

            // Przypisanie każdego wierzchołka do podzbioru (indeksowanie 1-based)
            pw.println("Przypisanie wierzchołków:");
            for (int i = 0; i < chromosome.length; i++) {
                String subset = (chromosome[i] == 0) ? "V1" : "V2";
                pw.printf("Wierzchołek %d -> %s%n", i + 1, subset);
            }
        }
    }
}
