
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JTextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */

/**
 *  
 * Autor: David Fernandez Villarreal
 * 
 * Para el uso del algoritmo KMeans es necesario tener una base de datos
 * donde cada dato esté separado por comas (,) en un archivo .txt. 
 * El usuario tendrá que ingresar el número de cluster en los que se 
 * agruparan los datos.
 * 
 * Importante: El algoritmo calcula las distancias, por lo que los datos que 
 * no sean numérico no se tomarán en cuenta.
 */

public class AlgoritmoKMeans {
    private static int numDeInstancias=0;
    private static LinkedList<Vector<Object>> dataSet = new LinkedList<Vector<Object>>();
    private static LinkedList<Vector<Double>> distanciaEuclidiana = new LinkedList<Vector<Double>>();
    private static LinkedList<Vector<Object>> centroides = new LinkedList<Vector<Object>>();
    private static LinkedList<Vector<Object>> promedioDeInsPorClus = new LinkedList<Vector<Object>>();
    private static Vector<Integer> cantidadDeElemPorCluster = new Vector<Integer>();
    private static int numDeClusters;
    private static int iteracion=0;
    private static boolean ciclo=true;
    private static int numPuntosPorInstancia=0; 
    
    private static Vector<Integer> clusterAsignado;
    private static LinkedList<Vector<Integer>> evolucionDeLosClusters = new LinkedList<Vector<Integer>>();
    private static LinkedList<LinkedList<Vector<Object>>> clusters = new LinkedList<LinkedList<Vector<Object>>>();
    
    public AlgoritmoKMeans(int numDeClusters) {
        AlgoritmoKMeans.numDeClusters = numDeClusters;
    }

    public static LinkedList<Vector<Object>> getDataSet() {
        return dataSet;
    }

    public static Vector<Integer> getCantidadDeElemPorCluster() {
        return cantidadDeElemPorCluster;
    }
    
    // Lee el archivo
    public static void leerArchivo(File archivo) throws FileNotFoundException {
        Scanner scanner = new Scanner(archivo);//Dataset path
        scanner.useDelimiter(System.getProperty("line.separator"));
        
        while (scanner.hasNext()) {
            analizarLinea(scanner.next());
        }
        
        scanner.close();
    }
    
    // Analiza cada línea del archivo y separa los datos por comas(,)
    public static void analizarLinea(String line) { 
        Scanner lineScanner = new Scanner(line);
        lineScanner.useDelimiter(",");  
        Vector<Object> instancia = new Vector<Object>();
        
        numPuntosPorInstancia=0;
        while (lineScanner.hasNext()) {
            Object punto = lineScanner.next();
            instancia.add(punto);
            numPuntosPorInstancia++;
        }
        
        dataSet.add(instancia);
        numDeInstancias++;
    }
    
    // Elige aleatoriamente los centroides iniciales
    public static void elegirCentroidesIniciales() {
        for (int i=0; i<numDeClusters; i++) {
            centroides.add(dataSet.get(new Random().nextInt(numDeInstancias)));
        }
    }
    
    public static void calcularDistanciaEuclidiana() {
        double a=0;
        double res=0;
        distanciaEuclidiana = new LinkedList<Vector<Double>>();
        
        for (int i=0; i<numDeInstancias; i++) {
            Vector<Double> distancias = new Vector<Double>();
            for (int j=0; j<numDeClusters; j++) {
                res=0;
                for (int k=0; k<numPuntosPorInstancia; k++) {
                    try {
                    /*
                     * Formula para la distancia euclidiana RaizCuadrada( Sumatoria( (bi-ai)^2 ) )
                     */
                    a = (Double.parseDouble(String.valueOf(centroides.get(j).get(k))) - 
                            Double.parseDouble(String.valueOf(dataSet.get(i).get(k))));
                    a = a * a;
                    res = res + a;
                    } catch (NumberFormatException nfe) {} // Atrapar la excepción para los puntos que no sean numéricos
                }
                res = Math.sqrt(res);
                distancias.add(res);
            }
            distanciaEuclidiana.add(distancias);
        }
    }
    
    public static void asignarCluster() {
        double distanciaMenor;
        int cluster;
        clusterAsignado = new Vector<Integer>();
        
        for (int i=0; i<numDeInstancias; i++) {
            cluster=1;
            distanciaMenor = distanciaEuclidiana.get(i).get(0);
            for (int j=0; j<numDeClusters; j++) {
                // Encontrar la menor distancia para asignar la instancia a ese cluster
                if (distanciaMenor>=distanciaEuclidiana.get(i).get(j)) {
                    distanciaMenor = distanciaEuclidiana.get(i).get(j); // Si distanciaMenor es mayor reemplazamos el valor
                    cluster = j; // Número de cluster al que pertenece la instancia
                }
            }
            
            // El recorrido de los datos tiene que ser en orden, ya que los cluster son correspondientes a los datos.
            clusterAsignado.add(cluster);  
        }
        evolucionDeLosClusters.add(iteracion, clusterAsignado);
        iteracion++;
    }
    
