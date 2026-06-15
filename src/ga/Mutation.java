package ga;

import core.Individual;
import java.util.Random;

/**
 * Operator mutacji bit-flip dla algorytmu genetycznego rozwiązującego problem Max-Cut.
 *
 * Mutacja bit-flip jest standardowym operatorem dla reprezentacji binarnej.
 * Każdy gen chromosomu jest niezależnie poddawany próbie mutacji z takim samym
 * prawdopodobieństwem {@code pm}. Odwrócony bit zmienia przynależność wierzchołka
 * z podzbioru V1 do V2 lub odwrotnie.
 */
public class Mutation {

    /** Źródło losowości — współdzielone w ramach obiektu. */
    private final Random random = new Random();

    /**
     * Wykonuje mutację bit-flip na chromosomie osobnika.
     *
     * Iteruje po każdym genie i z prawdopodobieństwem {@code pm} odwraca jego
     * wartość (0→1 lub 1→0). Modyfikacja odbywa się in-place — zmienia tablicę
     * wewnątrz obiektu {@code Individual}, dlatego należy wywołać tę metodę
     * na kopiach potomków, a nie bezpośrednio na rodzicach.
     *
     * @param individual osobnik, którego chromosom zostanie zmutowany
     * @param pm         prawdopodobieństwo mutacji pojedynczego bitu (0.0 – 1.0)
     */
    public void bitFlipMutation(Individual individual, double pm) {
        int[] genes = individual.getChromosome();

        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < pm) {
                // Odwrócenie bitu: 0 → 1, 1 → 0
                genes[i] = 1 - genes[i];
            }
        }
        // Uwaga: getChromosome() zwraca referencję do wewnętrznej tablicy,
        // więc modyfikacja genes[] zmienia chromosom osobnika bezpośrednio.
        // Po mutacji fitness osobnika jest nieaktualne — należy ponownie wywołać evaluate().
    }
}
