package ru.evp.JXporter.report;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ru.evp.JXporter.models.AvgPayment;
import ru.evp.JXporter.models.PersonAveragePaymentsReport;

public class CreditPaymentReportServiceImpl implements CreditPaymentReportService {
	private static final Logger logger = LogManager.getLogger(CreditPaymentReportServiceImpl.class);
	
	private CellStyle dateStyle;
	private CellStyle numberStyle;
	private CellStyle textWrapStyle;
	private CellStyle textStyle;
	
	@Override
	public void generateReport(List<AvgPayment> combinedPayments, List<PersonAveragePaymentsReport> items, OutputStream outputStream) {
		try (Workbook workbook = new XSSFWorkbook()) {
			logger.info("Создание отчета xlsx.");
	        
	        fillStyles(workbook);
			logger.info("Создание стилей.");
			
			createPdnSheet(workbook, combinedPayments);
			for (PersonAveragePaymentsReport item: items) {
				createPersonSheet(workbook, item);
			}
            
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();

			logger.info("Отчет сформирован.");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации отчета", e);
        }
	}

	private void createPdnSheet(Workbook workbook, List<AvgPayment> combinedPayments) {
        Sheet sheet = workbook.createSheet("ПДН");
		logger.info("Создание листа ПДН.");
        
        sheet.setColumnWidth(0, 60 * 256);
        for (int i = 1; i < combinedPayments.size() + 3; i++) {
            sheet.setColumnWidth(i, 24 * 256);            	
        }
        
        setValue(sheet, 0, 0, true, 60, "ПСК - полная стоимость потребительского кредита (займа) в соответствии с договором кредита (займа), указанная в кредитном отчете, предоставляемом бюро кредитных историй, в процентах годовых;");
        setValue(sheet, 1, 0, true, 60, "СрЗ - сумма срочной задолженности по договору кредита (займа) без учета задолженности по процентным платежам, определенная с использованием информации, указанной в кредитном отчете, предоставляемом бюро кредитных историй;");
        setValue(sheet, 2, 0, true, 60, "ПрЗ - сумма просроченной задолженности по договору кредита (займа), определенная с использованием информации, указанной в кредитном отчете, предоставляемом бюро кредитных историй;");
        setValue(sheet, 3, 0, true, 60, "T - количество месяцев, оставшихся до погашения кредита (займа), определенное с использованием информации, указанной в кредитном отчете, предоставляемом бюро кредитных историй.");
        
        setValue(sheet, 4, 0, true, 60, "Среднемесячный Платеж");
        
        setFormula(sheet, 4, 1, "SUM(" + 
        		getRangeReference(4, 2, 4, combinedPayments.size() + 2) + ")");
        setValue(sheet, 5, 1,"Источник данных");
        setValue(sheet, 6, 1, true, 0,"УИД");
        setValue(sheet, 7, 1, "");
        setValue(sheet, 8, 1, true, 0, "Данные кого из созаемщиков использованы");            
        
        setValue(sheet, 4, 2, 0.0);
        setValue(sheet, 5, 2, "Заявка");
        setValue(sheet, 6, 2, true, 0, "запрашиваемый кредит");
        setValue(sheet, 7, 2,"Дата обновления");
        setValue(sheet, 8, 2, "");  
        
        int columnNumber = 3;
        for (AvgPayment payment : combinedPayments) {
        	setValue(sheet, 4, columnNumber, payment.getAveragePaymentAmount());
        	setValue(sheet, 5, columnNumber, payment.getSource());
        	setValue(sheet, 6, columnNumber, true, 0, payment.getUuid());
        	setValue(sheet, 7, columnNumber, payment.getUpdatedAt());
        	setValue(sheet, 8, columnNumber, true, 0, payment.getInitials());
			columnNumber++;
        }
        
        setValue(sheet, 9, 1, true, 0, "СОВОКУПНЫЕ СРЕДНЕМЕСЯЧНЫЕ ПЛАТЕЖИ СОЗАЕМЩИКОВ");            
        setValue(sheet, 9, 3, true, 0, "Доходы");
        setValue(sheet, 9, 4, true, 0, "");
        sheet.addMergedRegion(new CellRangeAddress(9, 9, 3, 4));
        setValue(sheet, 10, 0, "Среднемесячный Доход");
        setFormula(sheet, 10, 1, "D11+E12");
        setValue(sheet, 10, 3, 0.0);
        setValue(sheet, 10, 4, 0.0);
        setValue(sheet, 11, 4, 0.0);

        setValue(sheet, 12, 0, "ПДН");
        setFormula(sheet, 12, 1, "B5/B11");

        setValue(sheet, 15, 0, "Заемщик:");
        setValue(sheet, 15, 1, "");
        setValue(sheet, 16, 0, "Дата ССП:");
        setValue(sheet, 16, 1, "");
        setValue(sheet, 17, 0, "Дата НБКИ:");
        setValue(sheet, 17, 1, "");
        setValue(sheet, 18, 0, "Дата расчета ПДН:");
        setValue(sheet, 18, 1, LocalDate.now());
	}
	
