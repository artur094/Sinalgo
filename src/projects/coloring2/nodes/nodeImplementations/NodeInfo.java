package projects.coloring2.nodes.nodeImplementations;

/**
 * Created by ivanmorandi on 05/10/2017.
 */
public class NodeInfo{
    Integer id;
    Integer dominator;
    Integer color;
    Integer new_color;
    Integer dominator_color;
    Integer number_neighbours;

    public NodeInfo(int id, int dominator, int color, int neighbours){
        this.id = id;
        this.dominator = dominator;
        this.color = color;
        this.new_color = color;
        this.dominator_color = 0;
        this.number_neighbours = neighbours;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDominant(){ return (getId() == getDominator());}

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

    public Integer getDominatorColor() {
        return dominator_color;
    }

    public void setDominatorColor(Integer dominator_color) {
        this.dominator_color = dominator_color;
    }

    public Integer getNewColor() {
        return new_color;
    }

    public void setNewColor(Integer new_color) {
        this.new_color = new_color;
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

