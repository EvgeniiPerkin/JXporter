package ru.evp.JXporter.converters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.evp.JXporter.models.AvgPayment;
import ru.evp.JXporter.models.Person;
import ru.evp.JXporter.models.PersonAveragePaymentsReport;

public class DataReportConverterImpl implements DataReportConverter {
	private static final Logger logger = LogManager.getLogger(DataReportConverterImpl.class);

	@Override
	public PersonAveragePaymentsReport convert(Person person, List<AvgPayment> nbkiPayments, List<AvgPayment> sspPaymants) {
		PersonAveragePaymentsReport result = new PersonAveragePaymentsReport();
		result.setPerson(person);
		logger.info("Начало конвертации данных по: " + person.getInitials());
		
		// Получаем сгруппированные по `УИД` и отфильтрованные по `ДатеРасчета` данные из ССП
        List<AvgPayment> groupedSspPayments = new ArrayList<AvgPayment>();
    	Map<String, List<AvgPayment>> groupingItems = groupingByUuid(sspPaymants);
    	for(Map.Entry<String, List<AvgPayment>> item : groupingItems.entrySet()) {
    		groupedSspPayments.add(getAvgPaymentForMaxUpdatedAt(item.getValue()));
        }
    	
		logger.info("Данные ССП сгрупперованы и отфильтрованы. Объединение с данными НБКИ.");
    	
    	/* В цикле по данным из НБКИ и ССП нужно найти совпадения по УИД
    	 * Cогласно оговоренным условиям:
    	 * 	- Если в данных ССП нет совпадений, то нужно дополнить данными из НБКИ
    	 *  - Если есть совпадение по УИД то выбрать с более новой датой рассчета.
    	*/
    	for (AvgPayment nbkiPayment : nbkiPayments) {
    		
    		boolean isFound = false;
    		
    		for (int i = 0; i < groupedSspPayments.size(); i++) {
	    		
	    		AvgPayment sspPayment = groupedSspPayments.get(i);
	            
	    		if (nbkiPayment.getUuid().equals(sspPayment.getUuid())) {
	            	if (nbkiPayment.getUpdatedAt().isAfter(sspPayment.getUpdatedAt())) {
	            		groupedSspPayments.set(i, nbkiPayment);
	            	}
            		isFound = true;
            		break;
	            }
	        }
    		
    		if (!isFound) {
    			groupedSspPayments.add(nbkiPayment);
    		}
    	}

		logger.info("Успешная конвертации данных по: " + person.getInitials());
    	result.setPayments(groupedSspPayments);
        
		return result;
	}
	
	private AvgPayment getAvgPaymentForMaxUpdatedAt(List<AvgPayment> items) {
		return items.stream()
				.filter(m -> m.getUpdatedAt() != null)
		        .max(Comparator.comparing(AvgPayment::getUpdatedAt))
		        .orElseThrow();
	}
	
	private Map<String, List<AvgPayment>> groupingByUuid(List<AvgPayment> items) {		
		Map<String, List<AvgPayment>> retults = items.stream().collect(
                Collectors.groupingBy(AvgPayment::getUuid));
		return retults;
	}

	@Override
	public List<AvgPayment> join(List<PersonAveragePaymentsReport> items) {
		List<AvgPayment> results = new ArrayList<AvgPayment>();

		logger.info("Анализ данных для основного листа отчета.");
		for (PersonAveragePaymentsReport item : items) {
			for (AvgPayment payment : item.getPayments()) {
				
				payment.setInitials(item.getPerson().getInitials());
				
				boolean isFound = false;
				
				for (int i = 0; i < results.size(); i++) {
					if (payment.getUuid().equals(results.get(i).getUuid())) {
						String initials = results.get(i).getInitials();
						initials += "\n" + payment.getInitials();
						results.get(i).setInitials(initials);
						isFound = true;
						break;
					}
	    		}
				
				if (!isFound) {
					results.add(payment);
				}
    		}
		}

		logger.info("Успешное завершение анализа данных.");
		return results;
	}
}
