package projects.tdma.nodes.nodeImplementations;

/**
 * Created by ivanmorandi on 02/11/2017.
 * Interval is defined by 2 variables left (start) and right (end)
 * It is used in TDMA to define the intervals of time when a node can transmit
 */
public class Interval {
    //Left < right
    //Left is when the interval starts
    Double left;
    //Right when the interval ends
    Double right;

    public Interval(Double left, Double right){
        this.left = left;
        this.right = right;
    }

    public Double getLeft() {
        return left;
    }

    public void setLeft(Double left) {
        if(left <= this.right)
            this.left = left;
    }

    public Double getRight() {
        return right;
    }

    public void setRight(Double right) {
        if(right >= this.left)
            this.right = right;
    }

    /**
     * Get the length of the interval
     * @return
     */
    public double getLength(){
        return (this.right - this.left);
    }

    /**
     * Say if the intervals intersect between them (they overlaps for a specific period)
     * @param interval
     * @return
     */
    public boolean intersect(Interval interval){
        if(this.left > interval.right || this.right < interval.left)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof Interval))
            return false;

        Interval i = (Interval) obj;
        if(left.equals(i.left) && right.equals(i.right))
            return true;
        return false;
    }

    @Override
    public String toString() {
        return getLeft() + " " + getRight();
    }
}
