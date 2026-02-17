package ru.evp.JXporter.report;

import java.io.OutputStream;
import java.util.List;

import ru.evp.JXporter.models.AvgPayment;
import ru.evp.JXporter.models.PersonAveragePaymentsReport;

public interface CreditPaymentReportService {
	/**
     * Генерирует XLSX отчет по кредитным платежам клиента
     *
     * @param combinedPayments объединеные данные для итоговоголиста
     * @param listDataReport параметры для формирования отчета
     * @param outputStream поток, в который будет записан XLSX
     */
	void generateReport(List<AvgPayment> combinedPayments, List<PersonAveragePaymentsReport> listDataReport, OutputStream outputStream);
}
