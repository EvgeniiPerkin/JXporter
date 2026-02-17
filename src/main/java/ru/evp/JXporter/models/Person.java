package ru.evp.JXporter.models;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public class Person {
	private String lastName;
	private String firstName;
	private String patronymic;
	private LocalDate birthDate;
	
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getPatronymic() {
		return patronymic;
	}
	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}
	
	public LocalDate getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getInitials() {
		String initialsPerson = lastName;
		initialsPerson += firstName.isEmpty() ? "" : " " + firstName.charAt(0);
		initialsPerson += patronymic.isEmpty() ? "" : patronymic.charAt(0);
		return initialsPerson;
	}
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    
	    Person person = (Person) o;
	    
	    return Objects.equals(birthDate, person.birthDate) && 
	    		equalsIgnoreCase(firstName, person.firstName)
	            && equalsIgnoreCase(lastName, person.lastName)
	            && equalsIgnoreCase(patronymic, person.patronymic);
	}
	
	private boolean equalsIgnoreCase(String s1, String s2) {
	    if (s1 == null && s2 == null) return true;
	    if (s1 == null || s2 == null) return false;
	    return s1.equalsIgnoreCase(s2);
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(
	            normalize(firstName),
	            normalize(lastName),
	            normalize(patronymic),
	            birthDate
	        );
	}
	private String normalize(String s) {
	    return s == null ? null : s.toLowerCase(Locale.ROOT);
	}
}
