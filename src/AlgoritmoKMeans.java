
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
    static int checker=1;
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
        
        for(int col=0;col<numPuntosPorInstancia;col++) {
            Double punto = lineScanner.nextDouble();
            instancia.add(punto);
        }
        
        dataSet.add(instancia);
        numDeInstancias++;
    }
    
    // Elige los centroides de todo el dataset
    public static void elegirCentroides() {
        for (int i=0; i<numDeClusters; i++) {
            centroides.add(dataSet.get(i));
        }
    }
    
    public static void calcularDistanciaEuclidiana() {
        double a=0;
        double res=0;
        
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
                    cluster = j; // Numero de cluster al que pertenece la instancia
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
        
        for (int i=0; i<numDeClusters; i++) {
            // cluster = i 
            sumatoriaDePtsPorIns = new Vector<Double>(); // Reutilizamos el vector para el siguiente cluster
            for (int x=0; x<numPuntosPorInstancia; x++) {
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
                    instanciasEnCluster++; // Numero de instancias en el cluster
                }    
            }
            promedioDeInsPorClus.add(sumatoriaDePtsPorIns);
            cantidadDeElemPorCluster.add(instanciasEnCluster);
        }
        
        for (int i=0; i<numDeClusters; i++) {
            Vector<Double> promediosPtsDelCluster = new Vector<Double>();
            for (int j=0; j<numPuntosPorInstancia; j++) {
                if (cantidadDeElemPorCluster.get(i)==0) {
                    promediosPtsDelCluster.add(0.0);
                } else {
                    promediosPtsDelCluster.add(promedioDeInsPorClus.get(i).get(j)/cantidadDeElemPorCluster.get(i));
                }
            }
            promedioDeInsPorClus.set(i, promediosPtsDelCluster);
        }
        
        centroides = new LinkedList<Vector<Double>>();
        centroides = promedioDeInsPorClus;
        promedioDeInsPorClus = new LinkedList<Vector<Double>>();
    }
    
    public static void imprimirCentroidesIniciales() {
        for (Vector<Double> instancia : centroides) {
            System.out.print("[");
            for (Double punto : instancia) {
                System.out.print(punto + ", ");
            }
            System.out.print("]\n");
        }
        //centroidesIniciales.clear();
    }
    
    public static void imprimirDistancias() {
        int i=1;
        for (Vector<Double> distancias : distanciaEuclidiana) {
            System.out.print(i + ".[");
            for (Double distancia : distancias) {
                System.out.print(distancia + ", ");
            }
            System.out.print("]\n");
            i++;
        }
    }
    
    public static void imprimirAsignacionCluster() {
        System.out.println("==================================================================================");
        int i=0;
        for (Vector<Double> instancias : dataSet) {
            System.out.print((i+1) + ".[");
            for (Double puntos : instancias) {
                System.out.print(puntos + ", ");
            }
            System.out.print("] C: " + clusterAsignado.get(i) + "\n");
            i++;
        }
    }
    
    public static void imprimirNuevosCentroides() {
        System.out.println("============================== NUEVOS CENTROIDES =================================");
        int i=0;
        for (Vector<Double> nuevosCentroides : centroides) {
            System.out.print((i+1) + ".[");
            for (Double puntos : nuevosCentroides) {
                System.out.print(puntos + ", ");
            }
            System.out.print("]\n");
            i++;
        }
    }
}
