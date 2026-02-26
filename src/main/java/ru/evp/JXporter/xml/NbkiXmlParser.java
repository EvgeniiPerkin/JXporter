package ru.evp.JXporter.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.evp.JXporter.models.AvgPayment;
import ru.evp.JXporter.models.Person;

public class NbkiXmlParser {
	private static final Logger logger = LogManager.getLogger(NbkiXmlParser.class);
	private final XmlParser parser;
	
	public NbkiXmlParser(XmlParser parser) {
		this.parser = parser;
	}

	public boolean isValidXml(File file) {		
		try (InputStream is =
		         new BufferedInputStream(new FileInputStream(file))) {
			Document doc;
		    doc = parser.parse(is);		    
			return parser.hasTag(doc, "//nbchScoringReport");
		} catch(Exception ex) {
			logger.error("Ошибка валидации файла xml nbki: " + ex.getMessage());
			return false;
		}
	}
	
	public Person getPerson(File file) {
		try (InputStream is =
		         new BufferedInputStream(new FileInputStream(file))) {
			Document doc;
		    doc = parser.parse(is);
		    
		    Person result = new Person();
		    result.setLastName(parser.getString(doc, "nbchScoringReport/product/prequest/req/PersonReq/name1"));
		    result.setFirstName(parser.getString(doc, "nbchScoringReport/product/prequest/req/PersonReq/first"));
		    result.setPatronymic(parser.getString(doc, "nbchScoringReport/product/prequest/req/PersonReq/paternal"));
		    result.setBirthDate(LocalDate.parse(parser.getString(doc, "nbchScoringReport/product/prequest/req/PersonReq/birthDt")));
		    
		    logger.info("Успешное получение из файла xml nbki данных о клиенте: " + result.getInitials());
			return result;
		} catch(Exception ex) {
			logger.error("Ошибка получения из файла xml nbki данных о клиенте: " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}		
	}
	
	public List<AvgPayment> getList(File file) {
		try (InputStream is =
		         new BufferedInputStream(new FileInputStream(file))) {
			Document doc;
		    doc = parser.parse(is);

		    logger.info("Запрос данных AccountReplyRUTDF в xml nbki.");
		    
		    NodeList nodeList = parser.getNodes(doc, "nbchScoringReport/product/preply/report/AccountReplyRUTDF");
		    Map<String, AvgPayment> items = new HashMap<String, AvgPayment>();
		    for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
	                AvgPayment payment = extractData(node);
	                if (items.containsKey(payment.getUuid())) {
	                	AvgPayment payment2 = items.get(payment.getUuid());
	                	items.put(payment.getUuid(), join(payment, payment2));
	                } else {
	                	items.put(payment.getUuid(), payment);
	                }
                }
            }
		    
		    List<AvgPayment> results = new ArrayList<AvgPayment>();
		    for (Map.Entry<String, AvgPayment> entry : items.entrySet()) {
		    	AvgPayment value = entry.getValue();
		    	if (value.getLoanIndicator().equals("EMPTY")) {
		    		results.add(value);
		    	}
		    }

		    logger.info("Успешное получение данных AccountReplyRUTDF из xml nbki.");
		    return results;
		} catch(Exception ex) {
			logger.error("Ошибка получения из файла xml nbki данных AccountReplyRUTDF: " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	private AvgPayment join(AvgPayment p1, AvgPayment p2) {
		AvgPayment result = new AvgPayment();
		
		result.setSource(p1.getSource());
		result.setUuid(p1.getUuid());
		result.setInitials(p1.getInitials());
		
		if (p1.getLoanIndicator().equals("EMPTY") || p2.getLoanIndicator().equals("EMPTY")) {
			result.setLoanIndicator(
					p1.getLoanIndicator().equals("EMPTY") ? 
							p2.getLoanIndicator()
							: p1.getLoanIndicator());
		} else {
			result.setLoanIndicator(p1.getLoanIndicator());
		}

		result.setAveragePaymentAmount(p1.getAveragePaymentAmount());
		result.setUpdatedAt(p2.getUpdatedAt());
		
		return result;
	}

	private AvgPayment extractData(Node node) {
        AvgPayment payment = new AvgPayment();
        payment.setSource("НБКИ");
        payment.setUuid(parser.getString(node, "uuid"));
        
        String li = parser.getString(node, "loanIndicator");	                
        if (li.equals(null) || li.equals("")) {
        	payment.setLoanIndicator("EMPTY");
        } else {
        	payment.setLoanIndicator(li);
        }
        		                
        List<LocalDate> dates = new ArrayList<LocalDate>();	
        NodeList trades = parser.getSubNodes(node, "trade");
	    for (int j = 0; j < trades.getLength(); j++) {
            Node childNode = trades.item(j);
            dates.add(LocalDate.parse(parser.getString(childNode, "reportingDt")));
        }
	    if (!dates.isEmpty()) {
            payment.setUpdatedAt(dates.getFirst());
        }
	    
        List<BigDecimal> amts = new ArrayList<BigDecimal>();
        NodeList monthAverPaymts = parser.getSubNodes(node, "monthAverPaymt");
	    for (int j = 0; j < monthAverPaymts.getLength(); j++) {
            Node childNode = monthAverPaymts.item(j);
            String strAmt = parser.getString(childNode, "averPaymtAmt");
            if (strAmt.equals(null) || strAmt.equals("")) {
            	amts.add(BigDecimal.ZERO);
            } else {
            	amts.add(new BigDecimal(strAmt));
            }
        }
	    if (!amts.isEmpty()) {
            payment.setAveragePaymentAmount(amts.getLast());
        }
	    
	    return payment;
	}
}
