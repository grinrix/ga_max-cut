package io;

import core.Graph;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RudyGraphReader {
    public Graph readGraph(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            while (line != null && line.trim().isEmpty()) {
                line = br.readLine(); // Pomiń ewentualne puste linie początkowe
            }

            if (line == null) throw new IOException("Pusty plik instancji");

            String[] firstLineTokens = line.trim().split("\\s+");
            int verticesCount = Integer.parseInt(firstLineTokens[0]);

            Graph graph = new Graph(verticesCount);

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.trim().split("\\s+");

                // Konwersja indeksowania z 1-based (plik) na 0-based (Java)
                int u = Integer.parseInt(tokens[0]) - 1;
                int v = Integer.parseInt(tokens[1]) - 1;
                double weight = Double.parseDouble(tokens[2]);

                graph.addEdge(u, v, weight);
            }
            return graph;
        }
    }
}