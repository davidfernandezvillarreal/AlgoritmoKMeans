
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
    static int iteraciones=1;
    static int checker=1;
    static int numPuntosPorInstancia=4;//rows in Your DataSet here i use iris dataset
    static int numeroDeCentroides;
    
    public AlgoritmoKMeans(int numeroDeCentroides) {
        AlgoritmoKMeans.numeroDeCentroides = numeroDeCentroides;
    }
    
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
    
    public static void elegirCentroides() {
        for (int i=0; i<numeroDeCentroides; i++) {
            centroidesIniciales.add(dataSet.get(i));
        }
    }
    
    public static void imprimirCentroidesIniciales() {
        for (Vector<Double> instancia : centroidesIniciales) {
            System.out.print("[");
            for (Double punto : instancia) {
                System.out.print(punto + ", ");
            }
            System.out.print("]\n");
        }
        centroidesIniciales.clear();
    }
}
