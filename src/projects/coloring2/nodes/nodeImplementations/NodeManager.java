package projects.coloring2.nodes.nodeImplementations;

import projects.coloring2.nodes.messages.CMessage;

import java.awt.*;
import java.util.HashSet;

/**
 * Created by ivanmorandi on 06/11/2017.
 * NodeManager is the logical component of the node (it computes all algorithm, in this case MIS and Coloring protocol distance 2
 * NodeManager uses SpectrumManager which contains all important information
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
                //If yes, then I update the node's dominator with the new ID
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
     * Compute the colors for itself and the dominated nodes.
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
}
