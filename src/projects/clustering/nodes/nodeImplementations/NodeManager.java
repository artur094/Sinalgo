package projects.clustering.nodes.nodeImplementations;

import projects.clustering.nodes.messages.CMessage;
import sinalgo.nodes.Connections;
import sinalgo.nodes.edges.Edge;

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
        int rand_color = (int)((Math.random()*colors.length)%colors.length);
        myself.setNewColor(rand_color);
        myself.setColor(myself.getNewColor());
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
     * It stores the received data and start the clustering algorithm
     * @param cMessage Received message from a neighbour
     * @param outConnections Neighbours of the node
     */
    public void parseMessage(CMessage cMessage, Connections outConnections){
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

        //Compute Clustering
        computeClustering(outConnections);

        //Check if clustering of cluster head of the node is less than the node's clustering
        //If yes, the node is clusterhead of itself
        if(this.getSpectrumManager().getMySelf().getClusterHeadClustering() < this.getSpectrumManager().getMySelf().getClustering()){
            this.getSpectrumManager().getMySelf().setClusterHead(this.getSpectrumManager().getMySelf().getId());
            this.getSpectrumManager().getMySelf().setClusterHeadClustering(this.getSpectrumManager().getMySelf().getClustering());
        }

        //Check if the neighbour who sent the packet can be the clusterhead of the node (if neighbour clustering is bigger than the one of node's cluster head
        //If yes, set the neighbour as clusterhead
        if(cMessage.getMySpectrumManager().getMySelf().getClustering() > this.getSpectrumManager().getMySelf().getClusterHeadClustering()){
            this.getSpectrumManager().getMySelf().setClusterHead(cMessage.getMySpectrumManager().getMySelf().getId());
            this.getSpectrumManager().getMySelf().setClusterHeadClustering(cMessage.getMySpectrumManager().getMySelf().getClustering());
            this.getSpectrumManager().getMySelf().setNewColor(cMessage.getMySpectrumManager().getMySelf().getColor());
        }
        //If the neighbour is cluster head of the node, then set the node color to the neighbour's one
        if(cMessage.getMySpectrumManager().getMySelf().getId() == this.getSpectrumManager().getMySelf().getClusterHead()){
            this.getSpectrumManager().getMySelf().setNewColor(cMessage.getMySpectrumManager().getMySelf().getColor());
        }

        //Update color
        this.getSpectrumManager().getMySelf().setColor(this.getSpectrumManager().getMySelf().getNewColor());

        //Update the spectrum with my new data
        this.getSpectrumManager().addMyNeighbours(this.getSpectrumManager().getMySelf());
    }

    /**
     * Compute clustering value of the node
     * To compute clustering, search for any triangles (neighbours of the node are connected together) and divide by the total number of node's links
     * @param outConnections Neighbours of the node
     */
    public void computeClustering(Connections outConnections){
        int edges = 0;

        //Foreach neighbour, I consider the neighbour's spectrum and I check the commong neighbours with it (intersection of neighbours)
        for(Integer key : this.getSpectrumManager().getMyNeighboursSpectrum().keySet()){
            //Get spectrum of the neighbour
            HashSet<NodeInfo> neighbours = this.getSpectrumManager().getMyNeighbourSpectrum(key).getMySpectrum();
            //Count intersections
            edges += intersection(neighbours, outConnections);
        }

        //Since I count twice each common edge, then I divide it by 2
        edges /= 2;

        //Then I add the number of edges between the node and neighbours (so 1 for each neighbour)
        edges += outConnections.size();
        //Then I set the cluster value to #edges / #neighbours
        this.getSpectrumManager().getMySelf().setClustering((double)(edges) / (double) outConnections.size());
    }

    /**
     * Count the number of intersections between 2 collections
     * @param neighbours_of_neighbour
     * @param outgoingConnections
     * @return
     */
    public int intersection(HashSet<NodeInfo> neighbours_of_neighbour, Connections outgoingConnections){
        //If no neighbours, then exit
        if(neighbours_of_neighbour == null)
            return 0;
        //It is easier to work with index, so better an arraylist compared to hashset
        ArrayList<Integer> my_neigh = new ArrayList<>();
        ArrayList<Integer> neigh_neigh = new ArrayList<>();

        int count = 0;

        //fill the array with data inside outgoingConnections (neighbours of the node)
        for (Edge e : outgoingConnections){
            my_neigh.add(e.endNode.ID);
        }

        //Fill the array of the neighbours of the neighbour
        for (NodeInfo neigh : neighbours_of_neighbour)
            neigh_neigh.add(neigh.getId());

        //Sort both arrays based on increasing IDs
        my_neigh.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        neigh_neigh.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        int i=0;
        int j=0;

        //Count the intersection between the 2 arrays of IDs
        for (; i<my_neigh.size() && j<neigh_neigh.size();){
            if(my_neigh.get(i).equals(neigh_neigh.get(j)) && ! my_neigh.get(i).equals(this.getSpectrumManager().getMySelf().getId()))
            {
                count++;
                i++;
                j++;
            }else if(my_neigh.get(i).compareTo(neigh_neigh.get(j)) < 0)
                i++;
            else
                j++;
        }
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
}
