package de.dfki.vsm.model.sceneflow.command.expression.condition.constant;

import de.dfki.vsm.util.ios.IndentWriter;
import org.w3c.dom.Element;

/**
 * A boolean constant.
 *
 * @author Gregor Mehlmann
 */
public class Object extends Constant {

    private java.lang.Object mValue;

    public Object() {
        mValue = null;
    }

    public Object(java.lang.Object value) {
        mValue = value;
    }

    public java.lang.Object getValue() {
        return mValue;
    }

    public void setValue(java.lang.Object value) {
        mValue = value;
    }

    @Override
    public ConstType getConstType() {
        return ConstType.OBJECT;
    }

    @Override
    public java.lang.String getAbstractSyntax() {
        return "Object(" + getConcreteSyntax() + ")";
    }

    @Override
    public java.lang.String getConcreteSyntax() {
        if (mValue != null) {
            return mValue.toString();
        }
        return "null";
    }

    @Override
    public java.lang.String getFormattedSyntax() {
        return "#c#" + mValue;
    }

    @Override
    public Object getCopy() {
        return new Object(mValue);
    }

    @Override
    public void writeXML(IndentWriter out) {
        out.println("<Object value=\"" + mValue + "\"/>");
    }

    @Override
    public void parseXML(Element element) {
       // mValue = element.getAttribute("value");

    }
}
