package ru.evp.JXporter.models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AvgPayment {
	/**
	 * Источник. ССП или НБКИ
	 */
	private String source;
	
	/**
	 * УИд сделки
	 */
	private String uuid;

	/**
	 * Среднемесячный платеж
	 */
	private BigDecimal averagePaymentAmount;
	
	/**
	 * Дата обновления
	 */
	private LocalDate updatedAt;
	
	/**
	 * Инициалы заемщика \ заемщиков
	 */
	private String initials;
	
	/**
	 *  Код основания прекращения обязательства
	 */
	private String loanIndicator;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public BigDecimal getAveragePaymentAmount() {
		return averagePaymentAmount;
	}

	public void setAveragePaymentAmount(BigDecimal averagePaymentAmount) {
		this.averagePaymentAmount = averagePaymentAmount;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public String getLoanIndicator() {
		return loanIndicator;
	}

	public void setLoanIndicator(String loanIndicator) {
		this.loanIndicator = loanIndicator;
	}
}
