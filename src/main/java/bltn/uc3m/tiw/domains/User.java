package bltn.uc3m.tiw.domains;

import java.io.Serializable;


public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer userID;
	
	private String email;
	private String password;
	private String forename;
	private String surname;
	private String city;
	private boolean admin;
	
	public Integer getUserID() {
		return userID;
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	public String getForename() {
		return forename;
	}
	public String getSurname() {
		return surname;
	}
	public String getCity() {
		return city;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setUserID(Integer userID) {
		this.userID = userID;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setForename(String forename) {
		this.forename = forename;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}