package projects.clustering_new.nodes.nodeImplementations;

import projects.clustering_new.nodes.messages.CMessage;
import projects.clustering_new.nodes.nodeImplementations.*;

import java.awt.*;
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
