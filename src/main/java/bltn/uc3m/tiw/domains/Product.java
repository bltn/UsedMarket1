package bltn.uc3m.tiw.domains;

import java.io.Serializable;
import java.sql.Blob;

import javax.validation.constraints.Size;

public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer productID;
	@Size(min = 2, max = 40)
	private String title;
	@Size(min  = 2, max = 400)
	private String description;
	private Category category;
	private byte[] photo;
	private Integer price;
	private Availability availability; 
	private Integer userID;
	
	public Product() {}
	
	public Product(String title, String description, String category,
			byte[] photo, Integer price, Integer userID) {
		
		this.productID = null;
		this.title = title;
		this.description = description;
		this.category = stringToCategory(category);
		this.photo = photo;
		this.price = price;
		this.availability = Availability.AVAILABLE;
		this.userID = userID;
	}
	
	public Integer getProductID() {
		return productID;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public int getPrice() {
		return price;
	}
	
	public Category getCategory() {
		return category;
	}

	public Availability getAvailability() {
		return availability;
	}

	public int getUserID() {
		return userID;
	}

	public void setProductID(Integer productID) {
		this.productID = productID;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}
	
	public void setCategory(Category category) {
		this.category = category;
	}
	
	public static enum Category {
		BEDROOM, KITCHEN, OFFICE, BATHROOM, OTHER;
	}
	
	public static enum Availability {
		AVAILABLE, RESERVED, SOLD;
	}
	
	private Category stringToCategory(String cat) {
		if (cat.equalsIgnoreCase("bedroom")) {
			return Category.BEDROOM;
		} else if (cat.equalsIgnoreCase("kitchen")) {
			return Category.KITCHEN;
		} else if (cat.equalsIgnoreCase("office")) {
			return Category.OFFICE;
		} else if (cat.equalsIgnoreCase("bathroom")) {
			return Category.BATHROOM;
		} else {
			return Category.OTHER;
		}
	}
	
	private Availability stringToAvailability(String avail) {
		if (avail.equalsIgnoreCase("reserved")) {
			return Availability.RESERVED;
		} else if (avail.equals("sold")) {
			return Availability.SOLD;
		} else {
			return Availability.AVAILABLE;
		}
	}

}
