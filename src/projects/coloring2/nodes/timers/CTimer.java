package projects.coloring2.nodes.timers;

import projects.coloring2.nodes.nodeImplementations.CNode;
import projects.coloring2.nodes.messages.*;
import sinalgo.nodes.timers.Timer;

/*
 * Description de l'unique timer utilisŽ dans l'application
 */

public class CTimer extends Timer {
    CNode sender;
    int interval;

    public CTimer(CNode sender, int interval) {
        this.sender = sender;
        this.interval = interval;
    }

	/* La fonction "fire" est appelŽe lorsque le timer expire
	 */

    public void fire() {
        // The message will contain the whole spectrum of the node
        // The spectrum contains me, neighbours of the node and the neighbours of each neighbour of the node
        CMessage msg= new CMessage(sender.getNodeManager().getSpectrumManager());
        // le noeud envoie le message ˆ tous ses voisins
        sender.broadcast(msg);
        // le noeud relance un nouveau timer
        this.startRelative(interval, node); // recursive restart of the timer
    }
}