    public static void calcularNuevosCentroides() {
        int instanciasEnCluster=0;
        Vector<Object> sumatoriaDePtsPorIns = new Vector<Object>();
        cantidadDeElemPorCluster = new Vector<Integer>();
        
        for (int i=0; i<numDeClusters; i++) {
            // cluster = i 
            sumatoriaDePtsPorIns = new Vector<Object>(); // Reutilizamos el vector para el siguiente cluster
            for (int x=0; x<numPuntosPorInstancia; x++) { // Inicializamos los puntos necesarios del vector para la sumatoria
                sumatoriaDePtsPorIns.add(x, 0.0);
            }
            instanciasEnCluster = 0;
            
            for (int j=0; j<numDeInstancias; j++) {
                for (int k=0; k<numPuntosPorInstancia; k++) {
                    if (clusterAsignado.get(j)==i) {
                        try {
                            sumatoriaDePtsPorIns.set(k, Double.parseDouble(sumatoriaDePtsPorIns.get(k)+"") + 
                                    Double.parseDouble(dataSet.get(j).get(k)+""));
                        } catch (NumberFormatException nfe) {} // Excepción para los puntos que no son numéricos 
                        
                    }
                }
                if (clusterAsignado.get(j)==i) {
                    instanciasEnCluster++; // Número de instancias en el cluster
                }    
            }
            promedioDeInsPorClus.add(sumatoriaDePtsPorIns);
            cantidadDeElemPorCluster.add(instanciasEnCluster);
        }
        
        for (int i=0; i<numDeClusters; i++) {
            Vector<Object> promedioPtsDelCluster = new Vector<Object>();
            for (int j=0; j<numPuntosPorInstancia; j++) {
                if (cantidadDeElemPorCluster.get(i)==0) {
                    promedioPtsDelCluster.add(0.0);
                } else {
                    promedioPtsDelCluster.add(Double.parseDouble(promedioDeInsPorClus.get(i).get(j)+"")/cantidadDeElemPorCluster.get(i));
                }
            }
            promedioDeInsPorClus.set(i, promedioPtsDelCluster);
        }
        
        centroides = new LinkedList<Vector<Object>>();
        centroides = promedioDeInsPorClus;
        promedioDeInsPorClus = new LinkedList<Vector<Object>>();
    }
    
    public static void compararAsignacionDeCluster() {
        ciclo = false;
        for (int i=0; i<numDeInstancias; i++) {
            if (Double.compare(evolucionDeLosClusters.get(iteracion-2).get(i), 
                    evolucionDeLosClusters.get(iteracion-1).get(i)) != 0) {
                ciclo = true;
                break;
            }
        }
    }
    
    public static void imprimirDataset() {
        System.out.println("=================================== DATASET ========================================");
        for (Vector<Object> instancias : dataSet) 
            System.out.println(instancias);
    }
    
    public static void imprimirCentroides() {
        System.out.println("============================ CENTROIDES INICIALES =================================");
        for (Vector<Object> instancias : centroides) 
            System.out.println(instancias);
    }
    
    public static void imprimirDistancias() {
        System.out.println("================================= DISTANCIAS ======================================");
        for (Vector<Double> distancias : distanciaEuclidiana) 
            System.out.println(distancias);
    }
    
    public static void imprimirAsignacionCluster() {
        System.out.println("================================ AGRUPACIÓN =======================================");
        int i=0;
        for (Vector<Object> instancias : dataSet) 
            System.out.println(instancias + " C: " + clusterAsignado.get(i++));
    }
    
    public static void imprimirClusters() {
        System.out.println("Numero de iteraciones: " + iteracion);
        System.out.println("================================== CLUSTERS ======================================");
        for (int i=0; i<numDeClusters; i++) {
            System.out.println("CLUSTER " + (i+1));
            for (int j=0; j<numDeInstancias; j++) {
                if (evolucionDeLosClusters.getLast().get(j)==i) {
                    System.out.println(dataSet.get(j));
                }
            }
            System.out.println("Cantidad de instancias \nen el cluster: " + cantidadDeElemPorCluster.get(i));
        }
    }
    
    public static void imprimirClusters(JTextArea cajaClusters) {
        cajaClusters.setText(cajaClusters.getText() + "---------------------- Numero de iteraciones: " + iteracion + " ----------------------\n");
        for (int i=0; i<numDeClusters; i++) {
            cajaClusters.setText(cajaClusters.getText() + "--------- CLUSTER " + (i+1) + " ---------\n");
            for (int j=0; j<numDeInstancias; j++) {
                if (evolucionDeLosClusters.getLast().get(j)==i) {
                    cajaClusters.setText(cajaClusters.getText() + dataSet.get(j) + "\n");
                }
            }
            cajaClusters.setText(cajaClusters.getText() + "Cantidad de instancias: " + cantidadDeElemPorCluster.get(i) + "\n");
        }
    }
    
    public static LinkedList<LinkedList<Vector<Object>>> guardarDatosEnClusters() {
        clusters = new LinkedList<LinkedList<Vector<Object>>>();
        LinkedList<Vector<Object>> cluster = new LinkedList<Vector<Object>>();
        for (int i=0; i<numDeClusters; i++) {
            cluster = new LinkedList<Vector<Object>>();
            for (int j=0; j<numDeInstancias; j++) {
                if (evolucionDeLosClusters.getLast().get(j)==i) {
                    cluster.add(dataSet.get(j));
                }
            }
            clusters.add(cluster);
        }
        return clusters;
    }
    
    public static void restablecerValores() {
        numDeInstancias=0;
        dataSet = new LinkedList<Vector<Object>>();
        distanciaEuclidiana = new LinkedList<Vector<Double>>();
        centroides = new LinkedList<Vector<Object>>();
        promedioDeInsPorClus = new LinkedList<Vector<Object>>();
        cantidadDeElemPorCluster = new Vector<Integer>();
        iteracion=0;
        ciclo=true;
        numPuntosPorInstancia=0; 
        evolucionDeLosClusters = new LinkedList<Vector<Integer>>();
    }
    
    public static void ejecutarKMeans() {
        /*
           Primera agrupación  
        */
        elegirCentroidesIniciales();
        calcularDistanciaEuclidiana();
        asignarCluster();
        
        while (ciclo) {
            calcularNuevosCentroides();
            calcularDistanciaEuclidiana();
            asignarCluster();
            compararAsignacionDeCluster();
        }
    }
}
