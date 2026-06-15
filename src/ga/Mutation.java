package ga;

import core.Individual;
import java.util.Random;

public class Mutation {

    private final Random random = new Random();

    public void bitFlipMutation(Individual individual, double pm) {
        // Pobieramy tablicę genów osobnika (zakładam typ int[])
        int[] genes = individual.getGenes();

        // Przechodzimy po każdym genie po kolei
        for (int i = 0; i < genes.length; i++) {
            // Sprawdzamy, czy zachodzi prawdopodobieństwo mutacji dla tego konkretnego bitu
            if (random.nextDouble() < pm) {
                // Odwracamy bit: jeśli był 0, staje się 1; jeśli był 1, staje się 0
                // Można to zrobić prostym warunkiem lub operatorem trójargumentowym:
                genes[i] = (genes[i] == 0) ? 1 : 0;
                
                // Alternatywny, sprytny sposób matematyczny (XOR lub odejmowanie):
                // genes[i] = 1 - genes[i];
            }
        }
        
        // UWAGA: Jeśli Twój Individual po zmianie tablicy genów musi 
        // przeliczyć swój koszt/fitness na nowo, warto tu wywołać np.:
        // individual.setGenes(genes); // o ile metoda modyfikuje też stan/flagi osobnika
    }
}