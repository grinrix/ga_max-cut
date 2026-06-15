package ga;

import core.Individual;

public class Population {

    // Strukturę przechowująca osobników – tablica o stałym rozmiarze
    private final Individual[] individuals;

    public Population(int size) {
        // Zainicjalizowanie pustej populacji o zadanym rozmiarze
        this.individuals = new Individual[size];
    }

    // Zwraca osobnika o wskazanym indeksie
    public Individual getIndividual(int index) {
        return individuals[index];
    }

    // Ustawia osobnika na wskazanym indeksie
    public void setIndividual(int index, Individual ind) {
        this.individuals[index] = ind;
    }
    
    // Zwraca aktualny rozmiar populacji (przydatne do pętli)
    public int size() {
        return individuals.length;
    }

    public Individual getBestIndividual() {
        // Jeśli populacja jest pusta lub niezainicjalizowana, zwracamy null
        if (individuals == null || individuals.length == 0 || individuals[0] == null) {
            return null;
        }

        // Zaczynamy poszukiwania, zakładając, że pierwszy osobnik jest najlepszy
        Individual best = individuals[0];

        // Przeszukujemy resztę populacji w poszukiwaniu wyższego Fitness (szukamy Max-Cut)
        for (int i = 1; i < individuals.length; i++) {
            if (individuals[i] != null && individuals[i].getFitness() > best.getFitness()) {
                best = individuals[i];
            }
        }

        return best;
    }
}