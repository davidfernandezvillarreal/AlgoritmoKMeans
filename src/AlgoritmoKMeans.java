
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */
public class AlgoritmoKMeans {
    static int numDeInstancias = 0;
    static LinkedList<Vector<Double>> dataSet = new LinkedList<Vector<Double>>();
    static LinkedList<Vector<Double>> distanciaEuclidiana = new LinkedList<Vector<Double>>();
    static LinkedList<Vector<Double>> centroides = new LinkedList<Vector<Double>>();
    static LinkedList<Vector<Double>> promedioDeInsPorClus = new LinkedList<Vector<Double>>();
    static Vector<Integer> cantidadDeElemPorCluster = new Vector<Integer>();
    static int numDeClusters;
    static int iteracion=0;
    static boolean ciclo=true;
    static int numPuntosPorInstancia=4; //registros en el DataSet, aqui uso los iris dataset
    
    static Vector<Double> clusterAsignado;
    static LinkedList<Vector<Double>> evolucionDeLosClusters = new LinkedList<Vector<Double>>();
    
    public AlgoritmoKMeans(int numDeClusters) {
        AlgoritmoKMeans.numDeClusters = numDeClusters;
    }
    
    // Lee el archivo
    public static void leerArchivo(File archivo) throws FileNotFoundException {
        Scanner scanner = new Scanner(archivo);//Dataset path
        scanner.useDelimiter(System.getProperty("line.separator"));
        int lineNo = 0;
        
        while (scanner.hasNext()) {
            analizarLinea(scanner.next(),lineNo);
            lineNo++;
        }
             // System.out.println("total"+num); PRINT THE TOTAL
        scanner.close();
    }
    
    // Analiza cada línea del archivo y separa los datos por tablulaciones
    public static void analizarLinea(String line,int lineNo) { 
        Scanner lineScanner = new Scanner(line);
        lineScanner.useDelimiter("\t");  
        Vector<Double> instancia = new Vector<Double>();
        
        for(int i=0; i<numPuntosPorInstancia; i++) {
            Double punto = lineScanner.nextDouble();
            instancia.add(punto);
        }
        
        dataSet.add(instancia);
        numDeInstancias++;
    }
    
    // Elige los primeros elementos como centroides de todo el dataset
    public static void elegirCentroidesIniciales() {
        for (int i=0; i<numDeClusters; i++) {
            centroides.add(dataSet.get(i));
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
                    /*
                     * Formula para la distancia euclidiana RaizCuadrada( Sumatoria( (bi-ai)^2 ) )
                     */
                    a = (centroides.get(j).get(k) - dataSet.get(i).get(k));
                    a = a * a;
                    res = res + a;
                }
                res = Math.sqrt(res);
                distancias.add(res);
            }
            distanciaEuclidiana.add(distancias);
        }
    }
    
    public static void asignarCluster() {
        double distanciaMenor;
        double cluster;
        clusterAsignado = new Vector<Double>();
        
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
        Vector<Double> sumatoriaDePtsPorIns = new Vector<Double>();
        cantidadDeElemPorCluster = new Vector<Integer>();
        
        for (int i=0; i<numDeClusters; i++) {
            // cluster = i 
            sumatoriaDePtsPorIns = new Vector<Double>(); // Reutilizamos el vector para el siguiente cluster
            for (int x=0; x<numPuntosPorInstancia; x++) { // Inicializamos los puntos necesarios del vector para la sumatoria
                sumatoriaDePtsPorIns.add(x, 0.0);
            }
            instanciasEnCluster = 0;
            
            for (int j=0; j<numDeInstancias; j++) {
                for (int k=0; k<numPuntosPorInstancia; k++) {
                    if (clusterAsignado.get(j)==i) {
                        sumatoriaDePtsPorIns.set(k, sumatoriaDePtsPorIns.get(k)+dataSet.get(j).get(k));
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
            Vector<Double> promedioPtsDelCluster = new Vector<Double>();
            for (int j=0; j<numPuntosPorInstancia; j++) {
                if (cantidadDeElemPorCluster.get(i)==0) {
                    promedioPtsDelCluster.add(0.0);
                } else {
                    promedioPtsDelCluster.add(promedioDeInsPorClus.get(i).get(j)/cantidadDeElemPorCluster.get(i));
                }
            }
            promedioDeInsPorClus.set(i, promedioPtsDelCluster);
        }
        
        centroides = new LinkedList<Vector<Double>>();
        centroides = promedioDeInsPorClus;
        promedioDeInsPorClus = new LinkedList<Vector<Double>>();
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
    
    public static void imprimirCentroides() {
        System.out.println("============================ CENTROIDES INICIALES =================================");
        for (Vector<Double> instancias : centroides) 
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
        for (Vector<Double> instancias : dataSet) 
            System.out.println(instancias + " C: " + clusterAsignado.get(i++));
    }
    
    public static void imprimirClusters() {
        System.out.println("================================== CLUSTERS ======================================");
        for (int i=0; i<numDeClusters; i++) {
            System.out.println("CLUSTER " + (i+1));
            for (int j=0; j<numDeInstancias; j++) {
                if (evolucionDeLosClusters.getLast().get(j)==i) {
                    System.out.println(dataSet.get(j));
                } 
            }
        }
    }
    
    public static void ejecutarKMeans() {
        /*
           Primera agrupación  
        */
        elegirCentroidesIniciales();
        calcularDistanciaEuclidiana();
        asignarCluster();
        
        while (ciclo) {
            System.out.println("--------------------------- ITERACION " + iteracion + " ----------------------------------");
            calcularNuevosCentroides();
            calcularDistanciaEuclidiana();
            asignarCluster();
            compararAsignacionDeCluster();
        }
        
        imprimirClusters();
    }
}
