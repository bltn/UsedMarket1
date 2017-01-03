package bltn.uc3m.tiw.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import bltn.uc3m.tiw.domains.Product;
import bltn.uc3m.tiw.domains.User;

@Controller
public class ProductController {
	
	@Autowired
	RestTemplate restTemplate;
	
	/*
	 * Render page showing all products
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/products/index")
	public String renderProductIndexPage(Model model) {
		List<Product> products = restTemplate.getForObject("http://"
				+ "localhost:8082/products/index", List.class);

		model.addAttribute("products", products);
		return "products/index";
	}
	
	/*
	 * Render new product page and bind empty product object
	 * to the form 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/products/new")
	public String renderNewProductPage(Model model) {
		Product product = new Product();
		model.addAttribute(product);
		return "products/new";
	}
	
	/*
	 * Process new product submission
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/products/new")
	public String processNewProduct(HttpServletRequest request, Model model) {
		Map<String, String[]> formParams = request.getParameterMap();
		
		User user = (User) request.getSession(false).getAttribute("user");
				
		// Query microservice to create new product
		Product newProduct = restTemplate.postForObject("http://"
				+ "localhost:8082/{id}/products/new", formParams, Product.class, user.getUserID());
		
		if (newProduct != null) {
			model.addAttribute("product", newProduct);
			return "products/view";
		} else {
			model.addAttribute("error", "couldn't create new product: try again?");
			return "products/new";
		}
	}
	
	/*
	 * Render product profile page 
	 */
	@RequestMapping("/products/{id}")
	public String renderProductProfile(Model model, @PathVariable("id") Integer id) {
		// Query microservice for product 
		Product product = restTemplate.getForObject("http://"
				+ "localhost:8082/products/{id}", Product.class, id);
		
		if (product != null) {
			model.addAttribute("product", product);
			return "products/view";
		} else {
			return "products/index";
		}
	}
	
	/*
	 * Process request for editing a product 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/users/{userID}/products/{productID}/edit")
	public String renderEditProductPage(Model model, @PathVariable("userID") Integer userID, @PathVariable("productID") Integer productID, HttpServletRequest request) {
		User user = (User) request.getSession(false).getAttribute("user");
		
		// Make sure product's owned by logged in user or it's an admin user editing it 
		if (userID.equals(user.getUserID()) || user.isAdmin()) {
			// Query microservice for product with the given id 
			Product product = restTemplate.getForObject("http://"
					+ "localhost:8082/products/{id}", Product.class, productID);
			
			if (product != null) {
				model.addAttribute("product", product);
				return "products/edit";
			}
			else {
				model.addAttribute("error", "Couldn't retrieve product with the given id");
				return "products/index";
			}
		} else {
			return "redirect:/products/index";
		}
	}
	
	/*
	 * Edit a product 
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/users/{userID}/products/{productID}/edit")
	public String editProduct(Model model, @PathVariable("userID") Integer userID, @PathVariable("productID") Integer productID, HttpServletRequest request) {
		User user = (User) request.getSession(false).getAttribute("user");
		
		Map<String, String[]> formParams = request.getParameterMap();
		
		// Make sure the product's owned by logged in user or it's an admin editing it 
		if (userID.equals(user.getUserID()) || user.isAdmin()) {
			Product product = restTemplate.postForObject("http://"
					+ "localhost:8082/products/{id}/edit", formParams, Product.class, productID);
			
			if (product != null) {
				model.addAttribute("product", product);
				return "redirect:/products/" + product.getProductID();
			} else {
				model.addAttribute("error", "Couldn't update the product");
				return "redirect:/users/"+userID+"/product/"+productID+"/edit";
			}
		} else {
			return "redirect:/products/index";
		}
	}
	
	/*
	 * Process request for deleting a product  
	 */
	@RequestMapping("/users/{userID}/products/{productID}/delete")
	public String deleteProduct(Model model, @PathVariable("userID") Integer userID, @PathVariable("productID") Integer productID, HttpServletRequest request) {
		User user = (User) request.getSession(false).getAttribute("user");
		
		// Check product belongs to the user or the user is an admin 
		if (userID.equals(user.getUserID()) || user.isAdmin()) {
			// Send request to microservice to delete product with the given id 
			boolean deleted = restTemplate.postForObject("http://" 
					+ "localhost:8082/products/{id}/delete", null, boolean.class, productID);
			
			if (deleted) {
				return "redirect:/products/index";
			}
			else {
				model.addAttribute("error", "Couldn't delete product");
				return "redirect:/products/" + productID;
			}
		} else {
			return "redirect:/products/index";
		}
	}
}
