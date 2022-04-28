import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class KMeans {
    private final List<Row> rows  = new ArrayList<>();
    private final List<List<Row>> clusters  = new ArrayList<>();
    private final List<Row> centroids = new ArrayList<>();
    private final List<Row> outliers = new ArrayList<>();
    private int kClusters;
    private int rowSize;
    private int generalId;

    public KMeans(){
        kClusters = 0;
        rowSize = 0;
        generalId = 0;
    }
    //setter for num of clusters
    public void setkClusters(int kClusters){
        this.kClusters = kClusters;
    }

    public void updateCentroids(List<Row> newCentroids){
        centroids.clear();
        centroids.addAll(newCentroids);
    }

    public void updateClusters(List<Row> newCentroids){
        clusters.clear();
        for(int i = 0; i < newCentroids.size(); i++){
            List<Row> temp = new ArrayList<>();
            clusters.add(temp);
        }
    }
    //create amount of empty clusters equal to passed size
    public void setClusters(int size){
        for(int i = 0; i < size; i++){
            List<Row> temp = new ArrayList<>();
            clusters.add(temp);
        }
    }

    //get attributes of one row as an array and create row by this data then store it in rows list
    private void createRow(String[] attributes){
        Row row = new Row();
        row.setId(Integer.parseInt(attributes[0]));
        for(int i = 1; i < attributes.length; i++){
            row.setFeature(Double.parseDouble(attributes[i]));
        }
        rows.add(row);
    }

    //read each row from csv file and call create row function to store it.
    public void ReadFeaturesFromCsv(String fileName){
        Path pathToFile = Paths.get(fileName);
        try(BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)){
            String line = br.readLine();
            line = br.readLine();
            while(line != null){
                String[] attributes = line.split(",");
                createRow(attributes);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //generate random centroids equal to num of clusters.
    public void generateRandomCentroids(){
        Random random = new Random();
        IntStream ints = random.ints(kClusters, 0, rows.size()-1);
        int[] temp = ints.toArray();
        for(int i = 0; i < kClusters; i++){
            centroids.add(rows.get(temp[i]));
        }
    }
    //calculate distance between the centroid and one row using euclidean rule
    public double euclideanDistance(List<Double> centroidFeatures, List<Double> rowFeatures){
            double sum = 0;
            for (int i = 0; i < centroidFeatures.size(); i++) {
                sum += Math.pow(centroidFeatures.get(i) - rowFeatures.get(i), 2);
            }
            return Math.sqrt(sum);
    }

    //calculate distance between the centroid and one row using manhattan rule
    public double manhattanDistance(List<Double> centroidFeatures, List<Double> rowFeatures){
        double sum = 0;
        for (int i = 0; i < centroidFeatures.size(); i++) {
            sum +=  Math.abs(centroidFeatures.get(i) - rowFeatures.get(i));
        }
        return Math.sqrt(sum);
    }
    //calculate mean for one cluster to generate its new centroid
    public List<Double> claculateMean(List<Row> cluster){
        List<Double> newList = new ArrayList<>();
        double value;
        for(int i = 0; i < rowSize; i++){
            value = 0;
            for (Row row : cluster) {
                value += row.features.get(i);
            }
            value /= cluster.size();
            newList.add(value);
        }
        return newList;
    }
    //generating new centroids by calculating the mean of each cluster
    public List<Row> calculateNewCentroids(List<List<Row>> latestClusters){
        List<Row> newCentroids = new ArrayList<>();
        for (List<Row> latestCluster : latestClusters) {
            Row newCentroid = new Row();
            newCentroid.setId(generalId++);
            newCentroid.setFeatures(claculateMean(latestCluster));
            newCentroids.add(newCentroid);
        }
        return newCentroids;
    }
    //check if two lists are identical
    public boolean areIdentical(List<Double> list1, List<Double> list2){
        for(int i = 0; i < list1.size(); i++){
            if(!list1.get(i).equals(list2.get(i))){
                return false;
            }
        }
        return true;
    }
    //function compare between old and new centroids and check if any changes
    public boolean changed(List<Row> newCentroids, List<Row> centroids){
       for(int i = 0; i < centroids.size(); i++){
           if (!areIdentical(centroids.get(i).getFeatures(), newCentroids.get(i).getFeatures()))
               return true;
           else
               newCentroids.get(i).setId(centroids.get(i).getId());
       }
       return false;
    }
    //sorting cluster
    void sort(List<Row> unsortedCluster)
    {
        int n = unsortedCluster.size();
        for (int i = 1; i < n; ++i) {
            Row key = unsortedCluster.get(i);
            int j = i - 1;

            while (j >= 0 && unsortedCluster.get(j).getDistanceFromCentroid() > key.getDistanceFromCentroid()) {
                unsortedCluster.set(j+1, unsortedCluster.get(j));
                j = j - 1;
            }
            unsortedCluster.set(j+1, key);
        }
    }

    //detect if there's any outlier using IQR.
    public void detectOutliers(){
        for (List<Row> cluster : clusters) {
            sort(cluster);
            int quartile1 = (int) Math.ceil((cluster.size()) * 0.25);
            int quartile3 = (int) Math.ceil((cluster.size()) * 0.75);
            double iqr = cluster.get(quartile3 - 1).getDistanceFromCentroid() - cluster.get(quartile1 - 1).getDistanceFromCentroid();
           // double lowerBoundVal = cluster.get(quartile1 - 1).getDistanceFromCentroid() - (1.5 * iqr);
            double upperBoundVal = cluster.get(quartile3 - 1).getDistanceFromCentroid() + (1.5 * iqr);
            for (Row row : cluster) {
                if (row.getDistanceFromCentroid() > upperBoundVal ) {
                    outliers.add(row);
                }
            }
        }
    }

    public void printClusters(){
        for(int i = 0; i < clusters.size(); i++){
            System.out.println("*************************************************************************");
            System.out.println("Cluster no." + (i + 1) + " with centroid [" + centroids.get(i) + "] is:");
            System.out.println("*************************************************************************");
            System.out.println(clusters.get(i));
        }
    }
    public void printOutliers(){
        if(outliers.size() > 0){
            System.out.println("*************************************************************************");
            System.out.println("Outliers are: ");
            System.out.println("*************************************************************************");
            System.out.println(outliers);
        }

    }

    //test K-Means Algorithm
    public void kMeansAlgorithm(String fileName, int kClusters, int distanceAlgorithm){
        double minDistance = 100000;
        ReadFeaturesFromCsv(fileName);
        setkClusters(kClusters);
        setClusters(kClusters);
        generateRandomCentroids();
        generalId = rows.size() + 1;
        rowSize = rows.get(0).getFeaturesSize();
        while(true) {
            for (Row row : rows) {
                int centroidIndex = 0;
                double distance;
                for (int j = 0; j < centroids.size(); j++) {
                    if (distanceAlgorithm == 1)
                        distance = euclideanDistance(centroids.get(j).getFeatures(), row.getFeatures());
                    else if (distanceAlgorithm == 2)
                        distance = manhattanDistance(centroids.get(j).getFeatures(), row.getFeatures());
                    else
                        return;
                    if (distance < minDistance) {
                        minDistance = distance;
                        centroidIndex = j;
                        row.setDistanceFromCentroid(minDistance);
                    }
                }
                clusters.get(centroidIndex).add(row);
                minDistance = 10000000;
            }
            List<Row> newCentroids = calculateNewCentroids(clusters);
            if(!changed(newCentroids, centroids)){
                break;
            }
            else{
                updateCentroids(newCentroids);
                updateClusters(newCentroids);
            }
       }
       detectOutliers();
       printClusters();
       printOutliers();
    }
}

