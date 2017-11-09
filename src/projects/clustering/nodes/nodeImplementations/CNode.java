package projects.clustering.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.clustering.nodes.timers.*;
import projects.clustering.nodes.messages.*;
import sinalgo.nodes.messages.Message;



public class CNode extends Node {

    private NodeManager nodeManager;

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public void handleMessages(Inbox inbox) {

        if(inbox.hasNext()==false) return;

        while(inbox.hasNext()){

            Message msg=inbox.next();

            if(msg instanceof CMessage){

                nodeManager.parseMessage((CMessage)msg, this.outgoingConnections);
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
        if(this.getNodeManager().getSpectrumManager().getMySelf().getClusterHead().equals(this.getNodeManager().getSpectrumManager().getMySelf().getId())){
            text += " H";
        }
        else
            text += " C"+this.getNodeManager().getSpectrumManager().getMySelf().getClusterHead();
        c=Color.BLACK;

        super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, c);
    }
}