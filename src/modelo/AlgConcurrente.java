package modelo;

import java.util.*;
import java.util.concurrent.*;

public class AlgConcurrente {

    // Lista de adyacencia del grafo
    private final ArrayList<ArrayList<Integer>> adj;
    // Arreglo para marcar qué nodos fueron visitados
    private final boolean[] visited;
    // Lista de resultados sincronizada para guardar los nodos visitados
    private final List<Integer> resultado;
    // Executor para manejar múltiples hilos
    private final ExecutorService executor;
    // Objeto para sincronizar accesos concurrentes
    private final Object lock = new Object();

    public AlgConcurrente(ArrayList<ArrayList<Integer>> adj) {
        this.adj = adj;
        this.visited = new boolean[adj.size()];
        this.resultado = Collections.synchronizedList(new ArrayList<>());
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // Pool de hilos según núcleos
    }

    // Ejecuta un recorrido DFS concurrente a partir del nodo inicial
    public List<Integer> ejecutarDFS(int start) {
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        queue.add(start);

        List<Future<?>> tareas = new ArrayList<>();

        // Lanza tareas concurrentes en múltiples hilos
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            tareas.add(executor.submit(() -> {
                while (!queue.isEmpty()) {
                    Integer nodo = queue.poll();
                    if (nodo == null) continue;

                    synchronized (lock) {
                        if (visited[nodo]) continue;
                        visited[nodo] = true;
                        resultado.add(nodo);
                    }

                    // Agrega los vecinos no visitados a la cola
                    for (int vecino : adj.get(nodo)) {
                        synchronized (lock) {
                            if (!visited[vecino]) {
                                queue.add(vecino);
                            }
                        }
                    }
                }
            }));
        }

        // Espera que todas las tareas terminen
        for (Future<?> f : tareas) {
            try {
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executor.shutdown(); // Cierra el pool de hilos
        return resultado;
    }

    // Agrega una arista entre dos nodos en un grafo no dirigido
    public static void addEdge(ArrayList<ArrayList<Integer>> adj, int s, int t) {
        adj.get(s).add(t);
        adj.get(t).add(s);
    }

    // Genera un grafo aleatorio con V vértices y E aristas
    public static ArrayList<ArrayList<Integer>> generarGrafo(int V, int E) {
        Random rand = new Random();
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }

        Set<String> conexiones = new HashSet<>();
        while (conexiones.size() < E) {
            int u = rand.nextInt(V);
            int v = rand.nextInt(V);
            if (u != v && !conexiones.contains(u + "," + v) && !conexiones.contains(v + "," + u)) {
                conexiones.add(u + "," + v);
                addEdge(adj, u, v);
            }
        }
        return adj;
    }
}