	private void createPersonSheet(Workbook workbook, PersonAveragePaymentsReport item) {
		String initialsPerson = item.getPerson().getInitials();        
		Sheet sheet = workbook.createSheet(initialsPerson);
		logger.info("Создание листа " + initialsPerson);
        
        List<AvgPayment> payments = item.getPayments();
        
        sheet.setColumnWidth(0, 60 * 256);
        for (int i = 1; i < payments.size() + 2; i++) {
            sheet.setColumnWidth(i, 24 * 256);            	
        }
        setValue(sheet, 0, 0, true, 60, "ПСК - полная стоимость потребительского кредита (займа) в соответствии с договором кредита (займа), указанная в кредитном отчете, предоставляемом бюро кредитных историй, в процентах годовых;");
        setValue(sheet, 1, 0, true, 60, "СрЗ - сумма срочной задолженности по договору кредита (займа) без учета задолженности по процентным платежам, определенная с использованием информации, указанной в кредитном отчете, предоставляемом бюро кредитных историй;");
        setValue(sheet, 2, 0, true, 60, "ПрЗ - сумма просроченной задолженности по договору кредита (займа), определенная с использованием информации, указанной в кредитном отчете, предоставляемом бюро кредитных историй;");
        setValue(sheet, 3, 0, true, 60, "T - количество месяцев, оставшихся до погашения кредита (займа), определенное с использованием информации, указанной в кредитном отчете, предоставляемом бюро кредитных историй.");

        setValue(sheet, 4, 0, true, 60, "Среднемесячный Платеж");
        
        setFormula(sheet, 4, 1, "SUM(" + 
        		getRangeReference(4, 2, 4, payments.size() + 1) + ")");
        setValue(sheet, 5, 1,"Источник данных");
        setValue(sheet, 6, 1, true, 0,"УИД");
        setValue(sheet, 7, 1, "Дата обновления");
        
        int columnNumber = 2;
        for (AvgPayment payment : payments) {
        	setValue(sheet, 4, columnNumber, payment.getAveragePaymentAmount());
        	setValue(sheet, 5, columnNumber, payment.getSource());
        	setValue(sheet, 6, columnNumber, true, 0, payment.getUuid());
        	setValue(sheet, 7, columnNumber, payment.getUpdatedAt());
			columnNumber++;
        }
	}

