package projects.coloring_distance2_new.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import projects.coloring_distance2_new.nodes.nodeImplementations.NodeInfo;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.coloring_distance2_new.nodes.timers.*;
import projects.coloring_distance2_new.nodes.messages.*;
import sinalgo.nodes.messages.Message;



public class CNode extends Node {

    private NodeManager nodeManager;

    private final int nb = 10;
    private final Color tab[] = {Color.BLUE,Color.CYAN,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.WHITE,Color.YELLOW};

    //--------------------------------


    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public void initNodeColor(int range){
        this.nodeManager.getSpectrumManager().getMySelf().setColor(0);
        //setNode_color((int) (Math.random() * range) % range);
    }

    public ArrayList<Integer> getDominatedNodes(){

        ArrayList<Integer> dominated = new ArrayList<>();

        for (NodeInfo nodeInfo : this.me.getSpectrum()) {
            Integer dominant = nodeInfo.getDominator();

            if(dominant != null && dominant == this.ID)
                dominated.add(nodeInfo.getId());
        }
        if(this.me.isDominant())
            dominated.add(this.ID);

        return dominated;
    }

    /*
     * La fonction "compute" est lancŽe ˆ chaque rŽception
     * de message. Elle permet de changer la color du noeud si nŽcessaire
     */
    public void computeColor(int dominator, int color){
        if(dominator == this.me.getDominator()){

        }
    }

    /* La fonction ci-dessous est appelŽe
     * ˆ chque rŽception de message.
     */
    public void handleMessages(Inbox inbox) {

        if(inbox.hasNext()==false) return;

        while(inbox.hasNext()){

            Message msg=inbox.next();

            if(msg instanceof CMessage){
                //Update the spectrum
                updateSpectrum(((CMessage) msg).id, ((CMessage) msg).dominator, ((CMessage) msg).color, ((CMessage) msg).dominator_color, ((CMessage) msg).number_neighbours);

                //Update neighbor spectrum
                this.me.setNeighbourSpectrum(((CMessage) msg).id, ((CMessage) msg).spectrum);

                HashSet<NodeInfo> whole_spectrum = new HashSet<>();

                for (Integer key : ((CMessage) msg).myself.getNeighbour_spectrum().keySet()){
                    whole_spectrum.addAll(((CMessage) msg).myself.getNeighbourSpectrum(key));
                }
                whole_spectrum.addAll(((CMessage) msg).spectrum);
                whole_spectrum.addAll(getSpectrum());

                computeMIS(whole_spectrum);

                //Update my color if the msg comes from my leader

                if(((CMessage) msg).id == this.getDominator()){
                    for(NodeInfo nd : ((CMessage) msg).spectrum){
                        if(nd.getId() == this.ID){
                            if(nd.getPreferredColor() != null)
                            {
                                this.me.setColor(nd.getPreferredColor());

                                //TODO: remove useless int c
                                int c;
                            }
                        }
                    }
                }

                //Update my info in the spectrum

                Integer color = this.getMe().getDominatorColor();
                if(((CMessage) msg).id == this.getDominator())
                    color = ((CMessage) msg).color;

                updateSpectrum(this.ID, this.me.getDominator(), this.me.getColor(), color, 0);
            }


            System.gc();
            System.runFinalization();
        }

    }

    public void preStep() {}

	/*
	 * La fonction ci-dessous est appelŽe au dŽmarrage uniquement
	 * On initialise la color du noeud au hasard
	 * On charge le premier timer
	 * On crŽe la table de hachage
	 */

    public void init() {
        me = new NodeInfo(this.ID, this.ID, 0, this.outgoingConnections.size());

        this.initNodeColor(nb);

        (new CTimer(this, 50)).startRelative(50, this);
        //(new CTimer(this, 50)).startRelative(50, this);
    }

    public void neighborhoodChange() {}

    public void postStep() {}

    public String toString() {
        String s = "Node(" + this.ID + ") [";
        Iterator<Edge> edgeIter = this.outgoingConnections.iterator();
        while(edgeIter.hasNext()){
            Edge e = edgeIter.next();
            Node n = e.endNode;
            s+=n.ID+" ";
        }
        return s + "]";
    }

    public void checkRequirements() throws WrongConfigurationException {}

	/*
	 * La fonction ci-dessous affiche le noeud
	 */

    public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
        Color c;
        this.setColor(this.RGBColor());

        String text = ""+this.ID;
        if(this.me.isDominant()) {
            text += "D";
            text += this.me.getDominator();
        }
        else{
            text+="-";
            text+=this.me.getDominator();
        }
        c=Color.BLACK;

        super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, c);
    }

    public int freeColors(boolean[] used_colors){
        for(int i=0;i<used_colors.length;i++){
            if(!used_colors[i])
                return i;
        }
        return -1;
    }


}