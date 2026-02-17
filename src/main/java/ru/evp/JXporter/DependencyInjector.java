package ru.evp.JXporter;

import javafx.util.Callback;
import ru.evp.JXporter.controllers.MainController;
import ru.evp.JXporter.controllers.WorkSpaceController;
import ru.evp.JXporter.converters.DataReportConverter;
import ru.evp.JXporter.converters.DataReportConverterImpl;
import ru.evp.JXporter.report.CreditPaymentReportService;
import ru.evp.JXporter.report.CreditPaymentReportServiceImpl;
import ru.evp.JXporter.settings.SettingsService;
import ru.evp.JXporter.xml.NbkiXmlParser;
import ru.evp.JXporter.xml.SspXmlParser;
import ru.evp.JXporter.xml.XmlParser;
import ru.evp.JXporter.xml.XmlParserImpl;

public class DependencyInjector {
	// Dependencies
    private final XmlParser parser = new XmlParserImpl();
    private final NbkiXmlParser nbkiParser = new NbkiXmlParser(parser);
    private final SspXmlParser sspParser = new SspXmlParser(parser);
    private final CreditPaymentReportService reportsService = new CreditPaymentReportServiceImpl();
    private final DataReportConverter converter = new DataReportConverterImpl();
    private final SettingsService settingsService;
    
    public DependencyInjector(SettingsService settingsService) {
    	this.settingsService = settingsService;
    }
    
    public Callback<Class<?>, Object> getControllerFactory() {
        return type -> {
            try {
                if (type == MainController.class) {
                    return new MainController(this);
                }
                if (type == WorkSpaceController.class) {
                    return new WorkSpaceController(nbkiParser, sspParser, reportsService, converter, settingsService);
                }
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
