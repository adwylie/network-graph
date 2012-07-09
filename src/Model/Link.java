package Model;
// The link class represents a communications link between two (vertices)
// networked nodes in a plane.

public class Link implements WeightedEdgeInterface {
    
    private String name = "";
    private float weight = 0f;
    
    public int compareTo(WeightedEdgeInterface o) {
        // Round away from zero to keep correct ordering.
        if ((this.getWeight() - o.getWeight()) > 0f) {
            return 1;
        } else if ((this.getWeight() - o.getWeight()) < 0f) {
            return -1;
        } else {
            return 0;
        }
    }

    public float getWeight() {
        return weight;
    }
    
    public void setWeight(float weight) {
        this.weight = weight;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

}
