package ifrn.pi.comercio.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ifrn.pi.comercio.models.Produto;
import ifrn.pi.comercio.models.Venda;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{

	List<Produto> findByVenda(Venda venda);
	
}
