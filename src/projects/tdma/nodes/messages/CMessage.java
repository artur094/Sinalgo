package projects.tdma.nodes.messages;

import projects.tdma.nodes.nodeImplementations.SpectrumManager;
import sinalgo.nodes.messages.Message;

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