package projects.clustering.nodes.timers;

import projects.coloring_distance2_2.nodes.messages.MMessage;
import projects.coloring_distance2_2.nodes.nodeImplementations.CNode;
import sinalgo.nodes.timers.Timer;

/*
 * Description de l'unique timer utilisŽ dans l'application
 */

public class MTimer extends Timer {
    CNode sender;
    int interval;

    public MTimer(CNode sender, int interval) {
        this.sender = sender;
        this.interval = interval;
    }

	/* La fonction "fire" est appelŽe lorsque le timer expire
	 */

    public void fire() {
        // le noeud crŽe un message contenant sa couleur
        MMessage msg= new MMessage(sender.ID,sender.getDominator());
        // le noeud envoie le message ˆ tous ses voisins
        sender.broadcast(msg);
        // le noeud relance un nouveau timer
        this.startRelative(interval, node); // recursive restart of the timer
    }
}