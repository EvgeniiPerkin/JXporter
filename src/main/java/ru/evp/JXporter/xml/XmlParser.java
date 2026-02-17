package ru.evp.JXporter.xml;

import java.io.InputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public interface XmlParser {
	public Document parse(InputStream xmlInputStream);
	public boolean hasTag(Document doc, String xPathExpression);
	public String getString(Document doc, String expression);
	public NodeList getNodes(Document doc, String expression);
	public NodeList getSubNodes(Node node, String expression);
	public Node getSubNode(Node parentNode, String expression);
	public String getString(Node node, String expression);
	public String getAttributeValue(Node node, String expression);
}
