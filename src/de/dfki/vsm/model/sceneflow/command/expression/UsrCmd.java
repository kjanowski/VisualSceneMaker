package de.dfki.vsm.model.sceneflow.command.expression;

import de.dfki.vsm.util.ios.IndentWriter;
import de.dfki.vsm.util.xml.XMLParseAction;
import de.dfki.vsm.util.xml.XMLParseError;
import de.dfki.vsm.util.xml.XMLWriteError;
import java.util.Vector;
import org.w3c.dom.Element;

/**
 * @author Gregor Mehlmann
 */
public class UsrCmd extends Expression {

    private String mName;
    private Vector<Expression> mArgList;

    public UsrCmd() {
        mName = new String();
        mArgList = new Vector<Expression>();
    }

    public UsrCmd(String name) {
        mName = name;
        mArgList = new Vector<Expression>();
    }

    public UsrCmd(String name, Vector<Expression> argList) {
        mName = name;
        mArgList = argList;
    }

    public String getName() {
        return mName;
    }

    public void setName(String value) {
        mName = value;
    }

    public Vector<Expression> getArgList() {
        return mArgList;
    }

    public void setArgList(Vector<Expression> value) {
        mArgList = value;
    }

    public int getSizeOfArgList() {
        return mArgList.size();
    }

    public Vector<Expression> getCopyOfArgList() {
        Vector<Expression> copy = new Vector<Expression>();
        for (Expression exp : mArgList) {
            copy.add(exp.getCopy());
        }
        return copy;
    }

    public boolean addArg(Expression value) {
        return mArgList.add(value);
    }

    public Expression getArgAt(int index) {
        return mArgList.get(index);
    }

    public ExpType getExpType() {
        return ExpType.USR;
    }

    public String getAbstractSyntax() {
        String desc = "Command( " + mName + "( ";
        for (int i = 0; i < mArgList.size(); i++) {
            desc += mArgList.get(i).getAbstractSyntax();
            if (i != mArgList.size() - 1) {
                desc += " , ";
            }
        }
        return desc + " ) )";
    }

    public String getConcreteSyntax() {
        String desc = mName + " ( ";
        for (int i = 0; i < mArgList.size(); i++) {
            desc += mArgList.get(i).getConcreteSyntax();
            if (i != mArgList.size() - 1) {
                desc += " , ";
            }
        }
        return desc + " )";
    }

    public String getFormattedSyntax() {
        String desc = "#b#" + mName + " ( ";
        for (int i = 0; i < mArgList.size(); i++) {
            desc += mArgList.get(i).getFormattedSyntax();
            if (i != mArgList.size() - 1) {
                desc += " , ";
            }
        }
        return desc + " ) ";
    }

    public UsrCmd getCopy() {
        return new UsrCmd(mName, getCopyOfArgList());
    }

    public void writeXML(IndentWriter out) throws XMLWriteError {
        out.println("<UserCommand name=\"" + mName + "\">").push();
        for (int i = 0; i < mArgList.size(); i++) {
            mArgList.get(i).writeXML(out);
        }
        out.pop().println("</UserCommand>");
    }

    public void parseXML(Element element) throws XMLParseError {
        mName = element.getAttribute("name");
        XMLParseAction.processChildNodes(element, new XMLParseAction() {

            public void run(Element element) throws XMLParseError {
                Expression exp = Expression.parse(element);
                mArgList.add(exp);
            }
        });
    }
}