	private void fillStyles(Workbook workbook) {
        DataFormat format = workbook.createDataFormat();
     
        dateStyle = workbook.createCellStyle();
        dateStyle.setAlignment(HorizontalAlignment.CENTER);
        dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        dateStyle.setBorderTop(BorderStyle.THIN);
        dateStyle.setBorderBottom(BorderStyle.THIN);
        dateStyle.setBorderLeft(BorderStyle.THIN);
        dateStyle.setBorderRight(BorderStyle.THIN);		
        dateStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        dateStyle.setDataFormat(format.getFormat("dd.mm.yyyy"));

        numberStyle = workbook.createCellStyle();
        numberStyle.setAlignment(HorizontalAlignment.CENTER);
        numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        numberStyle.setBorderTop(BorderStyle.THIN);
        numberStyle.setBorderBottom(BorderStyle.THIN);
        numberStyle.setBorderLeft(BorderStyle.THIN);
        numberStyle.setBorderRight(BorderStyle.THIN);		
        numberStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        numberStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        numberStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        numberStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        numberStyle.setDataFormat(format.getFormat("#,##0.00"));

        textWrapStyle = workbook.createCellStyle();
        textWrapStyle.setAlignment(HorizontalAlignment.LEFT);
        textWrapStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textWrapStyle.setBorderTop(BorderStyle.THIN);
        textWrapStyle.setBorderBottom(BorderStyle.THIN);
        textWrapStyle.setBorderLeft(BorderStyle.THIN);
        textWrapStyle.setBorderRight(BorderStyle.THIN);		
        textWrapStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        textWrapStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        textWrapStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        textWrapStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        textWrapStyle.setWrapText(true);

        textStyle = workbook.createCellStyle();
        textStyle.setAlignment(HorizontalAlignment.LEFT);
        textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        textStyle.setBorderTop(BorderStyle.THIN);
        textStyle.setBorderBottom(BorderStyle.THIN);
        textStyle.setBorderLeft(BorderStyle.THIN);
        textStyle.setBorderRight(BorderStyle.THIN);		
        textStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
        textStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        textStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        textStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
	}
	
	private String getRangeReference(int startRowNumber, int startColumnNumber, 
			int endRowNumber, int endColumnNumber) {
        CellReference startRef = new CellReference(startRowNumber, startColumnNumber);
        CellReference endRef = new CellReference(endRowNumber, endColumnNumber);
        return startRef.formatAsString() + ":" + endRef.formatAsString();
	}
	
	private void setValue(Sheet sheet, int rowNumber, int columnNumber, boolean isWrap, int rowHeight, String value) {
		Row r = sheet.getRow(rowNumber);
		if (r == null) {
			r = sheet.createRow(rowNumber);
			if (rowHeight != 0) {
				r.setHeightInPoints(rowHeight);
			}
		}
		
		Cell c = r.getCell(columnNumber);
		if (c == null) {
			c = r.createCell(columnNumber);
		}
		c.setCellValue(value);
		c.setCellStyle(isWrap ? textWrapStyle : textStyle);
	}
	
	private void setValue(Sheet sheet, int rowNumber, int columnNumber, String value) {
		Row r = sheet.getRow(rowNumber);
		if (r == null) {
			r = sheet.createRow(rowNumber);
		}//.setHeightInPoints(60);
		
		Cell c = r.getCell(columnNumber);
		if (c == null) {
			c = r.createCell(columnNumber);
		}
		c.setCellValue(value);
		c.setCellStyle(textStyle);
	}
	
	private void setValue(Sheet sheet, int rowNumber, int columnNumber, BigDecimal value) {
		Row r = sheet.getRow(rowNumber);
		if (r == null) {
			r = sheet.createRow(rowNumber);
		}
		
		Cell c = r.getCell(columnNumber);
		if (c == null) {
			c = r.createCell(columnNumber);
		}
		c.setCellValue(value == null ? 0.0: value.doubleValue());
		c.setCellStyle(numberStyle);
	}
	
	private void setValue(Sheet sheet, int rowNumber, int columnNumber, double value) {
		Row r = sheet.getRow(rowNumber);
		if (r == null) {
			r = sheet.createRow(rowNumber);
		}
		
		Cell c = r.getCell(columnNumber);
		if (c == null) {
			c = r.createCell(columnNumber);
		}
		c.setCellValue(value);
		c.setCellStyle(numberStyle);
	}
	
	private void setValue(Sheet sheet, int rowNumber, int columnNumber, LocalDate value) {
		Row r = sheet.getRow(rowNumber);
		if (r == null) {
			r = sheet.createRow(rowNumber);
		}
		
		Cell c = r.getCell(columnNumber);
		if (c == null) {
			c = r.createCell(columnNumber);
		}
		c.setCellValue(value);
		c.setCellStyle(dateStyle);
	}
	
	private void setFormula(Sheet sheet, int rowNumber, int columnNumber, String formula) {
		Row r = sheet.getRow(rowNumber);
		if (r == null) {
			r = sheet.createRow(rowNumber);
		}
		
		Cell c = r.getCell(columnNumber);
		if (c == null) {
			c = r.createCell(columnNumber);
		}
		c.setCellFormula(formula);
		c.setCellStyle(numberStyle);
	}
}
