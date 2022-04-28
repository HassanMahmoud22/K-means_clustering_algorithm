/*
    Hassan Mahmoud Hassan
    20180088
    Group S2
 */
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        KMeans kmeans = new KMeans();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter number of clusters");
        int clusters = input.nextInt();
        System.out.println("Choose distance rule \n 1- euclidean \n 2- Manhattan");
        int distanceChoice = input.nextInt();
        kmeans.kMeansAlgorithm("Power_Consumption.csv",clusters , distanceChoice);
    }
}
