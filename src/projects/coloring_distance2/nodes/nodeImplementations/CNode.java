package projects.coloring_distance2.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import javafx.util.Pair;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.coloring_distance2.nodes.timers.*;
import projects.coloring_distance2.nodes.messages.*;
import sinalgo.nodes.messages.Message;

/*
 * La classe "dato" ci-dessous est utilisŽe
 * pour stocker l'Žtat d'un voisin, ici sa color.
 * Les Žtats de tous les voisins
 * seront ensuite stockŽs dans une table de hachage
 */

/*
 * La classe "CNode" ci-dessous implŽmente le code de chaque noeud
 */

public class CNode extends Node {

    private int couleur; // the color of the node
    private int dominant; // who is the dominant node for this one (minLp on TDMA)

    private boolean[] available_colors;

	/*
	 *  ci-dessous "nb" reprŽsente le nombre de couleurs total
	 *  le tableau "tab" stocke les codes color
	 */

    private final int nb = 10;
    private final Color tab[] = {Color.BLUE,Color.CYAN,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.WHITE,Color.YELLOW};

    /* La table de hachage "neighbours_state" stocke le dernier Žtat connu de chauqe voisin
     *
     */
    //TODO: fix spectrum for non dominant nodes
    private HashSet<NodeInfo> spectrum; // (dominator, color of neighbor)
    private Hashtable<Integer, Integer> neighbours_dominant; //dominants of my neihgbours
    private Hashtable<Integer, HashSet<NodeInfo>> neighbours_spectrum;



    //--------------------------------


    public HashSet<NodeInfo> getSpectrum() {
        return spectrum;
    }

    public boolean isDominant() {
        return (dominant == this.ID);
    }

    public Integer getDominant(){
        return this.dominant;
    }

