package projects.clustering.nodes.timers;

import projects.clustering.nodes.messages.C2Message;
import projects.clustering.nodes.nodeImplementations.CNode;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.timers.Timer;

import java.util.Hashtable;

/*
 * Description de l'unique timer utilisŽ dans l'application
 */

public class C2Timer extends Timer {
    CNode sender;
    int interval;
    Hashtable<Integer, Integer> preferred_colors;

    public C2Timer(CNode sender, int interval, Hashtable<Integer, Integer> preferred_colors) {
        this.sender = sender;
        this.interval = interval;
        this.preferred_colors = preferred_colors;
    }

	/* La fonction "fire" est appelŽe lorsque le timer expire
	 */

    public void fire() {
        Message msg;

        msg = new C2Message(sender.ID, sender.getDominator(), this.preferred_colors);

        // le noeud envoie le message ˆ tous ses voisins
        sender.broadcast(msg);
        // le noeud relance un nouveau timer
        this.startRelative(interval, node); // recursive restart of the timer
    }
}