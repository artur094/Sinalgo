package projects.tdma.nodes.nodeImplementations;

import projects.tdma.nodes.messages.CMessage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by ivanmorandi on 06/11/2017.
 * NodeManager is the logical component of the node
 * NodeManager is based on SpectrumManager which contains all important data
 * Each node has a NodeManager
 */
public class NodeManager {
    //SpectrumManager of the node
    private SpectrumManager spectrumManager;
    //Set of colors used for the graph
    private Color colors[] = {Color.BLUE,Color.CYAN,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.WHITE,Color.YELLOW};

    /**
     *
     * @param myself NodeInfo of the node
     * @param number_neighbours Number of neighbours of the node
     */
    public NodeManager(NodeInfo myself, int number_neighbours){
        spectrumManager = new SpectrumManager(myself, number_neighbours);
    }

    /**
     * Return the RGB color based on the color ID (index of the colors array)
     * @param color
     * @return
     */
    public Color getRGBColor(int color){
        return colors[color];
    }

    /**
     * Most important function of NodeManager, it is called when the node receives a message.
     * It stores the received data and start MIS and Coloring algorithm
     * @param cMessage Received message from a neighbour
     */
    public void parseMessage(CMessage cMessage){
        //Take the spectrumManager of the neighbour (which contains all information)
        SpectrumManager neighbourSpectrum = cMessage.getMySpectrumManager();

        //Update my spectrum with my neighbour info
        this.getSpectrumManager().addMyNeighbours(neighbourSpectrum.getMySelf());

        //Update spectrum of the neighbour
        this.getSpectrumManager().addMyNeighbourSpectrum(neighbourSpectrum.getMySelf(), neighbourSpectrum);

        //Check if the sender is my dominant
        //If sender is dominant, then set which will be the new color of the node (it will be updated at the end)
        if(cMessage.getMySpectrumManager().getMySelf().getId() == this.getSpectrumManager().getMySelf().getDominator())
            this.getSpectrumManager().getMySelf().setNewColor(cMessage.getMySpectrumManager().getNeighbourFromMySpectrum(this.getSpectrumManager().getMySelf().getId()).getNewColor());

        //Start MIS algorithm
        computeMIS();
        //Compute colors (if not dominator, the compute colors will exit immediately)
        computeColors();

        //Update the node's color with the new (if not changed, it will be the same)
        this.getSpectrumManager().getMySelf().setColor(this.getSpectrumManager().getMySelf().getNewColor());

        //Update the spectrum with my new data
        this.getSpectrumManager().addMyNeighbours(this.getSpectrumManager().getMySelf());

        //Set the node base with the one computed by the function getBase()
        this.getSpectrumManager().getMySelf().setColorBase(this.getBase());

        //Compute TDMA algorithm
        computeTDMA();

        //Print all intervals where the node can transmit on standard output
        System.out.print("Node " + this.getSpectrumManager().getMySelf().getId() + " with base " + this.getSpectrumManager().getMySelf().getColorBase() + " ");
        for (Interval i : this.getSpectrumManager().getMySelf().getIntervals())
            System.out.print(i.toString() + ", ");
        System.out.println();
    }

    /**
     * It start the MIS algorithm.
     * A node is a dominator, when there all nodes with a greater ID are not dominator
     * If a node has a neighbour with a greater ID and it is a dominator, then it may be the node's dominator
     * The node will choose the neighbour with greates ID between the dominators
     */
    public void computeMIS(){
        //Set the node as a dominator
        Integer my_dominant = this.getSpectrumManager().getMySelf().getId();

        //Scan each neighbour (distance 1)
        for(NodeInfo nd : getSpectrumManager().getMySpectrum()){
            //If there is a neighbour with a greater ID than mine
            if(nd.getId() > this.getSpectrumManager().getMySelf().getId()){
                //I check if it is a dominator
                //If yes, I check if its ID is bigger than the node's dominator
                //If still yes, then I update the node's dominator with the new ID
                if(nd.isDominant()){
                    if(my_dominant < nd.getId())
                        my_dominant = nd.getId();
                }
            }
        }

        //Set dominator of the node (if not changed, it will be the node itself)
        this.getSpectrumManager().getMySelf().setDominator(my_dominant);
    }

