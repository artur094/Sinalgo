package projects.tdma.nodes.nodeImplementations;

import java.util.HashSet;

/**
 * Created by ivanmorandi on 05/10/2017.
 * NodeInfo contains the most important data of the node itself
 */
public class NodeInfo{
    //ID of the node
    Integer id;
    //ID of the dominator of the node (MIS)
    Integer dominator;
    //Number of different colors in N2 (used for TDMA)
    Integer color_base;
    //Color ID of the node
    Integer color;
    //Color ID of future color of the node
    Integer new_color;
    //Color ID of the dominator
    Integer dominator_color;
    //Number of neighbours
    Integer number_neighbours;
    //Intervals where the node can transmit
    HashSet<Interval> intervals;

    /**
     * At the beginning, the node set the dominator as itself
     * @param id
     * @param dominator
     * @param color
     * @param neighbours
     */
    public NodeInfo(int id, int dominator, int color, int neighbours){
        this.id = id;
        this.dominator = dominator;
        this.color = color;
        this.new_color = color;
        this.color_base = 1;
        this.dominator_color = 0;
        this.number_neighbours = neighbours;

        this.intervals = new HashSet<>();
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

    public Integer getColorBase() {
        return color_base;
    }

    public void setColorBase(Integer color_base) {
        this.color_base = color_base;
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

    public HashSet<Interval> getIntervals() {
        return intervals;
    }

    public void setIntervals(HashSet<Interval> intervals) {
        this.intervals = intervals;
    }

    /**
     * Add interval to the set of intervals
     * @param left
     * @param right
     */
    public void addInterval(Double left, Double right){
        Interval i = new Interval(left, right);
        this.intervals.add(i);
    }

    /**
     * Add interval to the set of intervals
     * @param interval
     */
    public void addInterval(Interval interval){
        this.intervals.add(interval);
    }

    /**
     * Add more intervals to the set of intervals
     * @param intervals
     */
    public void addIntervals(HashSet<Interval> intervals){
        this.intervals.addAll(intervals);
    }

    /**
     * Remove all elements from the set of intervals
     */
    public void clearIntervals(){
        this.intervals.clear();
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

