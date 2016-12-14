package bltn.uc3m.tiw.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
			return "success";
		} else {
			model.addAttribute("error", "couldn't create new product: try again?");
			return "index";
		}
	}
}
