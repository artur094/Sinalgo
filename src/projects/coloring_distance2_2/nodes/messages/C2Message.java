package projects.coloring_distance2_2.nodes.messages;

import sinalgo.nodes.messages.Message;

import java.util.Hashtable;

/* description de l'unique type de message utilisŽ
 * dans l'application
 */

public class C2Message extends Message {

    public int id;
    public int dominator;
    public Hashtable<Integer, Integer> preferred_colors;

    public C2Message(int id, int dominator, Hashtable<Integer, Integer> preferred_colors) {
        this.id=id;
        this.dominator = dominator;
        this.preferred_colors = preferred_colors;
    }

    public Message clone() {
        return new C2Message(id,dominator, preferred_colors);
    }

}