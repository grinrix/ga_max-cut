package core;

import java.util.Random;

public class Individual {
    private final int[] chromosome; // wektor binarny (geny: 0 lub 1)
    private double fitness;

    // Konstruktor dla losowego osobnika (populacja początkowa)
    public Individual(int length, Random rand) {
        this.chromosome = new int[length];
        for (int i = 0; i < length; i++) {
            this.chromosome[i] = rand.nextBoolean() ? 1 : 0;
        }
        this.fitness = 0.0;
    }

    // Konstruktor dla potomka (używany przy krzyżowaniu przez Osobę 2)
    public Individual(int[] chromosome) {
        this.chromosome = chromosome.clone();
        this.fitness = 0.0;
    }

    public int[] getChromosome() { return chromosome; }
    public double getFitness() { return fitness; }
    public void setFitness(double fitness) { this.fitness = fitness; }

    // Głęboka kopia przydatna do elityzmu
    public Individual copy() {
        Individual copy = new Individual(this.chromosome);
        copy.setFitness(this.fitness);
        return copy;
    }
}