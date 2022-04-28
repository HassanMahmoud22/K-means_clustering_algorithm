import java.util.ArrayList;
import java.util.List;

public class Row {
    public List<Double> features = new ArrayList<>();
    int id;
    double distanceFromCentroid;
    Row(){
        id = 0;
        distanceFromCentroid = 0;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setFeature(double feature){
        this.features.add(feature);
    }
    public void setFeatures(List<Double> features)
    {
        this.features.clear();
        for(int i = 0; i < features.size(); i++){
            this.features.add(i,features.get(i));
        }
    }
    public List<Double> getFeatures(){
        return this.features;
    }
    public int getFeaturesSize(){
        return this.features.size();
    }

    public void setDistanceFromCentroid(double distanceFromCentroid) {
        this.distanceFromCentroid = distanceFromCentroid;
    }

    public double getDistanceFromCentroid(){
        return distanceFromCentroid;
    }
    @Override
    public String toString(){
        String transactionData = "id: " + id + ", Distance from centroid = " + distanceFromCentroid + ", features [ ";
        for (Double feature : features) {
            transactionData += feature + ", ";
        }
        transactionData += "]\n";
        return transactionData;
    }

}
