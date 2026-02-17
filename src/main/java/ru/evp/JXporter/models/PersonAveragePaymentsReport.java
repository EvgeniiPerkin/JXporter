package ru.evp.JXporter.models;

import java.util.List;

public class PersonAveragePaymentsReport {
	private Person person;
	private List<AvgPayment> payments;
		
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public List<AvgPayment> getPayments() {
		return payments;
	}
	public void setPayments(List<AvgPayment> payments) {
		this.payments= payments;
	}
}
