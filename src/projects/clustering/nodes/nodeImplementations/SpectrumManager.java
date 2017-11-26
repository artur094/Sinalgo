package projects.clustering.nodes.nodeImplementations;

import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by ivanmorandi on 06/11/2017.
 * SpectrumManager contains all info known by a node: the node itself, the neighbours and the neighbours of each neighbour
 * Therefore, each node has an instance of SpectrumManager with all known information gathered by neighbours
 * SpectrumManager is focused only on storing data
 */
public class SpectrumManager {
    //Information about myself
    NodeInfo myself;

    //Spectrum containing the node and neighbours
    HashSet<NodeInfo> my_spectrum;
    //Table with all spectrums of all neighbours of the node
    Hashtable<Integer, SpectrumManager> my_neighbours_spectrum;

    /**
     *
     * @param myself NodeInfo with essential information of the node
     * @param number_neighbours Number of neighbours of the node
     */
    public SpectrumManager(NodeInfo myself, int number_neighbours){
        this.myself = myself;
        this.my_spectrum = new HashSet<>(number_neighbours);
        this.my_neighbours_spectrum = new Hashtable<>(number_neighbours);
    }

    /**
     * Return the NodeInfo of the node. NodeInfo contains the information of the node such as color, dominator (MIS), etc.
     * @return
     */
    public NodeInfo getMySelf() {
        return myself;
    }

    /**
     * Set NodeInfo of the node
     * @param myself
     */
    public void setMySelf(NodeInfo myself) {
        this.myself = myself;
    }

    /**
     * Return the spectrum of the node. The spectrum is an hashset of NodeInfo of each neighbour and the node itself.
     * @return
     */
    public HashSet<NodeInfo> getMySpectrum() {
        return my_spectrum;
    }

    /**
     * Add or Update the information (NodeInfo) of a node in the spectrum of the node
     * @param neighbour
     */
    public void addMyNeighbours(NodeInfo neighbour){
        if(this.getMySpectrum().contains(neighbour))
            this.getMySpectrum().remove(neighbour);
        this.getMySpectrum().add(neighbour);
    }

    /**
     * Return the NodeInfo of a specific neighbour
     * @param id ID of the neighbour
     * @return
     */
    public NodeInfo getNeighbourFromMySpectrum(Integer id){
        for(NodeInfo nd : this.getMySpectrum())
            if(nd.getId() == id)
                return nd;
        return null;
    }

    /**
     * Set the spectrum of the node to the given one
     * @param my_spectrum
     */
    public void setMySpectrum(HashSet<NodeInfo> my_spectrum) {
        this.my_spectrum = my_spectrum;
    }

    /**
     * Return the complete table containing the spectrum of each neighbour
     * @return
     */
    public Hashtable<Integer, SpectrumManager> getMyNeighboursSpectrum() {
        return my_neighbours_spectrum;
    }

    /**
     * Return the spectrum of a neighbour
     * @param neighbour_id ID of the neighbour
     * @return
     */
    public SpectrumManager getMyNeighbourSpectrum(Integer neighbour_id){
        return my_neighbours_spectrum.get(neighbour_id);
    }

    /**
     * Add or Update the spectrum of the neighbour in the table of the node
     * @param neighbour
     * @param neighbour_spectrum
     */
    public void addMyNeighbourSpectrum(NodeInfo neighbour, SpectrumManager neighbour_spectrum){
        if(this.my_neighbours_spectrum.keySet().contains(neighbour.getId()))
            this.my_neighbours_spectrum.remove(neighbour.getId());

        this.my_neighbours_spectrum.put(neighbour.getId(), neighbour_spectrum);
    }

    /**
     * Group all NodeInfo nodes contained in this class into one hashset
     * Used when there is the need of cycling for all nodes known by the node
     * @return
     */
    public HashSet<NodeInfo> getWholeSpectrum(){
        HashSet<NodeInfo> spectrum = new HashSet<>();
        spectrum.addAll(my_spectrum);

        for (Integer id : my_neighbours_spectrum.keySet()) {
            for (Integer id_neighbour : getMyNeighboursSpectrum().get(id).getMyNeighboursSpectrum().keySet()){
                spectrum.addAll(getMyNeighbourSpectrum(id).getMyNeighbourSpectrum(id_neighbour).getMySpectrum());
            }
        }
        return spectrum;
    }

    /**
     * Set the table of neighbours with the given one
     * @param my_neighbours_spectrum
     */
    public void setMyNeighboursSpectrum(Hashtable<Integer, SpectrumManager> my_neighbours_spectrum) {
        this.my_neighbours_spectrum = my_neighbours_spectrum;
    }

    /**
     * Override of equals, where a SpectrumManager is the same if the node Myself is the same of another SpectrumManager instance
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof SpectrumManager))
            return false;

        SpectrumManager sm = (SpectrumManager) obj;
        return this.getMySelf().equals(sm.getMySelf());
    }

    @Override
    public int hashCode() {
        return this.getMySelf().getId();
    }
}
