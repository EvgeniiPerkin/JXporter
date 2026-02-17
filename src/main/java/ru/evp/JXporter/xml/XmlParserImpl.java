package ru.evp.JXporter.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

public class XmlParserImpl implements XmlParser {

    private final XPath xPath;

    public XmlParserImpl() {
        this.xPath = XPathFactory.newInstance().newXPath();
    }
    
    public Document parse(InputStream xmlInputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // ===== XXE PROTECTION =====
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            factory.setNamespaceAware(true);

            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            // ==========================

            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(xmlInputStream);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге XML", e);
        }
    }

    /**
     * Возвращает строковое значение XPath
     */
    public String getString(Document doc, String expression) {
        try {
            return xPath.evaluate(expression, doc);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка XPath: " + expression, e);
        }
    }

    /**
     * Возвращает NodeList по XPath
     */
    public NodeList getNodes(Document doc, String expression) {
        try {
            return (NodeList) xPath.evaluate(
                    expression,
                    doc,
                    XPathConstants.NODESET
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка XPath: " + expression, e);
        }
    }

    /**
     * Возвращает sub NodeList по XPath
     */
    public NodeList getSubNodes(Node parentNode, String expression) {
        try {
        	return (NodeList) xPath.evaluate(
        	        ".//" + expression,
        	        parentNode,
        	        XPathConstants.NODESET
        	);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка XPath: " + expression, e);
        }
    }
    
    /**
     * Возвращает sub Node
     */
    public Node getSubNode(Node parentNode, String expression) {
        try {
	    	XPath xpath = XPathFactory.newInstance().newXPath();
	
	    	return (Node) xpath.evaluate(
	    	        "./" + expression,
	    	        parentNode,
	    	        XPathConstants.NODE
	    	);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка XPath: " + expression, e);
        }
    }
    /**
     * Возвращает строку относительно конкретного узла
     */
    public String getString(Node node, String expression) {
        try {
            return xPath.evaluate(expression, node);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка XPath: " + expression, e);
        }
    }

    /**
     * Проверяет по тегу подходит ли документ для дальнейшего парсинга
     */
    public boolean hasTag(Document doc, String xPathExpression) {
        try {
            Node node = (Node) xPath.evaluate(
                    xPathExpression,
                    doc,
                    XPathConstants.NODE
            );
            return node != null;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка XPath при проверке тега: " + xPathExpression, e);
        }
    }
    
    public String getAttributeValue(Node node, String expression) {
        try {
		    return (String) xPath.evaluate(
		            expression,
		            node,
		            XPathConstants.STRING);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка Node при проверке атрибута: " + expression, e);
        }
    }
}
