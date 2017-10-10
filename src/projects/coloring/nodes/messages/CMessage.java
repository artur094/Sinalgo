package projects.coloring.nodes.messages;

import sinalgo.nodes.messages.Message;

/* description de l'unique type de message utilisŽ
 * dans l'application
 */

public class CMessage extends Message {

    public int id;
    public int couleur;

    public CMessage(int id, int couleur) {
        this.id=id;
        this.couleur = couleur;
    }

    public Message clone() {
        return new CMessage(id,couleur);
    }

}