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
 */
public class NodeManager {
    private SpectrumManager spectrumManager;
    private Color colors[] = {Color.BLUE,Color.CYAN,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.WHITE,Color.YELLOW};


    public NodeManager(NodeInfo myself, int number_neighbours){
        int rand_color = (int)((Math.random()*colors.length)%colors.length);
        myself.setNewColor(rand_color);
        myself.setColor(myself.getNewColor());
        spectrumManager = new SpectrumManager(myself, number_neighbours);
    }

    public Color getRGBColor(int color){
        return colors[color];
    }

    public void parseMessage(CMessage cMessage, Connections outConnections){
        SpectrumManager neighbourSpectrum = cMessage.getMySpectrumManager();

        //Update my spectrum with my neighbour info
        this.getSpectrumManager().addMyNeighbours(neighbourSpectrum.getMySelf());

        //Update spectrum of the neighbour
        this.getSpectrumManager().addMyNeighbourSpectrum(neighbourSpectrum.getMySelf(), neighbourSpectrum);

        //Check if the sender is my dominant and set the new color if present
        if(cMessage.getMySpectrumManager().getMySelf().getId() == this.getSpectrumManager().getMySelf().getDominator())
            this.getSpectrumManager().getMySelf().setNewColor(cMessage.getMySpectrumManager().getNeighbourFromMySpectrum(this.getSpectrumManager().getMySelf().getId()).getNewColor());

        computeClustering(outConnections);

        if(this.getSpectrumManager().getMySelf().getClusterHeadClustering() < this.getSpectrumManager().getMySelf().getClustering()){
            this.getSpectrumManager().getMySelf().setClusterHead(this.getSpectrumManager().getMySelf().getId());
            this.getSpectrumManager().getMySelf().setClusterHeadClustering(this.getSpectrumManager().getMySelf().getClustering());
        }

        //Search maximum clustering
        if(cMessage.getMySpectrumManager().getMySelf().getClustering() > this.getSpectrumManager().getMySelf().getClusterHeadClustering()){
            this.getSpectrumManager().getMySelf().setClusterHead(cMessage.getMySpectrumManager().getMySelf().getId());
            this.getSpectrumManager().getMySelf().setClusterHeadClustering(cMessage.getMySpectrumManager().getMySelf().getClustering());
            this.getSpectrumManager().getMySelf().setNewColor(cMessage.getMySpectrumManager().getMySelf().getColor());
        }
        if(cMessage.getMySpectrumManager().getMySelf().getId() == this.getSpectrumManager().getMySelf().getClusterHead()){
            this.getSpectrumManager().getMySelf().setNewColor(cMessage.getMySpectrumManager().getMySelf().getColor());
        }

        //Update color
        this.getSpectrumManager().getMySelf().setColor(this.getSpectrumManager().getMySelf().getNewColor());

        //Update the spectrum with my new data
        this.getSpectrumManager().addMyNeighbours(this.getSpectrumManager().getMySelf());
    }

    public void computeClustering(Connections outConnections){
        int edges = 0;

        //count edges in common between neighbours
        for(Integer key : this.getSpectrumManager().getMyNeighboursSpectrum().keySet()){
            HashSet<NodeInfo> neighbours = this.getSpectrumManager().getMyNeighbourSpectrum(key).getMySpectrum();
            edges += intersection(neighbours, outConnections);
        }

        edges /= 2;

        edges += outConnections.size();
        //System.out.println("Node " + this.getMe().getId() + " with #edges = " + edges + " and #neighbours = "+this.outgoingConnections.size() + " with #intersections = "+intersections);
        this.getSpectrumManager().getMySelf().setClustering((double)(edges) / (double) outConnections.size());
    }

    public int intersection(HashSet<NodeInfo> neighbours_of_neighbour, Connections outgoingConnections){
        if(neighbours_of_neighbour == null)
            return 0;

        ArrayList<Integer> my_neigh = new ArrayList<>();
        ArrayList<Integer> neigh_neigh = new ArrayList<>();

        int count = 0;

        for (Edge e : outgoingConnections){
            my_neigh.add(e.endNode.ID);
        }

        for (NodeInfo neigh : neighbours_of_neighbour)
            neigh_neigh.add(neigh.getId());

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
}
