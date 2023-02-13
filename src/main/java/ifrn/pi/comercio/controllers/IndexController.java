package ifrn.pi.comercio.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	
	@RequestMapping("/")
	public String index() {
		System.out.println("Chamou o método index");
		return "redirect:/comercio/logar";
	}
}
