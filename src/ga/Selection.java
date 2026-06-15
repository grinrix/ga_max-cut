package ga;

import core.Individual;
import java.util.Random;

/**
 * Metody selekcji osobników do reprodukcji w algorytmie genetycznym.
 *
 * Klasa implementuje dwie strategie selekcji:
 * <ul>
 *   <li><b>Turniejowa</b> — losuje k osobników i wybiera najlepszego (szybka,
 *       odporna na skalowanie fitnessu, łatwa do strojenia przez rozmiar turnieju).</li>
 *   <li><b>Kołem ruletki</b> — wybiera osobnika proporcjonalnie do jego wartości fitness
 *       (faworyzuje najlepszych, może powodować przedwczesną zbieżność).</li>
 * </ul>
 * Obie metody zwracają referencję do wybranego osobnika — nie tworzą kopii.
 * Kopiowanie leży po stronie wywołującego (np. {@link GeneticEngine}).
 */
public class Selection {

    /** Źródło losowości — współdzielone w ramach obiektu. */
    private final Random random = new Random();

    /**
     * Selekcja turniejowa — wybiera najlepszego osobnika spośród losowo dobranych kandydatów.
     *
     * Z populacji losowane jest {@code tournamentSize} osobników (ze zwracaniem),
     * a spośród nich zwracany jest ten o najwyższym fitness. Im większy rozmiar
     * turnieju, tym silniejsza presja selekcyjna — populacja szybciej zbiega do
     * lokalnego optimum, ale traci różnorodność.
     *
     * @param population     aktualna populacja osobników
     * @param tournamentSize liczba uczestników turnieju (zalecane: 2–5)
     * @return               osobnik o najwyższym fitness w losowej grupie
     */
    public Individual tournamentSelection(Population population, int tournamentSize) {
        Individual best = null;

        for (int i = 0; i < tournamentSize; i++) {
            // Losujemy indeks uczestnika turnieju (ze zwracaniem)
            int idx = random.nextInt(population.size());
            Individual candidate = population.getIndividual(idx);

            // Aktualizujemy najlepszego uczestnika turnieju
            if (best == null || candidate.getFitness() > best.getFitness()) {
                best = candidate;
            }
        }

        return best;
    }

    /**
     * Selekcja kołem ruletki (selekcja proporcjonalna do fitness).
     *
     * Każdy osobnik otrzymuje wycinek koła ruletki proporcjonalny do swojego
     * fitness. Losowany jest jeden punkt na kole, a wybrany zostaje osobnik,
     * do którego wycinka należy ten punkt. Osobniki z zerowym fitness mają
     * zerową szansę na wybór.
     *
     * Uwaga: metoda zakłada, że wszystkie wartości fitness są nieujemne
     * (co jest zapewnione przez funkcję Max-Cut zwracającą sumy wag krawędzi ≥ 0).
     *
     * @param population aktualna populacja osobników
     * @return           wylosowany osobnik (z prawdopodobieństwem proporcjonalnym do fitness)
     */
    public Individual rouletteWheelSelection(Population population) {
        // Obliczamy sumę wszystkich wartości fitness w populacji
        double totalFitness = 0.0;
        for (int i = 0; i < population.size(); i++) {
            totalFitness += population.getIndividual(i).getFitness();
        }

        // Losujemy punkt na kole (z zakresu [0, totalFitness))
        double spin = random.nextDouble() * totalFitness;

        // Przechodzimy przez populację, zbierając skumulowane fitness, aż przekroczymy spin
        double cumulative = 0.0;
        for (int i = 0; i < population.size(); i++) {
            cumulative += population.getIndividual(i).getFitness();
            if (cumulative >= spin) {
                return population.getIndividual(i);
            }
        }

        // Fallback: zwracamy ostatniego osobnika (możliwe przy błędach zmiennoprzecinkowych)
        return population.getIndividual(population.size() - 1);
    }
}
