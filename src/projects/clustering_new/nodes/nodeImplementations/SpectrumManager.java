package projects.clustering_new.nodes.nodeImplementations;

import projects.coloring2.nodes.nodeImplementations.*;

import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by ivanmorandi on 06/11/2017.
 */
public class SpectrumManager {
    projects.coloring2.nodes.nodeImplementations.NodeInfo myself;

    HashSet<projects.coloring2.nodes.nodeImplementations.NodeInfo> my_spectrum;
    Hashtable<Integer, SpectrumManager> my_neighbours_spectrum;

    public SpectrumManager(projects.coloring2.nodes.nodeImplementations.NodeInfo myself, int number_neighbours){
        this.myself = myself;
        this.my_spectrum = new HashSet<>(number_neighbours);
        this.my_neighbours_spectrum = new Hashtable<>(number_neighbours);
    }

    public projects.coloring2.nodes.nodeImplementations.NodeInfo getMySelf() {
        return myself;
    }

    public void setMySelf(projects.coloring2.nodes.nodeImplementations.NodeInfo myself) {
        this.myself = myself;
    }

    public HashSet<projects.coloring2.nodes.nodeImplementations.NodeInfo> getMySpectrum() {
        return my_spectrum;
    }

    public void addMyNeighbours(projects.coloring2.nodes.nodeImplementations.NodeInfo neighbour){
        if(this.getMySpectrum().contains(neighbour))
            this.getMySpectrum().remove(neighbour);
        this.getMySpectrum().add(neighbour);
    }

    public projects.coloring2.nodes.nodeImplementations.NodeInfo getNeighbourFromMySpectrum(Integer id){
        for(projects.coloring2.nodes.nodeImplementations.NodeInfo nd : this.getMySpectrum())
            if(nd.getId() == id)
                return nd;
        return null;
    }

    public void setMySpectrum(HashSet<projects.coloring2.nodes.nodeImplementations.NodeInfo> my_spectrum) {
        this.my_spectrum = my_spectrum;
    }

    public Hashtable<Integer, SpectrumManager> getMyNeighboursSpectrum() {
        return my_neighbours_spectrum;
    }

    public SpectrumManager getMyNeighbourSpectrum(Integer neighbour_id){
        return my_neighbours_spectrum.get(neighbour_id);
    }

    public void addMyNeighbourSpectrum(projects.coloring2.nodes.nodeImplementations.NodeInfo neighbour, SpectrumManager neighbour_spectrum){
        if(this.my_neighbours_spectrum.keySet().contains(neighbour.getId()))
            this.my_neighbours_spectrum.remove(neighbour.getId());

        this.my_neighbours_spectrum.put(neighbour.getId(), neighbour_spectrum);
    }

    public HashSet<projects.coloring2.nodes.nodeImplementations.NodeInfo> getWholeSpectrum(){
        HashSet<projects.coloring2.nodes.nodeImplementations.NodeInfo> spectrum = new HashSet<>();
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
