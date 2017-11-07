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

    public void handleMessages(Inbox inbox) {

        if(inbox.hasNext()==false) return;

        while(inbox.hasNext()){

            Message msg=inbox.next();

            if(msg instanceof CMessage){

                nodeManager.parseMessage((CMessage)msg);
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
        NodeInfo me = new NodeInfo(this.ID, this.ID, 0, this.outgoingConnections.size());
        this.nodeManager = new NodeManager(me, this.outgoingConnections.size());

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
        this.setColor(this.nodeManager.getRGBColor(this.nodeManager.getSpectrumManager().getMySelf().getColor()));

        String text = ""+this.ID;
        if(this.getNodeManager().getSpectrumManager().getMySelf().isDominant()) {
            text += "D";
            text += this.getNodeManager().getSpectrumManager().getMySelf().getDominator();
        }
        else{
            text+="-";
            text+=this.getNodeManager().getSpectrumManager().getMySelf().getDominator();
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