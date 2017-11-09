package projects.tdma.nodes.nodeImplementations;

import projects.tdma.nodes.messages.CMessage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by ivanmorandi on 06/11/2017.
 */
public class NodeManager {
    private SpectrumManager spectrumManager;
    private Color colors[] = {Color.BLUE,Color.CYAN,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.WHITE,Color.YELLOW};


    public NodeManager(NodeInfo myself, int number_neighbours){
        spectrumManager = new SpectrumManager(myself, number_neighbours);
    }

    public Color getRGBColor(int color){
        return colors[color];
    }

    public void parseMessage(CMessage cMessage){
        SpectrumManager neighbourSpectrum = cMessage.getMySpectrumManager();

        //Update my spectrum with my neighbour info
        this.getSpectrumManager().addMyNeighbours(neighbourSpectrum.getMySelf());

        //Update spectrum of the neighbour
        this.getSpectrumManager().addMyNeighbourSpectrum(neighbourSpectrum.getMySelf(), neighbourSpectrum);

        //Check if the sender is my dominant and set the new color if present
        if(cMessage.getMySpectrumManager().getMySelf().getId() == this.getSpectrumManager().getMySelf().getDominator())
            this.getSpectrumManager().getMySelf().setNewColor(cMessage.getMySpectrumManager().getNeighbourFromMySpectrum(this.getSpectrumManager().getMySelf().getId()).getNewColor());

        computeMIS();
        computeColors();

        //Update color
        this.getSpectrumManager().getMySelf().setColor(this.getSpectrumManager().getMySelf().getNewColor());

        //Update the spectrum with my new data
        this.getSpectrumManager().addMyNeighbours(this.getSpectrumManager().getMySelf());

        this.getSpectrumManager().getMySelf().setColorBase(this.getBase());

        computeTDMA();

        System.out.print("Node " + this.getSpectrumManager().getMySelf().getId() + " with base " + this.getSpectrumManager().getMySelf().getColorBase() + " ");
        for (Interval i : this.getSpectrumManager().getMySelf().getIntervals())
            System.out.print(i.toString() + ", ");
        System.out.println();
    }

    public void computeMIS(){
        //COMPUTE MIS
        //I say that I am the dominant
        Integer my_dominant = this.getSpectrumManager().getMySelf().getId();

        for(NodeInfo nd : getSpectrumManager().getMySpectrum()){
            //System.out.println("Checking: "+nd.getId()+nd.getDominator());
            if(nd.getId() > this.getSpectrumManager().getMySelf().getId()){
                if(nd.isDominant()){
                    if(my_dominant < nd.getId())
                        my_dominant = nd.getId();
                }
            }
        }

        this.getSpectrumManager().getMySelf().setDominator(my_dominant);

        //System.out.println(this.getSpectrumManager().getMySelf().getId() + " " + this.getSpectrumManager().getMySelf().getDominator());
    }

    public void computeColors(){
        if(! this.getSpectrumManager().getMySelf().isDominant()){
            return;
        }

        HashSet<NodeInfo> spectrum = this.getSpectrumManager().getWholeSpectrum();

        boolean[] used_colors = new boolean[colors.length];

        //Set used colors
        for (NodeInfo nd : spectrum){
            if(nd.getDominator() > this.getSpectrumManager().getMySelf().getId()){
                used_colors[nd.color] = true;
                used_colors[nd.getDominatorColor()] = true;
            }
        }

        //Check if my color is used by someone else with higher priority
        //If yes, change it
        if(used_colors[getSpectrumManager().getMySelf().getColor()]){
            int free_color = freeColors(used_colors);

            if(free_color < 0){
                System.out.println("No free colors");
                return;
            }

            this.getSpectrumManager().getMySelf().setNewColor(free_color);
            used_colors[free_color] = true;
        }else{
            used_colors[this.getSpectrumManager().getMySelf().getColor()] = true;
        }

        //Set colors of my dominated colors
        for(NodeInfo neighbour : this.getSpectrumManager().getMySpectrum()){
            if(neighbour.getDominator() == this.getSpectrumManager().getMySelf().getId() && neighbour.getId() != this.getSpectrumManager().getMySelf().getId()){
                if(used_colors[neighbour.getColor()]){
                    int free_color = freeColors(used_colors);

                    if(free_color < 0){
                        System.out.println("No free colors");
                        return;
                    }

                    neighbour.setNewColor(free_color);
                    used_colors[free_color] = true;
                }else{
                    used_colors[neighbour.getColor()] = true;
                }
            }
        }
    }

