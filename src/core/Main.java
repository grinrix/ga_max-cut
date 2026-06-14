package core;

import io.RudyGraphReader;
import java.io.IOException;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        // Ścieżka
        String filePath = "data/input/test.txt";

        try {
            System.out.println("|ETAP 1: TEST WCZYTYWANIA GRAFU|");
            RudyGraphReader reader = new RudyGraphReader();
            Graph graph = reader.readGraph(filePath);

            System.out.println("Wczytano graf.");
            System.out.println("Liczba wierzchołków: " + graph.getVerticesCount());
            System.out.println("Liczba krawędzi: " + graph.getEdges().size());


            System.out.println("\n|ETAP 2: TEST MODELU I EWALUATORA|");
            Evaluator evaluator = new Evaluator();
            Random rand = new Random();

            // tworzymy jeden losowy osobnik testowy
            Individual testIndividual = new Individual(graph.getVerticesCount(), rand);

            // Wyświetlamy jego genotyp (przypisanie wierzchołków do podzbioru 0 lub 1)
            System.out.print("Wylosowany wektor binarny (Genotyp): [ ");
            for (int gene : testIndividual.getChromosome()) {
                System.out.print(gene + " ");
            }
            System.out.println("]");

            // obliczamy wagę przecięcia (Fitness)
            evaluator.evaluate(testIndividual, graph);

            System.out.println("Wyliczona waga przekroju (Fitness): " + testIndividual.getFitness());
            System.out.println("Liczba wykonanych ewaluacji: " + evaluator.getEvaluationCount());

            // napisać geneticEngine, odpalić cały algorytm


        } catch (IOException e) {
            System.err.println("ERROR: Nie udało się wczytać pliku. Sprawdź ścieżkę");
            System.err.println("Szczegóły: " + e.getMessage());
        }
    }
}