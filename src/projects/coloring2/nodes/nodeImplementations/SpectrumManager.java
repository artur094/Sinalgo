package projects.coloring2.nodes.nodeImplementations;

import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by ivanmorandi on 06/11/2017.
 */
public class SpectrumManager {
    NodeInfo myself;

    HashSet<NodeInfo> my_spectrum;
    Hashtable<Integer, SpectrumManager> my_neighbours_spectrum;

    public SpectrumManager(NodeInfo myself, int number_neighbours){
        this.myself = myself;
        this.my_spectrum = new HashSet<>(number_neighbours);
        this.my_neighbours_spectrum = new Hashtable<>(number_neighbours);
    }

    public NodeInfo getMySelf() {
        return myself;
    }

    public void setMySelf(NodeInfo myself) {
        this.myself = myself;
    }

    public HashSet<NodeInfo> getMySpectrum() {
        return my_spectrum;
    }

    public void addMyNeighbours(NodeInfo neighbour){
        if(this.getMySpectrum().contains(neighbour))
            this.getMySpectrum().remove(neighbour);
        this.getMySpectrum().add(neighbour);
    }

    public NodeInfo getNeighbourFromMySpectrum(Integer id){
        for(NodeInfo nd : this.getMySpectrum())
            if(nd.getId() == id)
                return nd;
        return null;
    }

    public void setMySpectrum(HashSet<NodeInfo> my_spectrum) {
        this.my_spectrum = my_spectrum;
    }

    public Hashtable<Integer, SpectrumManager> getMyNeighboursSpectrum() {
        return my_neighbours_spectrum;
    }

    public SpectrumManager getMyNeighbourSpectrum(Integer neighbour_id){
        return my_neighbours_spectrum.get(neighbour_id);
    }

    public void addMyNeighbourSpectrum(NodeInfo neighbour, SpectrumManager neighbour_spectrum){
        if(this.my_neighbours_spectrum.keySet().contains(neighbour.getId()))
            this.my_neighbours_spectrum.remove(neighbour.getId());

        this.my_neighbours_spectrum.put(neighbour.getId(), neighbour_spectrum);
    }

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


    public void setMyNeighboursSpectrum(Hashtable<Integer, SpectrumManager> my_neighbours_spectrum) {
        this.my_neighbours_spectrum = my_neighbours_spectrum;
    }

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
