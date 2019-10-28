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
import org.dgrf.yamunakinare.db.entities.Kirtan;

/**
 *
 * @author dgrfi
 */
public class KirtanJpaController implements Serializable {

    public KirtanJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Kirtan kirtan) throws PreexistingEntityException, Exception {
        if (kirtan.getTaxonomyList() == null) {
            kirtan.setTaxonomyList(new ArrayList<Taxonomy>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Taxonomy> attachedTaxonomyList = new ArrayList<Taxonomy>();
            for (Taxonomy taxonomyListTaxonomyToAttach : kirtan.getTaxonomyList()) {
                taxonomyListTaxonomyToAttach = em.getReference(taxonomyListTaxonomyToAttach.getClass(), taxonomyListTaxonomyToAttach.getTaxonomyPK());
                attachedTaxonomyList.add(taxonomyListTaxonomyToAttach);
            }
            kirtan.setTaxonomyList(attachedTaxonomyList);
            em.persist(kirtan);
            for (Taxonomy taxonomyListTaxonomy : kirtan.getTaxonomyList()) {
                taxonomyListTaxonomy.getKirtanList().add(kirtan);
                taxonomyListTaxonomy = em.merge(taxonomyListTaxonomy);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findKirtan(kirtan.getId()) != null) {
                throw new PreexistingEntityException("Kirtan " + kirtan + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Kirtan kirtan) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Kirtan persistentKirtan = em.find(Kirtan.class, kirtan.getId());
            List<Taxonomy> taxonomyListOld = persistentKirtan.getTaxonomyList();
            List<Taxonomy> taxonomyListNew = kirtan.getTaxonomyList();
            List<Taxonomy> attachedTaxonomyListNew = new ArrayList<Taxonomy>();
            for (Taxonomy taxonomyListNewTaxonomyToAttach : taxonomyListNew) {
                taxonomyListNewTaxonomyToAttach = em.getReference(taxonomyListNewTaxonomyToAttach.getClass(), taxonomyListNewTaxonomyToAttach.getTaxonomyPK());
                attachedTaxonomyListNew.add(taxonomyListNewTaxonomyToAttach);
            }
            taxonomyListNew = attachedTaxonomyListNew;
            kirtan.setTaxonomyList(taxonomyListNew);
            kirtan = em.merge(kirtan);
            for (Taxonomy taxonomyListOldTaxonomy : taxonomyListOld) {
                if (!taxonomyListNew.contains(taxonomyListOldTaxonomy)) {
                    taxonomyListOldTaxonomy.getKirtanList().remove(kirtan);
                    taxonomyListOldTaxonomy = em.merge(taxonomyListOldTaxonomy);
                }
            }
            for (Taxonomy taxonomyListNewTaxonomy : taxonomyListNew) {
                if (!taxonomyListOld.contains(taxonomyListNewTaxonomy)) {
                    taxonomyListNewTaxonomy.getKirtanList().add(kirtan);
                    taxonomyListNewTaxonomy = em.merge(taxonomyListNewTaxonomy);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = kirtan.getId();
                if (findKirtan(id) == null) {
                    throw new NonexistentEntityException("The kirtan with id " + id + " no longer exists.");
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
            Kirtan kirtan;
            try {
                kirtan = em.getReference(Kirtan.class, id);
                kirtan.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The kirtan with id " + id + " no longer exists.", enfe);
            }
            List<Taxonomy> taxonomyList = kirtan.getTaxonomyList();
            for (Taxonomy taxonomyListTaxonomy : taxonomyList) {
                taxonomyListTaxonomy.getKirtanList().remove(kirtan);
                taxonomyListTaxonomy = em.merge(taxonomyListTaxonomy);
            }
            em.remove(kirtan);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Kirtan> findKirtanEntities() {
        return findKirtanEntities(true, -1, -1);
    }

    public List<Kirtan> findKirtanEntities(int maxResults, int firstResult) {
        return findKirtanEntities(false, maxResults, firstResult);
    }

    private List<Kirtan> findKirtanEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Kirtan.class));
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

    public Kirtan findKirtan(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Kirtan.class, id);
        } finally {
            em.close();
        }
    }

    public int getKirtanCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Kirtan> rt = cq.from(Kirtan.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
