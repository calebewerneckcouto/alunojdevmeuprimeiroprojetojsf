package br.com.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.entidades.Lancamento;
import br.com.jpautil.JPAUtil;

public class IDaoLancamentoImpl implements IDaoLancamento {

	@Override
	public List<Lancamento> consultar(Long codUser) {
		List<Lancamento> lista = null;
		
		EntityManager manager = JPAUtil.getEntityManager();
		EntityTransaction transaction = manager.getTransaction();
		transaction.begin();
		
		lista = manager.createQuery(" from Lancamento where usuario.id = " + codUser).getResultList();
		
		transaction.commit();
		manager.close();
		
		return lista;
	}

}
