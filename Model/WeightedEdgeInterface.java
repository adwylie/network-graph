package Model;

public interface WeightedEdgeInterface
        extends EdgeInterface, Comparable<WeightedEdgeInterface> {
    float getWeight();
    void setWeight(float weight);
}
