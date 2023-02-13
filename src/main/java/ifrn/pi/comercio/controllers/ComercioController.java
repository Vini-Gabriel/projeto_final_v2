package ifrn.pi.comercio.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ifrn.pi.comercio.models.Produto;
import ifrn.pi.comercio.models.Usuario;
import ifrn.pi.comercio.models.Venda;
import ifrn.pi.comercio.repositories.ProdutoRepository;
import ifrn.pi.comercio.repositories.UsuarioRepository;
import ifrn.pi.comercio.repositories.VendaRepository;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/comercio")
public class ComercioController {
	
	@Autowired
	private UsuarioRepository ur;
	@Autowired
	private VendaRepository vr;
	@Autowired
	private ProdutoRepository pr;

	@GetMapping("/logar")
	public ModelAndView logar() {
		ModelAndView md= new ModelAndView();
		Usuario us = new Usuario();
		md.addObject(us);
		md.setViewName("login");
		return md;
	}
	@GetMapping("/cadastrarAdm")
	public String cadastrarAdm() {
		return "adicionarAdm";
	}
	
	@GetMapping("/cadastrarUser")
	public String cadastrarUser() {
		return "adicionarUser";
	}
	
	@GetMapping("/form")
	public String form(Venda venda) {
		return "comercio/formVenda";
	}
	
	
	
	@PostMapping("/salvarUsuarios")
	public String salvarUsuarios(Usuario usuario) {

		List<Usuario> usuarios = ur.findAll();

		for (Usuario us : usuarios) {
			if (us.getEmail().equals(usuario.getEmail())) {
				System.out.println("Este email já foi cadastrado");
				return "lista";
			}
		}
		usuario.setTipo("U");
		if (usuarios.isEmpty()) {
			usuario.setTipo("A");
		}
		ur.save(usuario);
		System.out.println("ADM cadastrado com sucesso");
		return "redirect:/comercio/logar";
	}

	@PostMapping("/salvarFuncionario")
	public String salvarFuncionario(Usuario usuario) {

		List<Usuario> usuarios = ur.findAll();

		for (Usuario us : usuarios) {
			if (us.getEmail().equals(usuario.getEmail())) {
				System.out.println("Este email já foi cadastrado");
				return cadastrarUser();
			}
		}
		usuario.setTipo("F");
		ur.save(usuario);
		System.out.println("Usuario cadastrado com sucesso");
		return "redirect:/comercio/produtos";
	}

	@PostMapping("/entrar")
	public String entrar(String email, String senha) {
		System.out.println(email);
		System.out.println(senha);
		Usuario us = ur.findByEmail(email);
		if (us == null) {
			System.out.println("O email inserido está incorreto");
			return "redirect:/comercio/logar";
		}
		if (us.getSenha().equals(senha)) {
			System.out.println("A senha está correta");
			if (us.getTipo().equals("A")) {
				return "redirect:/comercio/produtos";
			} else if (us.getTipo().equals("F")) {
				return "redirect:/comercio/detalhes";
			}
			return "redirect:/comercio/lista";
		}
		System.out.println("A senha inserida está incorreta");
		return "redirect:/comercio/logar";
	}
	
	
	
	
	
	@PostMapping
	public String salvar(@Valid Venda venda, BindingResult result, RedirectAttributes attributes) {
		
		if(result.hasErrors()) {
			return form(venda);
		}
		
		System.out.println(venda);
		vr.save(venda);
		attributes.addFlashAttribute("mensagem", "Venda efetuada com sucesso!");
		
		return "redirect:/comercio";
	}
	
	@GetMapping
	public ModelAndView listar() {
		List<Venda> vendas = vr.findAll();
		ModelAndView mv = new ModelAndView("comercio/lista");
		mv.addObject("vendas", vendas);
		return mv;
	}
	
	@GetMapping("/{id}")
	public ModelAndView detalhar(@PathVariable Long id, Produto produto) {
		ModelAndView md = new ModelAndView();
		Optional<Venda> opt = vr.findById(id);
		if(opt.isEmpty()) {
			md.setViewName("redirect:/comercio");
			return md;
		}
		md.setViewName("comercio/detalhes");
		Venda venda = opt.get();
		md.addObject("venda", venda);
		
		List<Produto> produtos = pr.findByVenda(venda);
		md.addObject("produtos", produtos);
		
		return md;
	}
	
	@PostMapping("/{idVenda}")
	public String salvarProduto(@PathVariable Long idVenda, Produto produto) {
		
		System.out.println("Id da venda: " + idVenda);
		System.out.println(produto);
		
		Optional<Venda> opt = vr.findById(idVenda);
		if(opt.isEmpty()) {
			return "redirect:/comercio";
		}
		
		Venda venda = opt.get();
		produto.setVenda(venda);
		
		pr.save(produto);
		
		return "redirect:/comercio/{idVenda}";
	}
	
	@GetMapping("/{id}/selecionar")
	public ModelAndView selecionarVenda(@PathVariable Long id) {
		ModelAndView md = new ModelAndView(); 
		Optional<Venda> opt = vr.findById(id);
		if(opt.isEmpty()) {
			md.setViewName("redirect:/comercio");
			return md;
		}
		
		Venda venda = opt.get();
		md.setViewName("comercio/formVenda");
		md.addObject("venda", venda);
		
		return md;
	}
	
	@GetMapping("/{idVenda}/produtos/{idProduto}/selecionar")
	public ModelAndView selecionarProduto(@PathVariable Long idVenda, @PathVariable Long idProduto) {
		ModelAndView md = new ModelAndView();
		
		Optional<Venda> optVenda = vr.findById(idVenda);
		Optional<Produto> optProduto = pr.findById(idProduto);
		
		if(optVenda.isEmpty() || optProduto.isEmpty()) {
			md.setViewName("redirect:/comercio");
			return md;
		}
		
		Venda venda = optVenda.get();
		Produto produto = optProduto.get();
		
		if(venda.getId() != produto.getVenda().getId()) {
			md.setViewName("redirect:/comercio");
			return md;
		}
		
		md.setViewName("comercio/detalhes");
		md.addObject("produto", produto);
		md.addObject("venda", venda);
		md.addObject("produtos", pr.findByVenda(venda));
		
		return md;
	}
	
	@GetMapping("/{id}/remover")
	public String apagarVenda(@PathVariable Long id, RedirectAttributes attributes) {
		
		Optional<Venda> opt = vr.findById(id);
		
		if(!opt.isEmpty()) {
			Venda venda = opt.get();
			
			List<Produto> produtos = pr.findByVenda(venda);
			
			pr.deleteAll(produtos);
			vr.delete(venda);
			attributes.addFlashAttribute("mensagem", "Venda removido com sucesso!");
		}
		return "redirect:/comercio";
	}
	
	@GetMapping("/{idVenda}/produtos/{idProduto}/remover")
	public String apagarProduto(@PathVariable Long idVenda, @PathVariable Long idProduto) {
		
		Optional<Produto> opt = pr.findById(idProduto);
		
		if(!opt.isEmpty()) {
			Produto produto = opt.get();
			pr.delete(produto);
		}
		return "redirect:/comercio/{idVenda}";
	}
}
