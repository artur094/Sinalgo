package projects.coloring_distance2.nodes.messages;

import projects.coloring_distance2.nodes.nodeImplementations.NodeInfo;
import sinalgo.nodes.messages.Message;

import java.util.HashSet;

/* description de l'unique type de message utilisŽ
 * dans l'application
 */

public class CMessage extends Message {

    public int id;
    public int color;
    public int dominator;
    public int dominator_color;
    public int number_neighbours;
    public HashSet<NodeInfo> spectrum;

    public CMessage(int id, int color, int dominator, int dominator_color, int number_neighbours, HashSet<NodeInfo> spectrum) {
        this.id=id;
        this.color = color;
        this.dominator = dominator;
        this.spectrum = spectrum;
        this.number_neighbours = number_neighbours;
        this.dominator_color = dominator_color;
    }

    public Message clone() {
        return new CMessage(id,color, dominator,dominator_color, number_neighbours, spectrum);
    }

}