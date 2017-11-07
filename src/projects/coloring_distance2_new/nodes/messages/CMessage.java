package projects.coloring_distance2_new.nodes.messages;

import projects.coloring_distance2_new.nodes.nodeImplementations.NodeInfo;
import projects.coloring_distance2_new.nodes.nodeImplementations.SpectrumManager;
import sinalgo.nodes.messages.Message;

import java.util.HashSet;

/* description de l'unique type de message utilisŽ
 * dans l'application
 */

public class CMessage extends Message {

    private SpectrumManager mySpectrumManager;

    public CMessage(SpectrumManager spectrumManager) {
        this.mySpectrumManager = spectrumManager;
    }

    public Message clone() {
        return new CMessage(this.mySpectrumManager);
    }

    public SpectrumManager getMySpectrumManager() {
        return mySpectrumManager;
    }
}