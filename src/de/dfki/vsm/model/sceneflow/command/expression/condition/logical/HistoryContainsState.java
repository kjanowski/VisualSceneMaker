package de.dfki.vsm.model.sceneflow.command.expression.condition.logical;

import de.dfki.vsm.util.ios.IndentWriter;
import org.w3c.dom.Element;

/**
 *
 * @author Gregor Mehlmann
 */
public class HistoryContainsState extends LogicalCond {

    String mState;
    String mSubState;
    int mDepth;

    public HistoryContainsState() {
    }

    public HistoryContainsState(String state, String subState) {
        mState = state;
        mSubState = subState;
        mDepth = 0;
    }

    public HistoryContainsState(String state, String subState, int depth) {
        mState = state;
        mSubState = subState;
        mDepth = depth;
    }

    public String getState() {
        return mState;
    }

    public String getSubState() {
        return mSubState;
    }

    public int getDepth() {
        return mDepth;
    }

    public LogicalType getLogicalType() {
        return LogicalType.HC;
    }

    public String getAbstractSyntax() {
        return "HistoryContainsState(" + mState + "," + mSubState + "," + mDepth + ")";
    }

    public String getConcreteSyntax() {
        return "HistoryContainsState(" + mState + "," + mSubState + "," + mDepth + ")";
    }

    public String getFormattedSyntax() {
        return "";
    }

    public HistoryContainsState getCopy() {
        return new HistoryContainsState(mState, mSubState, mDepth);
    }

    public void writeXML(IndentWriter out) {
        out.println("<HistoryContainsState state=\"" + mState + "\" substate=\"" + mSubState + "\" depth=\"" + mDepth + "\"/>");

    }

    public void parseXML(Element element) {
        mState = element.getAttribute("state");
        mSubState = element.getAttribute("substate");
        mDepth = Integer.valueOf(element.getAttribute("depth"));
    }
}
