package ru.evp.JXporter.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.evp.JXporter.models.AvgPayment;
import ru.evp.JXporter.models.Person;

public class SspXmlParser {
	private static final Logger logger = LogManager.getLogger(SspXmlParser.class);
	private final XmlParser parser;
	
	public SspXmlParser(XmlParser parser) {
		this.parser = parser;
	}

	public boolean isValidXml(File file) {		
		try (InputStream is =
		         new BufferedInputStream(new FileInputStream(file))) {
			Document doc;
		    doc = parser.parse(is);		    
			return parser.hasTag(doc, "//ОтветНаЗапросСведений/Сведения/КБКИ/Обязательства/БКИ/Договор");
		} catch(Exception ex) {
			logger.error("Ошибка валидации файла xml ssp: " + ex.getMessage());
			return false;
		}
	}
	
	public Person getPerson(File file) {
		try (InputStream is =
		         new BufferedInputStream(new FileInputStream(file))) {
			Document doc;
		    doc = parser.parse(is);
		    
		    Person result = new Person();
		    result.setLastName(parser.getString(doc, "ОтветНаЗапросСведений/Сведения/ТитульнаяЧасть/ФИО/Фамилия"));
		    result.setFirstName(parser.getString(doc, "ОтветНаЗапросСведений/Сведения/ТитульнаяЧасть/ФИО/Имя"));
		    result.setPatronymic(parser.getString(doc, "ОтветНаЗапросСведений/Сведения/ТитульнаяЧасть/ФИО/Отчество"));
		    result.setBirthDate(LocalDate.parse(parser.getString(doc, "ОтветНаЗапросСведений/Сведения/ТитульнаяЧасть/ДатаРождения")));
		    
		    logger.info("Успешное получение из файла xml ssp данных о клиенте: " + result.getInitials());
			return result;
		} catch(Exception ex) {
			logger.error("Ошибка получения из файла xml ssp данных о клиенте: " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}		
	}
	
	public List<AvgPayment> getList(File file) {
		try (InputStream is =
		         new BufferedInputStream(new FileInputStream(file))) {
			Document doc;
		    doc = parser.parse(is);
		    
		    logger.info("Запрос данных ОтветНаЗапросСведений в xml ssp.");
		    List<AvgPayment> results = new ArrayList<AvgPayment>();

		    NodeList nodeList = parser.getNodes(doc, "ОтветНаЗапросСведений/Сведения/КБКИ/Обязательства/БКИ/Договор");

		    for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                	AvgPayment item = new AvgPayment();

                	item.setSource("ССП");
                	item.setUuid(parser.getAttributeValue(node, "./@УИД"));
                	item.setUpdatedAt(LocalDate.parse(parser.getAttributeValue(node, "./СреднемесячныйПлатеж/@ДатаРасчета")));
                	item.setAveragePaymentAmount(new BigDecimal(parser.getString(node, "СреднемесячныйПлатеж")));

                	results.add(item);
                }	
            }
		    logger.info("Успешное получение данных ОтветНаЗапросСведений из xml ssp.");
		    return results;
		} catch(Exception ex) {
			logger.error("Ошибка получения из файла xml ssp данных ОтветНаЗапросСведений: " + ex.getMessage());
			throw new RuntimeException(ex.getMessage());
		}
		
	}
}
