
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
    static LinkedList<Vector<Double>> centroidesIniciales = new LinkedList<Vector<Double>>();
    static LinkedList<Vector<Double>> promedioDeInsPorClus = new LinkedList<Vector<Double>>();
    static Vector<Double> cantidadDeElemPorCluster = new Vector<>();
    static int numDeClusters;
    static int iteracion=1;
    static int checker=1;
    static int numPuntosPorInstancia=4; //registros en el DataSet, aqui uso los iris dataset
    
    static Vector<Double> clusterAsignado;
    
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
            centroidesIniciales.add(dataSet.get(i));
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
                    a = (centroidesIniciales.get(j).get(k) - dataSet.get(i).get(k));
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
        
        iteracion++;
    }
    
    public static void calcularMediaCentral() {
        
    }
    
    public static void imprimirCentroidesIniciales() {
        for (Vector<Double> instancia : centroidesIniciales) {
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
}
