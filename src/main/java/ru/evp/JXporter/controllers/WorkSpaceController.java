package ru.evp.JXporter.controllers;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.evp.JXporter.AlertUtil;
import ru.evp.JXporter.converters.DataReportConverter;
import ru.evp.JXporter.models.Person;
import ru.evp.JXporter.models.PersonAveragePaymentsReport;
import ru.evp.JXporter.report.CreditPaymentReportService;
import ru.evp.JXporter.settings.AppSettings;
import ru.evp.JXporter.settings.SettingsService;
import ru.evp.JXporter.xml.NbkiXmlParser;
import ru.evp.JXporter.xml.SspXmlParser;

public class WorkSpaceController {
	private static final Logger logger = LogManager.getLogger(WorkSpaceController.class);
	private final Map<String, File> files = new HashMap<>();
	private final NbkiXmlParser nbkiParser;
	private final SspXmlParser sspParser;
	private final CreditPaymentReportService reportsService;
	private final DataReportConverter converter;
    private final AppSettings settings;
	
	@FXML
	private TextField sspFileField1;
	@FXML
	private TextField nbkiFileField1;
	@FXML
	private TextField sspFileField2;
	@FXML
	private TextField nbkiFileField2;
	@FXML
	private TextField sspFileField3;
	@FXML
	private TextField nbkiFileField3;
	@FXML
	private TextField sspFileField4;
	@FXML
	private TextField nbkiFileField4;

	public WorkSpaceController(
			NbkiXmlParser nbkiParser, SspXmlParser sspParser, 
			CreditPaymentReportService reportsService, DataReportConverter converter, 
			SettingsService settingsService) {
	    this.nbkiParser = nbkiParser;
	    this.sspParser = sspParser;
	    this.reportsService = reportsService;
	    this.converter = converter;
        this.settings = settingsService.getSettings();
	}
	
	@FXML
	private void btnSelectSspFile1(ActionEvent event) {
		selectAndValidationSspFile(event, "ssp1", sspFileField1);
	}
	@FXML 
	private void btnSelectNbkiFile1(ActionEvent event) {
		selectAndValidationNbkiFile(event, "nbki1", nbkiFileField1);
	}
	
	@FXML
	private void btnSelectSspFile2(ActionEvent event) {
		selectAndValidationSspFile(event, "ssp2", sspFileField2);
	}
	@FXML 
	private void btnSelectNbkiFile2(ActionEvent event) {
		selectAndValidationNbkiFile(event, "nbki2", nbkiFileField2);
	}

	@FXML
	private void btnSelectSspFile3(ActionEvent event) {
		selectAndValidationSspFile(event, "ssp3", sspFileField3);
	}
	@FXML 
	private void btnSelectNbkiFile3(ActionEvent event) {
		selectAndValidationNbkiFile(event, "nbki3", nbkiFileField3);
	}
	
	@FXML
	private void btnSelectSspFile4(ActionEvent event) {
		selectAndValidationSspFile(event, "ssp4", sspFileField4);
	}
	@FXML 
	private void btnSelectNbkiFile4(ActionEvent event) {
		selectAndValidationNbkiFile(event, "nbki4", nbkiFileField4);
	}
	@FXML 
	private void btnCreateReport(ActionEvent event) {
		try {
			File dir = chooseSaveDirectory(event);
			if (dir == null) {
    	    	AlertUtil.warning("Предупреждение.", "Директория не выбрана.", "Отмена создания фала отчета.");
    	    	return;
			}
			logger.info("Выбор директории для сохранения файла отчета: " + dir.getAbsolutePath());
		    String pathToSave = dir.getAbsolutePath();
		    
		    File fSsp = files.get("ssp1");
		    File fNbki = files.get("nbki1");
		    if (fSsp == null || fNbki == null) {
				AlertUtil.warning("Предупреждение.", "Отсутствует один из файлов заемщика №1.", "Отмена создания фала отчета.");
				return;
		    }
		    
		    List<PersonAveragePaymentsReport> listDataReport = new ArrayList<PersonAveragePaymentsReport>();
		    addNotNull(listDataReport, buildPersonAveragePaymentsReport(1, fSsp, fNbki));
		    addNotNull(listDataReport, buildPersonAveragePaymentsReport(2, files.get("ssp2"), files.get("nbki2")));
		    addNotNull(listDataReport, buildPersonAveragePaymentsReport(3, files.get("ssp3"), files.get("nbki3")));
		    addNotNull(listDataReport, buildPersonAveragePaymentsReport(4, files.get("ssp4"), files.get("nbki4")));
		    
			logger.info("Кол-во участников в отчете: " + listDataReport.size());
		    
		    Path pathToFile = Path.of(pathToSave, UUID.randomUUID().toString() + ".xlsx");
		    OutputStream os = Files.newOutputStream(pathToFile);
		    reportsService.generateReport(converter.join(listDataReport), listDataReport, os);
			
		    logger.info("Отчет успешно сформирован по пути: " + pathToFile.toAbsolutePath());
		    AlertUtil.info("Информация", "Отчет успешно создан.", "Путь к файлу отчета: " + pathToFile.toAbsolutePath());
		} catch (Exception ex) {
			AlertUtil.error("Ошибка.", "Создание файла отчета xlsx.", ex.getMessage());
		}
	}
	
