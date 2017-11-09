package projects.coloring.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.coloring.nodes.timers.*;
import projects.coloring.nodes.messages.*;
import sinalgo.nodes.messages.Message;

/*
 * La classe "dato" ci-dessous est utilisŽe
 * pour stocker l'Žtat d'un voisin, ici sa color.
 * Les Žtats de tous les voisins
 * seront ensuite stockŽs dans une table de hachage
 */

class dato
{
    int color;

    dato(int color){
        this.color =color;
    }
}

/*
 * La classe "CNode" ci-dessous implŽmente le code de chaque noeud
 */

public class CNode extends Node {

    private int couleur; // la color du noeud

	/*
	 *  ci-dessous "nb" reprŽsente le nombre de couleurs total
	 *  le tableau "tab" stocke les codes color
	 */

    private final int nb = 10;
    private final Color tab[] = {Color.BLUE,Color.CYAN,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.WHITE,Color.YELLOW};

    /* La table de hachage "neighbours_state" stocke le dernier Žtat connu de chauqe voisin
     *
     */
    private Hashtable<Integer,dato> neighbours_state;

    public int getCouleur(){
        return couleur;
    }

    public Color RGBCouleur(){
        return tab[getCouleur()];
    }

    public void setCouleur(int c) {
        this.couleur=c;
    }


    /* La fonction ci-dessous
     * est utilisŽe pour tirer au hasard une color
     * parmi les nb disponibles
     */
    public void initCouleur(int range){
        setCouleur((int) (Math.random() * range) % range);
    }

    /*
     * La fonction "compute" est lancŽe ˆ chaque rŽception
     * de message. Elle permet de changer la color du noeud si nŽcessaire
     */
    public void compute(){
        boolean same=false;

        Iterator<Edge> it=this.outgoingConnections.iterator();
        boolean SC[]=new boolean[nb];

        for (int i=0;i<SC.length;i++)
            SC[i]=false;

        //For each edge
        while(it.hasNext()){
            Edge e=it.next();
            //Take the color of the neighbor
            dato tmp= neighbours_state.get(new Integer(e.endNode.ID));
            //If color has not been assigned
            if(tmp!=null){
                //If it has the same color --> same = TRUE
                if(tmp.color ==this.getCouleur()){
                    same=true;
                }
                //Say that the color is already used
                SC[tmp.color]=true;
            }
        }

        if (same){
            int dispo=0;
            //Search for available colors
            for (int i=0;i<SC.length;i++)
                if(SC[i]==false) dispo++;
            //If there aren't any colors, byebye
            if (dispo == 0) return;


            int choix= ((int) (Math.random() * 10000)) % dispo + 1;
            int i=0;

            //Get the not used color from choix
            //If the color is not used, decrease choix until 0 (and the one is the color that will be used)
            //If the color is use, just move to the next available color
            while(choix > 0){
                if(SC[i]==false)
                    choix--;
                if(choix>0) i++;
            }
            this.setCouleur(i);
        }
    }

    /* La fonction ci-dessous est appelŽe
     * ˆ chque rŽception de message.
     */
    public void handleMessages(Inbox inbox) {

        if(inbox.hasNext()==false) return;

        while(inbox.hasNext()){

            Message msg=inbox.next();

            if(msg instanceof CMessage){
			    // Received the color from a neighbour
                // I modifiy the neighbour state and then recompute the color
                dato tmp=new dato(((CMessage) msg).couleur);
                neighbours_state.put(new Integer(((CMessage) msg).id),tmp);
                compute();
            }
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
        initCouleur(nb);
        (new CTimer(this,50)).startRelative(50,this);
        this.neighbours_state =new Hashtable<Integer, dato>(this.outgoingConnections.size());

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
        this.setColor(this.RGBCouleur());

        String text = ""+this.ID;
        c=Color.BLACK;

        super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, c);
    }


}