    public void computeTDMA(){
        HashSet<Interval> intervals = new HashSet<>();

        HashSet<NodeInfo> tmp = this.getSpectrumManager().getSpectrumDitansce2();

        for (NodeInfo nd : tmp){
            if(nd.getColorBase() > this.getSpectrumManager().getMySelf().getColorBase() ||
                    (nd.getColorBase().equals(this.getSpectrumManager().getMySelf().getColorBase()) && nd.getColor() < this.getSpectrumManager().getMySelf().getColor())){
                intervals.addAll(nd.getIntervals());
            }
        }
        this.getSpectrumManager().getMySelf().clearIntervals();
        this.getSpectrumManager().getMySelf().addIntervals(g(this.getSpectrumManager().getMySelf().getColorBase(), intervals));
    }

    public Integer getBase(){
        Integer count = 0;
        for(NodeInfo nd : this.getSpectrumManager().getSpectrumDitansce2())
            count++;
        return count;
    }

    public SpectrumManager getSpectrumManager() {
        return spectrumManager;
    }

    public void setSpectrumManager(SpectrumManager spectrumManager) {
        this.spectrumManager = spectrumManager;
    }

    public int freeColors(boolean[] used_colors){
        for(int i=0;i<used_colors.length;i++){
            if(!used_colors[i])
                return i;
        }
        return -1;
    }

    private HashSet<Interval> g(Integer base, HashSet<Interval> S){
        //Return all free interval which are not used at distance N2
        //Maybe I can just return 1 interval (?), the biggest with the constraint that interval is less than 1/base

        //Let's try:
        //Return the biggest interval great up to 1/base
        //How:
        //1 - Convert the hashset into an arraylist
        //2 - Sort the arraylist
        //3 - Get free intervals from the arraylist
        //4 - Store the number of intervals such that I will get the maximum time possible (constrained by 1/base)

        Double max_interval = 1.0/(double)base;
        HashSet<Interval> final_intervals = new HashSet<>();

        //1 - Convert the hashset into an arraylist
        ArrayList<Interval> intervals = new ArrayList<>();
        intervals.addAll(S);

        //2 - Sort the arraylist
        intervals.sort(new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                return o1.getLeft().compareTo(o2.getLeft());
            }
        });

        //3 - Get free intervals from the arraylist
        ArrayList<Interval> free_intervals = new ArrayList<>();

        Double free_interval_left = 0.0;
        for(Interval i : intervals){
            if(i.getLeft() > free_interval_left){
                free_intervals.add(new Interval(free_interval_left, i.getLeft()));
            }
            free_interval_left = i.getRight();
        }
        if(free_interval_left < 1.0)
            free_intervals.add(new Interval(free_interval_left, 1.0));

        //4 - too long to rewrite

        //search for an interval > than 1/base (so it will be contiguous
        for (Interval i : free_intervals){
            //System.out.println("Node " + this.getSpectrumManager().getMySelf().getId() + " interval " + i.toString() + " with base " + this.getSpectrumManager().getMySelf().getColorBase());
            if(i.getLength() >= max_interval){
                //Reached maximum size of time
                final_intervals.add(new Interval(i.getLeft(), i.getLeft()+max_interval));
                return final_intervals;
            }
        }

        //otherwise, search for non contiguous intervals
        for (Interval i : free_intervals){
            if(i.getLength() >= max_interval){
                final_intervals.add(new Interval(i.getLeft(), max_interval));
                break;
            }else{
                final_intervals.add(i);
                max_interval -= i.getLength();
            }
        }

        return final_intervals;
    }
}