	private <T> void addNotNull(List<T> list, T element) {
	    if (element != null) {
	        list.add(element);
	    }
	}
	
	private PersonAveragePaymentsReport buildPersonAveragePaymentsReport(int personNumber, File fSsp, File fNbki) {
		if (fSsp == null || fNbki == null) {
	    	return null;
	    }
	    
		Person p1 = sspParser.getPerson(fSsp);
	    Person p2 = nbkiParser.getPerson(fNbki);
	    if (!p1.equals(p2)) {
	    	throw new RuntimeException("Данные в файлах xml заемщика №" + personNumber + " принадлежат разным клиентам.");
	    }
	    
	    return converter.convert(p1, nbkiParser.getList(fNbki), sspParser.getList(fSsp));
	}

	private void selectAndValidationSspFile(ActionEvent event, String key, TextField textFeild) {
		try {	        
	        File file = chooseFile(event, settings.getInitialDirectory());
	        
	        if (file != null) {
	        	if (sspParser.isValidXml(file)) {
		        	files.put(key, file);
		        	textFeild.setText(file.getAbsolutePath());
	        	} else {
	    	    	AlertUtil.warning("Внимание.", "Файл ССП не распознан.", "Не найдены совпадения по ключевым тэгам и атрибутам.");
	    	    	files.remove(key);
	    	    	textFeild.setText("");
	        	}
	        } else {
    	    	AlertUtil.warning("Внимание.", "Файл ССП не выбран.", "Выбирите подходящий файл xml.");
    	    	files.remove(key);
    	    	textFeild.setText("");
        	}
        } catch (Exception ex) {
	    	AlertUtil.error("Ошибка.", "Выбор файла ССП xml.",  ex.getMessage());
	    	files.remove(key);
	    	textFeild.setText("");
	    	logger.error("Ошибка выбора файла ССП: " + ex.getMessage());
        }
	}
	private void selectAndValidationNbkiFile(ActionEvent event, String key, TextField textFeild) {
		try {	        
	        File file = chooseFile(event, settings.getInitialDirectory());
	        
	        if (file != null) {
	        	if (nbkiParser.isValidXml(file)) {
		        	files.put(key, file);
		        	textFeild.setText(file.getAbsolutePath());
	        	} else {
	    	    	AlertUtil.warning("Внимание.", "Файл НБКИ не распознан.", "Не найдены совпадения по ключевым тэгам и атрибутам.");
	    	    	files.remove(key);
	    	    	textFeild.setText("");
	        	}
	        } else {
    	    	AlertUtil.warning("Внимание.", "Файл НБКИ не выбран.", "Выбирите подходящий файл xml.");
    	    	files.remove(key);
    	    	textFeild.setText("");
        	}
        } catch (Exception ex) {
	    	AlertUtil.error("Ошибка.", "Выбор файла НБКИ xml.",  ex.getMessage());
	    	files.remove(key);
	    	textFeild.setText("");
	    	logger.error("Ошибка выбора файла НБКИ: " + ex.getMessage());
        }
	}
	
	private File chooseFile(ActionEvent event, String initialDirectory) {
        Stage stage = (Stage)((Node)event.getSource())
                .getScene()
                .getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите файл xml");
        if (initialDirectory != null && !initialDirectory.isEmpty()) {
        	Path path = Path.of(initialDirectory);
        	if (Files.exists(path)) {
                fileChooser.setInitialDirectory(new File(initialDirectory));
        	}
    	}
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml")
        );
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
	        settings.setInitialDirectory(file.getParent());
	    }
        return file;
	}
	
	public File chooseSaveDirectory(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource())
                .getScene()
                .getWindow();
	    DirectoryChooser directoryChooser = new DirectoryChooser();
	    directoryChooser.setTitle("Выберите директорию для сохранения.");
	    directoryChooser.setInitialDirectory(new File(settings.getInitialDirectory()));
	    File dir = directoryChooser.showDialog(stage);
	    if (dir != null) {
	        settings.setInitialDirectory(dir.getAbsolutePath());
	    }
	    return dir;
	}
}
