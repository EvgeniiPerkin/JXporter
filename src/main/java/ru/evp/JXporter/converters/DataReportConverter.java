package ru.evp.JXporter.converters;

import java.util.List;

import ru.evp.JXporter.models.AvgPayment;
import ru.evp.JXporter.models.Person;
import ru.evp.JXporter.models.PersonAveragePaymentsReport;

public interface DataReportConverter {
	/**
	 * Конвертирует данные платежей от НБКИ и СПП в набор данных для отчета
	 * @param person Данные клиента
	 * @param nbkiPayments Данные по платежам от НБКИ
	 * @param sspPaymants Данные по платежам от ССП
	 * @return
	 */
	PersonAveragePaymentsReport convert(Person person, List<AvgPayment> nbkiPayments, List<AvgPayment> sspPaymants);
	/**
	 * Объединяет данные для отчета 
	 * @param items Список данных для отчета
	 * @return Объедененные данные
	 */
	List<AvgPayment> join(List<PersonAveragePaymentsReport> items);
}
