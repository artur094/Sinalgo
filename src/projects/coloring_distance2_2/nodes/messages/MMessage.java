package projects.coloring_distance2_2.nodes.messages;

import projects.coloring_distance2.nodes.nodeImplementations.CNode;
import sinalgo.nodes.messages.Message;

/* description de l'unique type de message utilisŽ
 * dans l'application
 */

public class MMessage extends Message {

    public int id;
    public int dominant;

    public MMessage(int id, int dominant) {
        this.id=id;
        this.dominant = dominant;
    }

    public Message clone() {
        return new MMessage(id,dominant);
    }

}