

package projects.coloring_distance2.nodes.nodeImplementations;

/**
 * Created by ivanmorandi on 05/10/2017.
 */
public class NodeInfo{
    Integer id;
    Integer dominator;
    Integer color;

    public NodeInfo(int id, int dominator, int color){
        this.id = id;
        this.dominator = dominator;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDominator() {
        return dominator;
    }

    public void setDominator(int dominator) {
        this.dominator = dominator;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof NodeInfo){
            if(((NodeInfo) obj).getId() == this.getId())
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Node "+this.getId()+" dominated by "+this.getDominator()+" with color "+this.getColor();
    }
}
