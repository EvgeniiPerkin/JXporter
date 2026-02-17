package ru.evp.JXporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.evp.JXporter.models.AvgPayment;
import ru.evp.JXporter.models.Person;
import ru.evp.JXporter.xml.SspXmlParser;
import ru.evp.JXporter.xml.XmlParser;

public class SspXmlParserTest {

    private File tempFile;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = File.createTempFile("test", ".xml");
        tempFile.deleteOnExit();
    }

    @Test
    void validXml_returnsTrue() {
        XmlParser parser = mock(XmlParser.class);
        when(parser.parse(any())).thenReturn(null);
        when(parser.hasTag(any(), eq("//ОтветНаЗапросСведений/Сведения/КБКИ/Обязательства/БКИ/Договор"))).thenReturn(true);

        SspXmlParser validator = new SspXmlParser(parser);

        assertTrue(validator.isValidXml(tempFile));
    }

    @Test
    void invalidXml_returnsFalse() {
        XmlParser parser = mock(XmlParser.class);
        when(parser.parse(any())).thenReturn(null);
        when(parser.hasTag(any(), eq("//other"))).thenReturn(false);
        SspXmlParser validator = new SspXmlParser(parser);

        assertFalse(validator.isValidXml(tempFile));
    }
    
    @Test
    void testGetPerson_returnsItem() {
        XmlParser parser = mock(XmlParser.class);
        when(parser.parse(any())).thenReturn(mock(Document.class));

        when(parser.getString(any(Document.class), eq("ОтветНаЗапросСведений/Сведения/ТитульнаяЧасть/ФИО/Фамилия")))
        	.thenReturn("Иванов");
        when(parser.getString(any(Document.class), eq("ОтветНаЗапросСведений/Сведения/ТитульнаяЧасть/ФИО/Имя")))
    		.thenReturn("Пётр");
        when(parser.getString(any(Document.class), eq("ОтветНаЗапросСведений/Сведения/ТитульнаяЧасть/ФИО/Отчество")))
    		.thenReturn("Васильевич");
        when(parser.getString(any(Document.class), eq("ОтветНаЗапросСведений/Сведения/ТитульнаяЧасть/ДатаРождения")))
    		.thenReturn("1990-04-24");

        SspXmlParser validator = new SspXmlParser(parser);
        Person result = validator.getPerson(tempFile);

        assertEquals("Иванов", result.getLastName());
        assertEquals("Пётр", result.getFirstName());
        assertEquals("Васильевич", result.getPatronymic());
        assertEquals(LocalDate.of(1990, 4, 24), result.getBirthDate());
    }
    
    @Test
    void testGetList_returnsItem() {
        XmlParser parser = mock(XmlParser.class);
        when(parser.parse(any())).thenReturn(mock(Document.class));
        
        NodeList nodeListMock = mock(NodeList.class);
        when(nodeListMock.getLength()).thenReturn(1);
        when(nodeListMock.item(0)).thenReturn(mock(Node.class));
        when(nodeListMock.item(0).getNodeType()).thenReturn(Node.ELEMENT_NODE);
        when(parser.getNodes(any(Document.class), anyString())).thenReturn(nodeListMock);

        when(parser.getAttributeValue(any(Node.class), eq("./@УИД"))).thenReturn("sd7f68s76df876sd76d");
        when(parser.getAttributeValue(any(Node.class), eq("./СреднемесячныйПлатеж/@ДатаРасчета"))).thenReturn("2025-12-31");
        when(parser.getString(any(Node.class), eq("СреднемесячныйПлатеж"))).thenReturn("312341");
        
        SspXmlParser validator = new SspXmlParser(parser);
        List<AvgPayment> result = validator.getList(tempFile);

        assertEquals(1, result.size());
        assertEquals("sd7f68s76df876sd76d", result.get(0).getUuid());
        assertEquals(LocalDate.of(2025, 12, 31), result.get(0).getUpdatedAt());
        assertEquals(new BigDecimal("312341"), result.get(0).getAveragePaymentAmount());
    }
}
