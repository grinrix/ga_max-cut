package ga;

import core.Individual;
import java.util.Random;

/**
 * Operator krzyżowania dla algorytmu genetycznego rozwiązującego problem Max-Cut.
 *
 * Implementuje krzyżowanie jednorodne (uniform crossover), w którym każdy gen
 * potomka pochodzi losowo (z prawdopodobieństwem 0.5) od jednego z dwóch rodziców.
 * Krzyżowanie jednorodne jest zalecane dla reprezentacji binarnej, ponieważ
 * pozwala na efektywną eksplorację przestrzeni rozwiązań bez zależności
 * od pozycji genów w chromosomie.
 */
public class Crossover {

    /** Źródło losowości — współdzielone w ramach obiektu. */
    private final Random random = new Random();

    /**
     * Wykonuje krzyżowanie jednorodne (uniform crossover) dwóch rodziców.
     *
     * Z prawdopodobieństwem {@code pc} tworzy dwóch nowych potomków przez losowe
     * mieszanie genów rodziców. Każda pozycja chromosomu dziedziczy gen od
     * rodzica 1 lub rodzica 2 z równym prawdopodobieństwem 0.5.
     *
     * Jeśli krzyżowanie nie zachodzi (losowanie nie spełnia warunku {@code pc}),
     * zwracane są głębokie kopie rodziców — chroni to oryginały przed późniejszą
     * mutacją in-place.
     *
     * @param parent1 pierwszy rodzic
     * @param parent2 drugi rodzic
     * @param pc      prawdopodobieństwo zajścia krzyżowania (0.0 – 1.0)
     * @return        tablica dwóch potomków [child1, child2]
     */
    public Individual[] uniformCrossover(Individual parent1, Individual parent2, double pc) {
        Individual[] offspring = new Individual[2];

        if (random.nextDouble() < pc) {
            // Krzyżowanie zachodzi: mieszamy geny rodziców
            int[] parent1Genes = parent1.getChromosome();
            int[] parent2Genes = parent2.getChromosome();
            int length = parent1Genes.length;

            int[] child1Genes = new int[length];
            int[] child2Genes = new int[length];

            // Dla każdej pozycji losujemy, który rodzic przekazuje gen któremu potomkowi
            for (int i = 0; i < length; i++) {
                if (random.nextDouble() < 0.5) {
                    child1Genes[i] = parent1Genes[i];
                    child2Genes[i] = parent2Genes[i];
                } else {
                    child1Genes[i] = parent2Genes[i];
                    child2Genes[i] = parent1Genes[i];
                }
            }

            offspring[0] = new Individual(child1Genes);
            offspring[1] = new Individual(child2Genes);

        } else {
            // Krzyżowanie nie zachodzi: klonujemy rodziców (głęboka kopia)
            offspring[0] = new Individual(parent1.getChromosome().clone());
            offspring[1] = new Individual(parent2.getChromosome().clone());
        }

        return offspring;
    }
}
