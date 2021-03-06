package de.dfki.vsm.model.sceneflow.definition.type;

import de.dfki.vsm.util.ios.IndentWriter;
import org.w3c.dom.Element;

/**
 * A list type definition.
 *
 * @author Gregor Mehlmann
 */
public class ListTypeDef extends TypeDef {

    private String mType;

    public ListTypeDef() {
        mType = new String();
    }

    public ListTypeDef(String name, String type) {
        super(name);
        mType = type;
    }

    public String getType() {
        return mType;
    }

    public void setType(String value) {
        mType = value;
    }

    public String getAbstractSyntax() {
        return "ListTypeDef(" + mName + "," + mType + ")";
    }

    public String getConcreteSyntax() {
        return "List " + mName + " : " + mType;
    }

    public String getFormattedSyntax() {
        return "#h#List " + "#t#" + mName + " : " + mType;
    }

    public ListTypeDef getCopy() {
        return new ListTypeDef(mName, mType);
    }

    public Flavour getFlavour() {
        return Flavour.List;
    }

    public void writeXML(IndentWriter out) {
        out.println("<ListType name=\"" + mName + "\" type=\"" + mType + "\"/>");
    }

    public void parseXML(Element element) {
        mName = element.getAttribute("name");
        mType = element.getAttribute("type");
    }
}
