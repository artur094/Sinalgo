package projects.clustering.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import projects.clustering.nodes.nodeImplementations.NodeInfo;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.Node;
import sinalgo.nodes.edges.Edge;
import sinalgo.nodes.messages.Inbox;
import projects.clustering.nodes.timers.*;
import projects.clustering.nodes.messages.*;
import sinalgo.nodes.messages.Message;



public class CNode extends Node {

    private NodeInfo me;

    private final int nb = 11;
    private final Color tab[] = {Color.BLUE,Color.CYAN,Color.GREEN,Color.LIGHT_GRAY,Color.MAGENTA,Color.ORANGE,Color.PINK,Color.MAGENTA,Color.WHITE,Color.YELLOW, Color.RED};

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
        //this.me.setColor(0);
        this.me.setColor((int) (Math.random() * range) % range);
    }

    public void updateSpectrum(int id, int dominator, int color, int dominator_color, int number_neighbours){
        this.me.updateSpectrum(id, dominator, color, dominator_color, number_neighbours);
    }


    /* ******************* MIS (2 COLORING DISTANCE) ***************************** */

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

    /* ******************* 2 COLORING DISTANCE ***************************** */

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

    /* ******************* CLUSTERING ***************************** */

    public void computeClustering(){
        int edges = 0;

        //count edges in common between neighbours
        for (Edge e : this.outgoingConnections){
            //Check neighbours of my neighbour with mines
            HashSet<Integer> neighbours = this.getMe().getNeighbours_of_neighbour(e.endNode.ID);
            edges += intersection(neighbours);
            //System.out.println("["+this.getMe().getId()+"] intersections = "+intersections + " from " + e.endNode.ID);
        }

        edges /= 2;

        edges += outgoingConnections.size();
        //System.out.println("Node " + this.getMe().getId() + " with #edges = " + edges + " and #neighbours = "+this.outgoingConnections.size() + " with #intersections = "+intersections);
        this.getMe().setClustering((double)(edges) / (double) this.outgoingConnections.size());
    }

    public int intersection(HashSet<Integer> neighbours_of_neighbour){
        if(neighbours_of_neighbour == null)
            return 0;

        ArrayList<Integer> my_neigh = new ArrayList<>();
        ArrayList<Integer> neigh_neigh = new ArrayList<>();

        int count = 0;

        for (Edge e : outgoingConnections){
            my_neigh.add(e.endNode.ID);
        }

        for (Integer neigh : neighbours_of_neighbour)
            neigh_neigh.add(neigh);

        my_neigh.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        neigh_neigh.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        int i=0;
        int j=0;

        for (; i<my_neigh.size() && j<neigh_neigh.size();){
            if(my_neigh.get(i).equals(neigh_neigh.get(j)) && ! my_neigh.get(i).equals(this.getMe().getId()))
            {
                count++;
                i++;
                j++;
            }else if(my_neigh.get(i).compareTo(neigh_neigh.get(j)) < 0)
                i++;
            else
                j++;
        }
        return count;
    }

    /* La fonction ci-dessous est appelŽe
     * ˆ chque rŽception de message.
     */
    public void handleMessages(Inbox inbox) {

        if(inbox.hasNext()==false) return;

        while(inbox.hasNext()){

            this.getMe().setNeighbours(outgoingConnections);

            Message msg=inbox.next();

            if(msg instanceof CMessage){
                //Update the spectrum
                updateSpectrum(((CMessage) msg).id, ((CMessage) msg).dominator, ((CMessage) msg).color, ((CMessage) msg).dominator_color, ((CMessage) msg).number_neighbours);

                //Update neighbor spectrum
                this.me.setNeighbourSpectrum(((CMessage) msg).id, ((CMessage) msg).myself.getSpectrum());

                //Update neighbours of my neighbour
                this.me.setNeighbours_of_neighbour(((CMessage) msg).id, ((CMessage) msg).myself.getNeighbours());

                /*HashSet<NodeInfo> whole_spectrum = new HashSet<>();

                for (Integer key : ((CMessage) msg).myself.getNeighbour_spectrum().keySet()){
                    whole_spectrum.addAll(((CMessage) msg).myself.getNeighbourSpectrum(key));
                }
                whole_spectrum.addAll(((CMessage) msg).spectrum);
                whole_spectrum.addAll(getSpectrum());

                computeMIS(whole_spectrum);
                */

                //Update my color if the msg comes from my leader

                /*
                if(((CMessage) msg).id == this.getDominator()){
                    for(NodeInfo nd : ((CMessage) msg).spectrum){
                        if(nd.getId() == this.ID){
                            if(nd.getPreferredColor() != null)
                            {
                                this.me.setColor(nd.getPreferredColor());
                            }
                        }
                    }
                }
                */

                //Compute Clustering
                computeClustering();

                if(this.getMe().getCluster_head_clustering() < this.getMe().getClustering()){
                    this.getMe().setCluster_head(this.ID);
                    this.getMe().setCluster_head_clustering(this.getMe().getClustering());
                }

                //Search maximum clustering
                if(((CMessage) msg).myself.getClustering() > this.getMe().getCluster_head_clustering()){
                    this.getMe().setCluster_head(((CMessage) msg).id);
                    this.getMe().setCluster_head_clustering(((CMessage) msg).myself.getClustering());
                    this.me.setColor(((CMessage) msg).color);
                }
                if(((CMessage) msg).id == this.getMe().getCluster_head()){
                    this.me.setColor(((CMessage) msg).color);
                }

                //Update my info in the spectrum
                updateSpectrum(this.ID, this.me.getDominator(), this.me.getColor(), this.getMe().getColor(), 0);
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

        //Double d = Math.random()%10;
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
        /*if(this.me.isDominant()) {
            text += "D";
            text += this.me.getDominator();
        }
        else{
            text+="-";
            text+=this.me.getDominator();
        }*/
        //text += " C"+ String.format("%.2f", me.getClustering()) + " P"+this.getMe().getCluster_head();
        if(this.getMe().getCluster_head().equals(this.getMe().getId())){
            text += " H";
        }
        else
            text += " C"+this.getMe().getCluster_head();
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