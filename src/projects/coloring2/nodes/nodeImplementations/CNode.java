package projects.coloring2.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.coloring2.nodes.timers.*;
import projects.coloring2.nodes.messages.*;
import sinalgo.nodes.messages.Message;


public class CNode extends Node {

    //NodeManager of the node
    private NodeManager nodeManager;

    //Getter of NodeManager
    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public void handleMessages(Inbox inbox) {
        //Each received message is given as input to NodeManager of the node

        if(inbox.hasNext()==false) return;

        while(inbox.hasNext()){

            Message msg=inbox.next();

            if(msg instanceof CMessage){
                //Give the received message to nodeManager
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
        //Initialize the NodeInfo with information of the node (default color: 0)
        NodeInfo me = new NodeInfo(this.ID, this.ID, 0, this.outgoingConnections.size());
        //Initialize nodeManager with the created NodeInfo and the number of neighbours
        this.nodeManager = new NodeManager(me, this.outgoingConnections.size());

        (new CTimer(this, 50)).startRelative(50, this);
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
        //Each node in the graph has a text formatted in a specific way
        //If dominator: NODEID + "D" + NODEID (e.g., 3D3, 3 is a dominator)
        //Otherwise: NODEID + "-" + DOMINATOR (e.g., 1-5, 1 is dominated by 5)
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
}