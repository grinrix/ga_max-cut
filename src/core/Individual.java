package core;

import java.util.Random;

/**
 * Reprezentuje jednego osobnika w populacji algorytmu genetycznego.
 *
 * Osobnik kodowany jest jako binarny wektor chromosomów (genów) o długości
 * równej liczbie wierzchołków grafu. Bit 0 oznacza przynależność wierzchołka
 * do podzbioru V1, a bit 1 — do podzbioru V2.
 */
public class Individual {

    /** Wektor binarny genów: chromosome[i] ∈ {0, 1}. */
    private final int[] chromosome;

    /** Wartość funkcji przystosowania (fitness) — suma wag krawędzi przeciętych. */
    private double fitness;

    /**
     * Konstruktor tworzący losowego osobnika.
     * Używany przy inicjalizacji populacji startowej.
     *
     * @param length długość chromosomu (liczba wierzchołków grafu)
     * @param rand   źródło losowości
     */
    public Individual(int length, Random rand) {
        this.chromosome = new int[length];
        for (int i = 0; i < length; i++) {
            this.chromosome[i] = rand.nextBoolean() ? 1 : 0;
        }
        this.fitness = 0.0;
    }

    /**
     * Konstruktor tworzący osobnika na podstawie gotowego chromosomu.
     * Używany przy tworzeniu potomków po krzyżowaniu.
     *
     * @param chromosome gotowy wektor binarny genów
     */
    public Individual(int[] chromosome) {
        this.chromosome = chromosome.clone();
        this.fitness = 0.0;
    }

    /** Zwraca bezpośrednią referencję do tablicy chromosomów (do mutacji in-place). */
    public int[] getChromosome() { return chromosome; }

    /** Zwraca aktualną wartość fitness osobnika. */
    public double getFitness() { return fitness; }

    /** Ustawia wartość fitness po ewaluacji przez {@link Evaluator}. */
    public void setFitness(double fitness) { this.fitness = fitness; }

    /**
     * Tworzy głęboką kopię osobnika (wraz z wartością fitness).
     * Przydatne przy implementacji elityzmu — chroni oryginał przed nadpisaniem.
     *
     * @return nowy obiekt Individual z identycznym chromosomem i fitness
     */
    public Individual copy() {
        Individual copy = new Individual(this.chromosome);
        copy.setFitness(this.fitness);
        return copy;
    }
}
