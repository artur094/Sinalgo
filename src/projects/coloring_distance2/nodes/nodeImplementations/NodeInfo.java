package projects.coloring_distance2.nodes.nodeImplementations;

import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by ivanmorandi on 05/10/2017.
 */
public class NodeInfo{
    Integer id;
    Integer dominator;
    Integer color;
    Integer preferred_color;
    Integer dominator_preferred_color; //??
    HashSet<NodeInfo> spectrum;

    Hashtable<Integer, HashSet<NodeInfo>> neighbour_spectrum;

    Integer dominator_color; //color of my dominator

    public NodeInfo(int id, int dominator, int color, int neighbours){
        this.id = id;
        this.dominator = dominator;
        this.color = color;
        this.dominator_color = 0;
        this.preferred_color = 0;

        this.spectrum = new HashSet<>(neighbours);
        this.neighbour_spectrum = new Hashtable<>(neighbours);
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

    public Integer getPreferredColor() {
        return preferred_color;
    }

    public void setPreferredColor(Integer preferred_color) {
        this.preferred_color = preferred_color;
    }

    public Integer getDominatorPreferred_color() {
        return dominator_preferred_color;
    }

    public void setDominatorPreferredColor(Integer dominator_preferred_color) {
        this.dominator_preferred_color = dominator_preferred_color;
    }

    public HashSet<NodeInfo> getNeighbourSpectrum(Integer node_id){
        return this.neighbour_spectrum.get(node_id);
    }

    public void setNeighbourSpectrum(Integer node_id, HashSet<NodeInfo> node_spectrum){
        if(this.neighbour_spectrum.contains(node_id))
            this.neighbour_spectrum.remove(node_id);
        this.neighbour_spectrum.put(node_id, node_spectrum);
    }


    public HashSet<NodeInfo> getSpectrum() {
        return spectrum;
    }

    public void updateSpectrum(NodeInfo node){
        if(this.spectrum.contains(node))
            this.spectrum.remove(node);
        this.spectrum.add(node);
    }

    public void updateSpectrum(Integer id, int dominator, int color, int dominator_color, int number_neighbours){
        for(NodeInfo node : this.spectrum){
            if(node.getId() == id){
                node.setColor(color);
                node.setDominator(dominator);
                node.setDominatorColor(dominator_color);
                return;
            }
        }

        NodeInfo nd = new NodeInfo(id, dominator, color, number_neighbours);
        this.spectrum.add(nd);
    }

    public void setSpectrum(HashSet<NodeInfo> spectrum) {
        this.spectrum = spectrum;
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

