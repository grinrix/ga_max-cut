package ga;

import core.Individual;
import java.util.Random;

public class Crossover {

    private final Random random = new Random();

    public Individual[] uniformCrossover(Individual parent1, Individual parent2, double pc) {
        Individual[] offspring = new Individual[2];

        // 1. Sprawdzamy warunek prawdopodobieństwa krzyżowania (pc)
        if (random.nextDouble() < pc) {
            
            // Pobieramy genotypy rodziców (zakładam, że zwracają tablice, np. int[] lub boolean[])
            // Jeśli Twoje geny to inny typ, zmień deklarację poniżej (np. na boolean[] lub int[])
            int[] parent1Genes = parent1.getGenes();
            int[] parent2Genes = parent2.getGenes();
            int length = parent1Genes.length;

            // Przygotowujemy tablice na geny potomków
            int[] child1Genes = new int[length];
            int[] child2Genes = new int[length];

            // 2. Przechodzimy pętlą po każdym genie
            for (int i = 0; i < length; i++) {
                // Z prawdopodobieństwem 0.5 zamieniamy geny u potomków
                if (random.nextDouble() < 0.5) {
                    child1Genes[i] = parent1Genes[i];
                    child2Genes[i] = parent2Genes[i];
                } else {
                    child1Genes[i] = parent2Genes[i];
                    child2Genes[i] = parent1Genes[i];
                }
            }

            // Tworzymy nowych osobników na podstawie zmiksowanych genów
            offspring[0] = new Individual(child1Genes);
            offspring[1] = new Individual(child2Genes);

        } else {
            // 3. Jeśli warunek 'pc' nie został spełniony, klonujemy rodziców
            // Robimy "głęboką kopię", aby zmiany u potomków (np. mutacja) nie psuły rodziców
            offspring[0] = new Individual(parent1.getGenes().clone());
            offspring[1] = new Individual(parent2.getGenes().clone())
        }

        return offspring;
    }
}