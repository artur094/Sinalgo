package projects.coloring_distance2_2.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.coloring_distance2_2.nodes.timers.*;
import projects.coloring_distance2_2.nodes.messages.*;
import sinalgo.nodes.messages.Message;



public class CNode extends Node {

    private NodeInfo me;
    Random rnd;

    private final int nb = 10;
    private final Color tab[] = {Color.BLUE,Color.CYAN,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.RED,Color.WHITE,Color.YELLOW};


    //--------------------------------

    public int getNumberNeighbours(){ return this.outgoingConnections.size(); }

    public NodeInfo getMe() {
        return me;
    }

    public HashSet<NodeInfo> getSpectrum() {
        return me.getSpectrum();
    }

    public boolean isDominant() {
        return (me.getDominator() == this.ID);
    }

    public Integer getDominator(){
        return this.me.getDominator();
    }

    public void setDominant(int dominant_node) {
        this.me.setDominator(dominant_node);
    }

    public int getNodeColor(){
        return me.getColor();
    }

    public Color RGBColor(){
        return tab[me.getColor()];
    }

    public void setNodeColor(int c) {
        this.me.setColor(c);
    }

    /* La fonction ci-dessous
     * est utilisŽe pour tirer au hasard une color
     * parmi les nb disponibles
     */
    public void initNodeColor(int range){
        this.me.setColor(0);
        //setNode_color((int) (Math.random() * range) % range);
    }

    public void updateSpectrum(int id, int dominator, int color, int number_neighbours){
        this.me.updateSpectrum(id, dominator, color, number_neighbours);
    }

    public ArrayList<Integer> getDominatedNodes(){

        ArrayList<Integer> dominated = new ArrayList<>();

        for (NodeInfo nodeInfo : this.me.getSpectrum()) {
            Integer dominant = nodeInfo.getDominator();

            if(dominant != null && dominant == this.ID)
                dominated.add(nodeInfo.getId());
        }
        if(this.me.isDominant())
            dominated.add(this.ID);

        return dominated;
    }

    public Hashtable<Integer, Integer> getPreferredColors(ArrayList<Integer> dominated_nodes){
        return null;
    }

    public void computeMIS(){
        //COMPUTE MIS
        //I say that I am the dominant
        Integer my_dominant = this.ID;

        for(NodeInfo nd : this.me.getSpectrum()){
            System.out.println("Checking: "+nd.getId()+nd.getDominator());
            if(nd.getId() > this.ID){
                if(nd.isDominant()){
                    if(my_dominant < nd.getId())
                        my_dominant = nd.getId();
                }
            }
        }

        this.me.setDominator(my_dominant);

        System.out.println(this.me.getId() + " " + this.me.getDominator());

        //If I am not a dominator, I have to wait for my color :((
        if(! this.isDominant())
            return;

        //TODO: compute color
        int mycolor = 0;

        computeColor(this.getDominator(), mycolor);
    }

    /*
     * La fonction "compute" est lancŽe ˆ chaque rŽception
     * de message. Elle permet de changer la color du noeud si nŽcessaire
     */
    public void computeColor(int dominator, int color){
        if(dominator == this.me.getDominator()){

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
                //Update the spectrum
                updateSpectrum(((CMessage) msg).id, ((CMessage) msg).dominator, ((CMessage) msg).color, ((CMessage) msg).number_neighbours);

                //neighbours_dominant.put(((CMessage) msg).id, ((CMessage) msg).dominator);

                //neighbours_spectrum.put(((CMessage) msg).id, ((CMessage) msg).spectrum);

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
            updateSpectrum(this.ID, this.me.getDominator(), this.me.getColor(), 0);
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
        me = new NodeInfo(this.ID, this.ID, 0, this.outgoingConnections.size());

        this.initNodeColor(nb);
        rnd= new Random();

        (new CTimer(this, 50)).startRelative(50, this);
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
        this.setColor(this.RGBColor());

        String text = ""+this.ID;
        if(this.me.isDominant()) {
            text += "D";
            text += this.me.getDominator();
        }
        else{
            text+="-";
            text+=this.me.getDominator();
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