    public void setDominant(int dominant_node) {
        this.dominant = dominant_node;
    }

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
        setCouleur(0);
        //setNode_color((int) (Math.random() * range) % range);
    }

    public void updateSpectrum(int id, int dominator, int color){
        NodeInfo tmp = new NodeInfo(id, dominator, color);
        if(spectrum.contains(tmp)){
            spectrum.remove(tmp);
        }
        spectrum.add(tmp);
    }

    public ArrayList<Integer> getDominatedNodes(){
        ArrayList<Integer> dominated = new ArrayList<>();
        for (Edge e: this.outgoingConnections) {
            Integer dominant = this.neighbours_dominant.get(e.endNode.ID);

            if(dominant != null && dominant == this.ID)
                dominated.add(e.endNode.ID);
        }
        if(this.isDominant())
            dominated.add(this.ID);
        return dominated;
    }

    public Hashtable<Integer, Integer> getPreferredColors(ArrayList<Integer> dominated_nodes){
        boolean[] used_colors = new boolean[nb];

        for(int i=0;i<nb;i++)
            used_colors[i] = false;

        //Set used colors
        for (Edge e: this.outgoingConnections) {
            //Take the spectrum of the neighbor
            if(this.neighbours_spectrum.get(e.endNode.ID) == null)
                continue;

            HashSet<NodeInfo> q_spectrum = this.neighbours_spectrum.get(e.endNode.ID);

            //Get list of
            for(NodeInfo node : q_spectrum){
                if(node.getDominator() > this.ID || true){
                    used_colors[node.getColor()] = true;
                }
            }
         }
        boolean[] neighbours_colors = used_colors.clone();
        boolean change_colors = false;
        //Check if I have problem with my nodes
        for(NodeInfo nd : this.spectrum){
            if(nd.getDominator() == this.ID){
                if(neighbours_colors[nd.getColor()])
                    change_colors = true;
                else
                    neighbours_colors[nd.getColor()] = true;
            }
        }

        if(! change_colors)
            return null;

        Hashtable<Integer, Integer> dominated_colors = new Hashtable<>(dominated_nodes.size());

        //for each node, assign a color
        for(int i=0;i<dominated_nodes.size();i++){
            //search for a not used color
            for (int j=0; j<used_colors.length;j++){
                if(! used_colors[j]){
                    used_colors[j] = true;
                    dominated_colors.put(dominated_nodes.get(i), j);
                    //System.out.println("Node "+dominated_nodes.get(i)+" will have color Nr "+j);
                    break;
                }
            }
        }

        return dominated_colors;
    }

    public void computeMIS(){
        //COMPUTE MIS
        Integer my_dominant = this.ID;

        for(Edge e : this.outgoingConnections){
            //R1 - R2
            if(e.endNode.ID > this.ID){
                Integer neighbor_mis = this.neighbours_dominant.get(e.endNode.ID);

                if(neighbor_mis == null)
                    continue;

                //If the dominant node is the same node, then my neighbor is a dominant node
                if(neighbor_mis == e.endNode.ID) {
                    if(my_dominant < neighbor_mis)
                        my_dominant = neighbor_mis;
                }
            }
        }
        this.setDominant(my_dominant);

        if(this.isDominant())
            computeColor(this.getDominant(), 0);

        (new CTimer(this, 50)).startRelative(50, this);

        //if(this.getDominator())
        //    this.setNode_color(0);
        //else
        //    this.setNode_color(8);
    }

    /*
     * La fonction "compute" est lancŽe ˆ chaque rŽception
     * de message. Elle permet de changer la color du noeud si nŽcessaire
     */
    public void computeColor(int dominator, int color){

        if(this.isDominant()){
            //Compute who are dominated by this node
            ArrayList<Integer> dominated = getDominatedNodes();

            //Compute colors to give to dominated nodes
            Hashtable<Integer, Integer> dominated_colors = getPreferredColors(dominated);

            if(dominated_colors != null) {

                this.setCouleur(dominated_colors.get(this.ID));

                //send preferred colors
                (new C2Timer(this, 50, dominated_colors)).startRelative(50, this);
            }
        }
        //Compute dominant node
        if(! this.isDominant()){
            //Set color sent by the dominant node
            if(dominator == this.getDominant())
                this.setCouleur(color);
        }

        //Update spectrum
        this.updateSpectrum(this.ID, this.dominant, this.getCouleur());

/*
        int number_used_colors = 0;
        boolean choose_color =  true;

        //boolean[] used_colors = new boolean[nb];

        //Set used colors to false (no colors used till now)

        if(this.getDominator())

            for (int i=0;i<available_colors.length;i++)
                available_colors[i]=true;

        Iterator<Edge> it = this.outgoingConnections.iterator();

        while(it.hasNext()){
            Edge e = it.next();

            dato tmp = neighbours_state.get(e.endNode.ID);

            if(tmp != null){
                available_colors[tmp.color] = false;
            }
        }

        if(!available_colors[this.getNode_color()])
            choose_color = true;

        if(choose_color){
            for(int i=0;i<available_colors.length;i++)
                if(! available_colors[i])
                    number_used_colors++;

            int availability = nb - number_used_colors;

            if(availability <= 0)
                return;

            int random= ((int) (Math.random() * 10000)) % availability + 1;
            int i=0;

            //Get the not used color from choix
            //If the color is not used, decrease choix until 0 (and the one is the color that will be used)
            //If the color is use, just move to the next available color
            while(random > 0){
                if(available_colors[i])
                    random--;
                if(random>0) i++;
            }
            this.setNode_color(i);

            available_colors[i] = false;

            (new C2Timer(this, 50)).startRelative(50, this);
        }



*/


        /*
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
                if(tmp.color ==this.getNode_color()){
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
            this.setNode_color(i);
        }

        */
    }

    /* La fonction ci-dessous est appelŽe
     * ˆ chque rŽception de message.
     */
    public void handleMessages(Inbox inbox) {

        if(inbox.hasNext()==false) return;

        while(inbox.hasNext()){

            Message msg=inbox.next();

            if(msg instanceof CMessage){
                //Update the spectrum
                updateSpectrum(((CMessage) msg).id, ((CMessage) msg).dominator, ((CMessage) msg).color);

                neighbours_dominant.put(((CMessage) msg).id, ((CMessage) msg).dominator);

                neighbours_spectrum.put(((CMessage) msg).id, ((CMessage) msg).spectrum);

                /*System.out.println(this.ID + " size of Spectrum["+((CMessage) msg).id + "] = "+this.getSpectrum().size());

                for(NodeInfo ni : neighbours_spectrum.get(((CMessage) msg).id)){
                    System.out.println(ni.toString());
                }*/

                computeMIS();
            }
            if(msg instanceof C2Message){
                //If I receive set of colors from my neighbor
                Integer color = ((C2Message) msg).preferred_colors.get(this.ID);

                if(color != null)
                    computeColor(((C2Message) msg).dominator, color);

                /*
                dato tmp = new dato(((C2Message) msg).color);
                spectrum.put(((C2Message) msg).id,tmp);

                for (int i=0;i<this.available_colors.length;i++)
                    this.available_colors[i] = true;

                if(((C2Message) msg).id > this.ID){
                    this.available_colors = ((C2Message) msg).available_color;
                }

                computeColor();
                */
            }
            updateSpectrum(this.ID, this.dominant, this.couleur);
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
        setDominant(this.ID);
        initCouleur(nb);

        this.spectrum =new HashSet<NodeInfo>(this.outgoingConnections.size());
        this.neighbours_dominant = new Hashtable<Integer, Integer>(this.outgoingConnections.size());
        this.neighbours_spectrum = new Hashtable<>();

        available_colors = new boolean[nb];
        for (int i=0;i<available_colors.length;i++)
            available_colors[i]=true;

        (new CTimer(this,50)).startRelative(50,this);
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
        if(this.isDominant()) {
            text += "D";
            text += this.getDominant();
        }
        else{
            text+="-";
            text+=this.getDominant().toString();
        }
        c=Color.BLACK;

        super.drawNodeAsDiskWithText(g, pt, highlight, text, 20, c);
    }

    /*public boolean[] freeColors(){
        boolean[] free_colors = new boolean[nb];
        for (int i=0;i<free_colors.length;i++)
            free_colors[i]=true;

        Iterator<Edge> it = this.outgoingConnections.iterator();

        while(it.hasNext()){
            Edge e = it.next();

            Integer tmp = spectrum.get(e.endNode.ID);

            if(tmp != null && e.endNode.ID > this.ID){
                free_colors[tmp] = false;
            }
        }

        free_colors[this.getNode_color()] = false;

        return free_colors;
    }*/


}