    /**
     * Compute the colors for each dominated neighbour.
     * It is only executed if the node is a dominator
     */
    public void computeColors(){
        //If node is not a dominator, then exit
        if(! this.getSpectrumManager().getMySelf().isDominant()){
            return;
        }

        //Get all nodes contained in SpectrumManager
        HashSet<NodeInfo> spectrum = this.getSpectrumManager().getWholeSpectrum();

        //Initialize the array of used colors (automatically set to false)
        boolean[] used_colors = new boolean[colors.length];

        //Search for used colors
        //A color is used if the node which has that color is dominated (even by itself) by a node with greater ID then the node which is executing the algorithm
        //Then, for each node in the spectrum
        for (NodeInfo nd : spectrum){
            //If dominator ID is bigger than the node executing the algorithm
            //Then set the used_colors of the neighbour and of its dominator as True
            if(nd.getDominator() > this.getSpectrumManager().getMySelf().getId()){
                used_colors[nd.color] = true;
                used_colors[nd.getDominatorColor()] = true;
            }
        }

        //Check if my color is used by someone else with higher priority
        //If yes, set new color of the node to an available color
        //If there aren't any available colors, then exit and print the error on the console
        //If both cases (the color was available or not), set the actual color as used
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

        //Foreach neighbours, I search for dominated nodes of the one executing the algorithm
        //Foreach dominated nodes, do the same operation done before for the node color
        //Check if used and if yes, change it with a new one and set the color as used
        //If not, set the actual color as used
        //If no available colors, print on the console the error and exit
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

    /**
     * Compute TDMA algorithm by searching available intervals of time
     * To compute available intervals, it check neighbours (distance 2) which have small color base (number of colors distance N2) or if equals, it checks if neighbours have a greater color ID
     * Then, from these neighbours, it takes their intervals and consider them as time not available
     * After having the time not available, with a difference we can get the available time
     * From available time, we can find the spots where the node can transmit (with the limit that the transmission can last maximum 1/color_base as maximum)
     */
    public void computeTDMA(){
        //Initialize set of intervals
        HashSet<Interval> intervals = new HashSet<>();

        //Get neighbours at maximum distance 2
        HashSet<NodeInfo> tmp = this.getSpectrumManager().getSpectrumDitansce2();

        //Foreach node, consider the one with lower color base (or if equal, with smaller color ID)
        //And add the intervals of these nodes to the set 'intervals'
        for (NodeInfo nd : tmp){
            if(nd.getColorBase() > this.getSpectrumManager().getMySelf().getColorBase() ||
                    (nd.getColorBase().equals(this.getSpectrumManager().getMySelf().getColorBase()) && nd.getColor() < this.getSpectrumManager().getMySelf().getColor())){
                intervals.addAll(nd.getIntervals());
            }
        }
        //Clear intervals of the node executing the algorithm
        this.getSpectrumManager().getMySelf().clearIntervals();
        //Assign intervals obtained from the function G (which returns the available intervals with a limit of 1/color_base)
        this.getSpectrumManager().getMySelf().addIntervals(g(this.getSpectrumManager().getMySelf().getColorBase(), intervals));
    }

    /**
     * Compute the color base of the node (TDMA)
     * The color base is the number of colors used at distance 2
     * @return
     */
    public Integer getBase(){
        Integer count = 0;
        //Count the number of colors used
        for(NodeInfo nd : this.getSpectrumManager().getSpectrumDitansce2())
            count++;
        return count;
    }

    /**
     * Return the SpectrumManager of the node
     * @return
     */
    public SpectrumManager getSpectrumManager() {
        return spectrumManager;
    }

    /**
     * Set the SpectrumManager of the node with the given one
     * @param spectrumManager
     */
    public void setSpectrumManager(SpectrumManager spectrumManager) {
        this.spectrumManager = spectrumManager;
    }

    /**
     * Return an available color based on the given array
     * @param used_colors
     * @return
     */
    public int freeColors(boolean[] used_colors){
        for(int i=0;i<used_colors.length;i++){
            if(!used_colors[i])
                return i;
        }
        return -1;
    }

    /**
     * This function return a list available intervals for the node
     * The sums of all intervals must be lower than 1/color_base
     * Total time range from 0 to 1
     * @param base
     * @param S
     * @return
     */
    private HashSet<Interval> g(Integer base, HashSet<Interval> S){
        //Return all free interval which are not used at distance N2

        //Return the biggest interval great up to 1/base
        //How:
        //1 - Convert the hashset into an arraylist
        //2 - Sort the arraylist
        //3 - Get free intervals from the arraylist
        //4 - Store the number of intervals such that I will get the maximum time possible (constrained by 1/base)

        //Compute the maximum size of intervals
        Double max_interval = 1.0/(double)base;

        //Initialize the set of intervals
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

        //Set the start of the free interval (left)
        Double free_interval_left = 0.0;
        //For each busy interval
        for(Interval i : intervals){
            //If there is an available gap (so the interval start later)
            //Then add the free interval, where the right (end) is equal to the left of the busy interval
            if(i.getLeft() > free_interval_left){
                free_intervals.add(new Interval(free_interval_left, i.getLeft()));
            }
            //If free_interval_left is inside the busy interval, then set it to the end of the busy interval
            free_interval_left = i.getRight();
        }
        //Check the last gap, if from the last busy interval to 1 there is a free interval
        //If yes, add it
        if(free_interval_left < 1.0)
            free_intervals.add(new Interval(free_interval_left, 1.0));

        //4 - Store the number of intervals such that I will get the maximum time possible (constrained by 1/base)

        //Search for an interval > than 1/base (so it will be contiguous)
        for (Interval i : free_intervals){
            if(i.getLength() >= max_interval){
                //Reached maximum size of time, so I can leave the function
                final_intervals.add(new Interval(i.getLeft(), i.getLeft()+max_interval));
                return final_intervals;
            }
        }

        //Otherwise, search for non contiguous intervals
        for (Interval i : free_intervals){
            //If the interval is bigger than what the remaining time
            if(i.getLength() >= max_interval){
                //Add an interval long as the remaining time and then leave the function
                final_intervals.add(new Interval(i.getLeft(), max_interval));
                break;
            }else{
                //Otherwise add the interval and decrease the remaining time
                final_intervals.add(i);
                max_interval -= i.getLength();
            }
        }
        //Return the set
        return final_intervals;
    }
}
