package projects.coloring_distance2_2.nodes.timers;

import projects.coloring_distance2_2.nodes.nodeImplementations.CNode;
import projects.coloring_distance2_2.nodes.messages.*;
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
        // le noeud crŽe un message contenant sa couleur
        CMessage msg= new CMessage(sender.ID,sender.getMe().getColor(), sender.getDominator(), sender.getMe().getDominatorColor(), sender.getNumberNeighbours(), sender.getMe().getSpectrum(), sender.getMe());
        // le noeud envoie le message ˆ tous ses voisins
        sender.broadcast(msg);
        // le noeud relance un nouveau timer
        this.startRelative(interval, node); // recursive restart of the timer
    }
}