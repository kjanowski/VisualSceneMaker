package de.dfki.vsm.model.sceneflow.command;

import de.dfki.vsm.model.sceneflow.command.Command.CmdType;
import de.dfki.vsm.util.ios.IndentWriter;
import org.w3c.dom.Element;

/**
 * @author Gregor Mehlmann
 */
public class HistoryClear extends Command {

    private String mState;

    public HistoryClear() {
    }

    public HistoryClear(String state) {
        mState = state;
    }

    public String getState() {
        return mState;
    }

    public CmdType getCmdType() {
        return CmdType.HC;
    }

    public String getAbstractSyntax() {
        return "HistoryClear ( " + mState + " )";
    }

    public String getConcreteSyntax() {
        return "HistoryClear ( " + mState + " )";
    }

    public String getFormattedSyntax() {
        return "#p#HistoryClear ( " + "#c#" + mState + " )";
    }

    public HistoryClear getCopy() {
        return new HistoryClear(mState);
    }

    public void writeXML(IndentWriter out) {
        out.println("<HistoryClear state=\"" + mState + "\"/>");

    }

    public void parseXML(Element element) {
        mState = element.getAttribute("state");
    }
}
