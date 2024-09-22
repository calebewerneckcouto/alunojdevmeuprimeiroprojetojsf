package br.com.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import br.com.jpautil.JPAUtil;

public class DAOGeneric <O>{

	
	public void salvar(O objeto) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		entityManager.persist(objeto);
		
		transaction.commit();
		entityManager.close();
	}
	
	public O merge(O objeto) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		O retorno = entityManager.merge(objeto);
		
		transaction.commit();
		entityManager.close();
		
		return retorno;
	}
	
	public void delete(O objeto) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		entityManager.remove(objeto);
		
		transaction.commit();
		entityManager.close();
	}
	
	public void deletePorId(O objeto) {
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		Object id = JPAUtil.getPrimaryKey(objeto);
		entityManager.createQuery("delete from " + objeto.getClass().getName() + " where id = "+ id).executeUpdate();
		
		transaction.commit();
		entityManager.close();
	}
	
	public List<O> getListGeneric(Class<O> objeto){
		EntityManager entityManager = JPAUtil.getEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		
		List<O> lista = entityManager.createQuery("from " + objeto.getName()).getResultList();
		
		transaction.commit();
		entityManager.close();
		
		return lista;
	}
	
	
}
