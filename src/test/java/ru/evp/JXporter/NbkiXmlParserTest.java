package ru.evp.JXporter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.evp.JXporter.models.AvgPayment;
import ru.evp.JXporter.models.Person;
import ru.evp.JXporter.xml.NbkiXmlParser;
import ru.evp.JXporter.xml.XmlParser;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class NbkiXmlParserTest {

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
        when(parser.hasTag(any(), eq("//nbchScoringReport"))).thenReturn(true);

        NbkiXmlParser validator = new NbkiXmlParser(parser);

        assertTrue(validator.isValidXml(tempFile));
    }

    @Test
    void invalidXml_returnsFalse() {
        XmlParser parser = mock(XmlParser.class);
        when(parser.parse(any())).thenReturn(null);
        when(parser.hasTag(any(), eq("//other"))).thenReturn(false);
        NbkiXmlParser validator = new NbkiXmlParser(parser);

        assertFalse(validator.isValidXml(tempFile));
    }

    @Test
    void testGetPerson_returnsItem() {
        XmlParser parser = mock(XmlParser.class);
        when(parser.parse(any())).thenReturn(mock(Document.class));

        when(parser.getString(any(Document.class), eq("nbchScoringReport/product/prequest/req/PersonReq/name1")))
        	.thenReturn("Иванов");
        when(parser.getString(any(Document.class), eq("nbchScoringReport/product/prequest/req/PersonReq/first")))
    		.thenReturn("Пётр");
        when(parser.getString(any(Document.class), eq("nbchScoringReport/product/prequest/req/PersonReq/paternal")))
    		.thenReturn("Васильевич");
        when(parser.getString(any(Document.class), eq("nbchScoringReport/product/prequest/req/PersonReq/birthDt")))
    		.thenReturn("1990-04-24");

        NbkiXmlParser validator = new NbkiXmlParser(parser);
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
        when(parser.getSubNodes(any(Node.class), anyString())).thenReturn(nodeListMock);

        when(parser.getString(any(Node.class), eq("uuid"))).thenReturn("&*9sdf&*(");
        when(parser.getString(any(Node.class), eq("loanIndicator"))).thenReturn("");
        when(parser.getString(any(Node.class), eq("reportingDt"))).thenReturn("2024-12-31");
        when(parser.getString(any(Node.class), eq("averPaymtAmt"))).thenReturn("1234.2");
        
        NbkiXmlParser validator = new NbkiXmlParser(parser);
        List<AvgPayment> result = validator.getList(tempFile);

        assertEquals(1, result.size());
        assertEquals("&*9sdf&*(", result.get(0).getUuid());
        assertEquals(LocalDate.of(2024, 12, 31), result.get(0).getUpdatedAt());
        assertEquals(new BigDecimal("1234.2"), result.get(0).getAveragePaymentAmount());
    }
    
    @Test
    void testGetList_returnsItem2() {
        XmlParser parser = mock(XmlParser.class);
        when(parser.parse(any())).thenReturn(mock(Document.class));
        
        NodeList nodeListMock = mock(NodeList.class);
        when(nodeListMock.getLength()).thenReturn(1);
        when(nodeListMock.item(0)).thenReturn(mock(Node.class));
        when(nodeListMock.item(0).getNodeType()).thenReturn(Node.ELEMENT_NODE);
        when(parser.getNodes(any(Document.class), anyString())).thenReturn(nodeListMock);
        
        NodeList nodeListMock2 = mock(NodeList.class);
        when(nodeListMock2.getLength()).thenReturn(0);
        when(parser.getSubNodes(any(Node.class), anyString())).thenReturn(nodeListMock2);

        when(parser.getString(any(Node.class), eq("uuid"))).thenReturn("&*9sdf&*(");
        when(parser.getString(any(Node.class), eq("loanIndicator"))).thenReturn("");
        when(parser.getString(any(Node.class), eq("reportingDt"))).thenReturn("2024-12-31");
        when(parser.getString(any(Node.class), eq("averPaymtAmt"))).thenReturn("1234.2");
        
        NbkiXmlParser validator = new NbkiXmlParser(parser);
        List<AvgPayment> result = validator.getList(tempFile);

        assertEquals(1, result.size());
        assertEquals("&*9sdf&*(", result.get(0).getUuid());
        assertEquals(null, result.get(0).getUpdatedAt());
        assertEquals(null, result.get(0).getAveragePaymentAmount());
    }
}
