package test;

import modelo.AlgConcurrente;
import java.util.*;

public class TestAlgConcurrente {

    public static void main(String[] args) {
        // Imprime la cantidad de hilos disponibles en el procesador
        int hilosDisponibles = Runtime.getRuntime().availableProcessors();
        System.out.println("Hilos disponibles para el pool: " + hilosDisponibles);

        int V = 800000; // Número de vértices (nodos del grafo)
        int E = 10000000; // Número de aristas (conexiones)

        Random rand = new Random();
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();

        // Inicializa la lista de adyacencia
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }

        // Conjunto para evitar duplicar aristas
        Set<String> conexiones = new HashSet<>();

        // Conectividad básica asegurada: una cadena lineal
        for (int i = 0; i < V - 1; i++) {
            adj.get(i).add(i + 1);
            adj.get(i + 1).add(i);
            conexiones.add(i + "," + (i + 1));
        }

        // Agrega aristas aleatorias restantes
        while (conexiones.size() < E) {
            int u = rand.nextInt(V);
            int v = rand.nextInt(V);

            if (u != v && !conexiones.contains(u + "," + v) && !conexiones.contains(v + "," + u)) {
                conexiones.add(u + "," + v);
                adj.get(u).add(v);
                adj.get(v).add(u); // Grafo no dirigido
            }
        }

        // Ejecuta el algoritmo DFS concurrente
        AlgConcurrente alg = new AlgConcurrente(adj);
        long inicio = System.currentTimeMillis(); // Marca el inicio del tiempo

        List<Integer> resultado = alg.ejecutarDFS(0); // Inicia desde el nodo 0

        long fin = System.currentTimeMillis(); // Marca el fin del tiempo

        // Resultados del recorrido
        System.out.println("Nodos totales: " + V);
        System.out.println("Aristas totales: " + E);
        System.out.println("Nodos visitados: " + resultado.size());
        System.out.println("Tiempo de ejecución: " + (fin - inicio) + " ms");
        System.out.println("Tiempo de ejecución: " + ((fin - inicio) / 1000.0) + " segundos");

    }
}
