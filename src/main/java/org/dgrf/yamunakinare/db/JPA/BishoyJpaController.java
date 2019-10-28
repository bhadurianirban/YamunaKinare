/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.db.JPA;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.dgrf.yamunakinare.db.entities.Taxonomy;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.dgrf.yamunakinare.db.JPA.exceptions.NonexistentEntityException;
import org.dgrf.yamunakinare.db.JPA.exceptions.PreexistingEntityException;
import org.dgrf.yamunakinare.db.entities.Bishoy;

/**
 *
 * @author dgrfi
 */
public class BishoyJpaController implements Serializable {

    public BishoyJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Bishoy bishoy) throws PreexistingEntityException, Exception {
        if (bishoy.getTaxonomyList() == null) {
            bishoy.setTaxonomyList(new ArrayList<Taxonomy>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Taxonomy> attachedTaxonomyList = new ArrayList<Taxonomy>();
            for (Taxonomy taxonomyListTaxonomyToAttach : bishoy.getTaxonomyList()) {
                taxonomyListTaxonomyToAttach = em.getReference(taxonomyListTaxonomyToAttach.getClass(), taxonomyListTaxonomyToAttach.getTaxonomyPK());
                attachedTaxonomyList.add(taxonomyListTaxonomyToAttach);
            }
            bishoy.setTaxonomyList(attachedTaxonomyList);
            em.persist(bishoy);
            for (Taxonomy taxonomyListTaxonomy : bishoy.getTaxonomyList()) {
                taxonomyListTaxonomy.getBishoyList().add(bishoy);
                taxonomyListTaxonomy = em.merge(taxonomyListTaxonomy);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findBishoy(bishoy.getId()) != null) {
                throw new PreexistingEntityException("Bishoy " + bishoy + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Bishoy bishoy) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Bishoy persistentBishoy = em.find(Bishoy.class, bishoy.getId());
            List<Taxonomy> taxonomyListOld = persistentBishoy.getTaxonomyList();
            List<Taxonomy> taxonomyListNew = bishoy.getTaxonomyList();
            List<Taxonomy> attachedTaxonomyListNew = new ArrayList<Taxonomy>();
            for (Taxonomy taxonomyListNewTaxonomyToAttach : taxonomyListNew) {
                taxonomyListNewTaxonomyToAttach = em.getReference(taxonomyListNewTaxonomyToAttach.getClass(), taxonomyListNewTaxonomyToAttach.getTaxonomyPK());
                attachedTaxonomyListNew.add(taxonomyListNewTaxonomyToAttach);
            }
            taxonomyListNew = attachedTaxonomyListNew;
            bishoy.setTaxonomyList(taxonomyListNew);
            bishoy = em.merge(bishoy);
            for (Taxonomy taxonomyListOldTaxonomy : taxonomyListOld) {
                if (!taxonomyListNew.contains(taxonomyListOldTaxonomy)) {
                    taxonomyListOldTaxonomy.getBishoyList().remove(bishoy);
                    taxonomyListOldTaxonomy = em.merge(taxonomyListOldTaxonomy);
                }
            }
            for (Taxonomy taxonomyListNewTaxonomy : taxonomyListNew) {
                if (!taxonomyListOld.contains(taxonomyListNewTaxonomy)) {
                    taxonomyListNewTaxonomy.getBishoyList().add(bishoy);
                    taxonomyListNewTaxonomy = em.merge(taxonomyListNewTaxonomy);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = bishoy.getId();
                if (findBishoy(id) == null) {
                    throw new NonexistentEntityException("The bishoy with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Bishoy bishoy;
            try {
                bishoy = em.getReference(Bishoy.class, id);
                bishoy.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The bishoy with id " + id + " no longer exists.", enfe);
            }
            List<Taxonomy> taxonomyList = bishoy.getTaxonomyList();
            for (Taxonomy taxonomyListTaxonomy : taxonomyList) {
                taxonomyListTaxonomy.getBishoyList().remove(bishoy);
                taxonomyListTaxonomy = em.merge(taxonomyListTaxonomy);
            }
            em.remove(bishoy);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Bishoy> findBishoyEntities() {
        return findBishoyEntities(true, -1, -1);
    }

    public List<Bishoy> findBishoyEntities(int maxResults, int firstResult) {
        return findBishoyEntities(false, maxResults, firstResult);
    }

    private List<Bishoy> findBishoyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Bishoy.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Bishoy findBishoy(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Bishoy.class, id);
        } finally {
            em.close();
        }
    }

    public int getBishoyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Bishoy> rt = cq.from(Bishoy.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
