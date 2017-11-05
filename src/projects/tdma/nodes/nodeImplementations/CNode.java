package projects.tdma.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import projects.tdma.nodes.nodeImplementations.NodeInfo;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.tdma.nodes.timers.*;
import projects.tdma.nodes.messages.*;
import sinalgo.nodes.messages.Message;



public class CNode extends Node {

    private NodeInfo me;

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

    public void updateSpectrum(int id, int dominator, int color, int dominator_color, int number_neighbours){
        this.me.updateSpectrum(id, dominator, color, dominator_color, number_neighbours);
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

    public void computeMIS(HashSet<NodeInfo> spectrum){
        //COMPUTE MIS
        //I say that I am the dominant
        Integer my_dominant = this.ID;

        for(NodeInfo nd : this.me.getSpectrum()){
            //System.out.println("Checking: "+nd.getId()+nd.getDominator());
            if(nd.getId() > this.ID){
                if(nd.isDominant()){
                    if(my_dominant < nd.getId())
                        my_dominant = nd.getId();
                }
            }
        }

        this.me.setDominator(my_dominant);

        //System.out.println(this.me.getId() + " " + this.me.getDominator());

        //If I am not a dominator, I have to wait for my color :((
        if(! this.isDominant())
            return;

        //TODO: compute color
        computePreferredColor(spectrum);

        //computeColor(this.getDominator(), mycolor);
    }

    public void computePreferredColor(HashSet<NodeInfo> spectrum){
        if(! this.isDominant()){
            return;
        }

        boolean[] used_colors = new boolean[nb];

        /*for (NodeInfo neighbour : this.getSpectrum()){
            for (NodeInfo neighbour_neighbour : neighbour.getSpectrum()){

            }
        }*/

        for (NodeInfo nd : spectrum){
            if(nd.getDominator() > this.getDominator()){
                used_colors[nd.color] = true;
                used_colors[nd.getDominatorColor()] = true;
            }
        }

        /*for (Edge edge : this.outgoingConnections){
            if(this.getMe().getNeighbourSpectrum(edge.endNode.ID) != null) {
                for (NodeInfo neighbour_neighbour : this.getMe().getNeighbourSpectrum(edge.endNode.ID)) {
                    if (neighbour_neighbour.getDominator() > this.getMe().getDominator()) {
                        used_colors[neighbour_neighbour.color] = true;
                        used_colors[neighbour_neighbour.getDominatorColor()] = true;
                    }
                }
            }
        }*/

        //if my color is already used, then change it
        if(used_colors[me.getColor()]){
            int free_color = freeColors(used_colors);

            if(free_color < 0){
                System.out.println("No free colors");
                return;
            }

            this.me.setColor(free_color);
            used_colors[free_color] = true;
        }else{
            used_colors[me.getColor()] = true;
        }

        for(NodeInfo neighbour : this.getSpectrum()){
            if(neighbour.getDominator() == this.getDominator() && neighbour.getId() != this.me.getId()){
                if(used_colors[neighbour.getColor()]){
                    int free_color = freeColors(used_colors);

                    if(free_color < 0){
                        System.out.println("No free colors");
                        return;
                    }

                    neighbour.setPreferredColor(free_color);
                    used_colors[free_color] = true;
                }else{
                    used_colors[neighbour.getColor()] = true;
                }
            }
        }
    }

    /*
     * La fonction "compute" est lancŽe ˆ chaque rŽception
     * de message. Elle permet de changer la color du noeud si nŽcessaire
     */
    public void computeColor(int dominator, int color){
        if(dominator == this.me.getDominator()){

        }
    }


    public void computeTDMA(){
        HashSet<Interval> intervals = new HashSet<>(); //Sp
        HashSet<NodeInfo> tmp = new HashSet<>();
        tmp.addAll(this.getMe().getSpectrum());
        for(Integer key : this.getMe().getNeighbour_spectrum().keySet())
            tmp.addAll(this.getMe().getNeighbourSpectrum(key));

        for (NodeInfo nd : tmp){
            if(nd.base > this.getMe().base || ((nd.base == this.getMe().base) && nd.getColor() < this.getMe().getColor())){
                intervals.addAll(nd.getIntervals());
            }
        }
        this.getMe().clearIntervals();
        this.getMe().addIntervals(g(this.getMe().getBase(), intervals));
    }

    private HashSet<Interval> g(Integer base, HashSet<Interval> S){
        //Return all free interval which are not used at distance N2
        //Maybe I can just return 1 interval (?), the biggest with the constraint that interval is less than 1/base

        //Let's try:
        //Return the biggest interval great up to 1/base
        //How:
        //1 - Convert the hashset into an arraylist
        //2 - Sort the arraylist
        //3 - Get free intervals from the arraylist
        //4 - Store the number of intervals such that I will get the maximum time possible (constrained by 1/base)

        Double max_interval = 1.0/(double)base;
        HashSet<Interval> final_intervals = new HashSet<>();

        //1 - Convert the hashset into an arraylist
        ArrayList<Interval> intervals = new ArrayList<>();
        intervals.addAll(S);

        //2 - Sort the arraylist
        intervals.sort(new Comparator<Interval>() {
            @Override
            public int compare(Interval o1, Interval o2) {
                return o1.getLeft().compareTo(o2.getLeft());
            }
        });

        //3 - Get free intervals from the arraylist
        ArrayList<Interval> free_intervals = new ArrayList<>();

        Double free_interval_left = 0.0;
        for(Interval i : intervals){
            if(i.getLeft() > free_interval_left){
                free_intervals.add(new Interval(free_interval_left, i.getLeft()));
            }
            free_interval_left = i.getRight();
        }
        if(free_interval_left < 1.0)
            free_intervals.add(new Interval(free_interval_left, 1.0));

        //4 - too long to rewrite

        //search for an interval > than 1/base (so it will be contiguous
        for (Interval i : free_intervals){
            System.out.println("Node " + getMe().getId() + " interval " + i.toString());
            if(i.getLength() >= max_interval){
                //Reached maximum size of time
                final_intervals.add(new Interval(i.getLeft(), i.getLeft()+max_interval));
                return final_intervals;
            }
        }

        //otherwise, search for non contiguous intervals
        for (Interval i : free_intervals){
            if(i.getLength() >= max_interval){
                final_intervals.add(new Interval(i.getLeft(), max_interval));
                break;
            }else{
                final_intervals.add(i);
                max_interval -= i.getLength();
            }
        }

        return final_intervals;
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
                this.getMe().updateSpectrum(((CMessage) msg).myself);
                //updateSpectrum(((CMessage) msg).id, ((CMessage) msg).dominator, ((CMessage) msg).color, ((CMessage) msg).dominator_color, ((CMessage) msg).number_neighbours);

                //Update neighbor spectrum
                this.me.setNeighbourSpectrum(((CMessage) msg).id, ((CMessage) msg).spectrum);

                HashSet<NodeInfo> whole_spectrum = new HashSet<>();

                for (Integer key : ((CMessage) msg).myself.getNeighbour_spectrum().keySet()){
                    whole_spectrum.addAll(((CMessage) msg).myself.getNeighbourSpectrum(key));
                }
                whole_spectrum.addAll(((CMessage) msg).spectrum);
                whole_spectrum.addAll(getSpectrum());

                computeMIS(whole_spectrum);

                //Update my color if the msg comes from my leader

                if(((CMessage) msg).id == this.getDominator()){
                    for(NodeInfo nd : ((CMessage) msg).spectrum){
                        if(nd.getId() == this.ID){
                            if(nd.getPreferredColor() != null)
                            {
                                this.me.setColor(nd.getPreferredColor());

                                //TODO: remove useless int c
                                int c;
                            }
                        }
                    }
                }

                //Update my info in the spectrum

                Integer color = this.getMe().getDominatorColor();
                if(((CMessage) msg).id == this.getDominator())
                    color = ((CMessage) msg).color;

                updateSpectrum(this.ID, this.me.getDominator(), this.me.getColor(), color, 0);

                //compute TDMA
                Boolean[] used_colors = new Boolean[nb];
                for (int i=0; i<nb; i++)
                    used_colors[i] = false;
                used_colors[this.getMe().getColor()] = true;

                for(NodeInfo nd : this.getMe().getSpectrum()){
                    used_colors[nd.getColor()] = true;
                }

                for (Integer key : this.getMe().getNeighbour_spectrum().keySet()){
                    for(NodeInfo nd : this.getMe().getNeighbour_spectrum().get(key)){
                        used_colors[nd.getColor()] = true;
                    }
                }

                int count = 0;
                for (int i=0; i<nb; i++)
                    if(used_colors[i])
                        count++;

                this.getMe().setBase(count);

                computeTDMA();

                System.out.println("Node " + getMe().getId() + " with base " + getMe().getBase());
                for(Interval i : this.getMe().getIntervals()){
                    System.out.print(i.toString() + " ");
                }
                System.out.println();
            }
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

        (new CTimer(this, 50)).startRelative(50, this);
        //(new CTimer(this, 50)).startRelative(50, this);
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

    public int freeColors(boolean[] used_colors){
        for(int i=0;i<used_colors.length;i++){
            if(!used_colors[i])
                return i;
        }
        return -1;
    }


}