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
import org.dgrf.yamunakinare.db.JPA.exceptions.IllegalOrphanException;
import org.dgrf.yamunakinare.db.JPA.exceptions.NonexistentEntityException;
import org.dgrf.yamunakinare.db.JPA.exceptions.PreexistingEntityException;
import org.dgrf.yamunakinare.db.entities.TaxonomyTypes;

/**
 *
 * @author dgrfi
 */
public class TaxonomyTypesJpaController implements Serializable {

    public TaxonomyTypesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TaxonomyTypes taxonomyTypes) throws PreexistingEntityException, Exception {
        if (taxonomyTypes.getTaxonomyList() == null) {
            taxonomyTypes.setTaxonomyList(new ArrayList<Taxonomy>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Taxonomy> attachedTaxonomyList = new ArrayList<Taxonomy>();
            for (Taxonomy taxonomyListTaxonomyToAttach : taxonomyTypes.getTaxonomyList()) {
                taxonomyListTaxonomyToAttach = em.getReference(taxonomyListTaxonomyToAttach.getClass(), taxonomyListTaxonomyToAttach.getTaxonomyPK());
                attachedTaxonomyList.add(taxonomyListTaxonomyToAttach);
            }
            taxonomyTypes.setTaxonomyList(attachedTaxonomyList);
            em.persist(taxonomyTypes);
            for (Taxonomy taxonomyListTaxonomy : taxonomyTypes.getTaxonomyList()) {
                TaxonomyTypes oldTaxonomyTypesOfTaxonomyListTaxonomy = taxonomyListTaxonomy.getTaxonomyTypes();
                taxonomyListTaxonomy.setTaxonomyTypes(taxonomyTypes);
                taxonomyListTaxonomy = em.merge(taxonomyListTaxonomy);
                if (oldTaxonomyTypesOfTaxonomyListTaxonomy != null) {
                    oldTaxonomyTypesOfTaxonomyListTaxonomy.getTaxonomyList().remove(taxonomyListTaxonomy);
                    oldTaxonomyTypesOfTaxonomyListTaxonomy = em.merge(oldTaxonomyTypesOfTaxonomyListTaxonomy);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTaxonomyTypes(taxonomyTypes.getId()) != null) {
                throw new PreexistingEntityException("TaxonomyTypes " + taxonomyTypes + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TaxonomyTypes taxonomyTypes) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TaxonomyTypes persistentTaxonomyTypes = em.find(TaxonomyTypes.class, taxonomyTypes.getId());
            List<Taxonomy> taxonomyListOld = persistentTaxonomyTypes.getTaxonomyList();
            List<Taxonomy> taxonomyListNew = taxonomyTypes.getTaxonomyList();
            List<String> illegalOrphanMessages = null;
            for (Taxonomy taxonomyListOldTaxonomy : taxonomyListOld) {
                if (!taxonomyListNew.contains(taxonomyListOldTaxonomy)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Taxonomy " + taxonomyListOldTaxonomy + " since its taxonomyTypes field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Taxonomy> attachedTaxonomyListNew = new ArrayList<Taxonomy>();
            for (Taxonomy taxonomyListNewTaxonomyToAttach : taxonomyListNew) {
                taxonomyListNewTaxonomyToAttach = em.getReference(taxonomyListNewTaxonomyToAttach.getClass(), taxonomyListNewTaxonomyToAttach.getTaxonomyPK());
                attachedTaxonomyListNew.add(taxonomyListNewTaxonomyToAttach);
            }
            taxonomyListNew = attachedTaxonomyListNew;
            taxonomyTypes.setTaxonomyList(taxonomyListNew);
            taxonomyTypes = em.merge(taxonomyTypes);
            for (Taxonomy taxonomyListNewTaxonomy : taxonomyListNew) {
                if (!taxonomyListOld.contains(taxonomyListNewTaxonomy)) {
                    TaxonomyTypes oldTaxonomyTypesOfTaxonomyListNewTaxonomy = taxonomyListNewTaxonomy.getTaxonomyTypes();
                    taxonomyListNewTaxonomy.setTaxonomyTypes(taxonomyTypes);
                    taxonomyListNewTaxonomy = em.merge(taxonomyListNewTaxonomy);
                    if (oldTaxonomyTypesOfTaxonomyListNewTaxonomy != null && !oldTaxonomyTypesOfTaxonomyListNewTaxonomy.equals(taxonomyTypes)) {
                        oldTaxonomyTypesOfTaxonomyListNewTaxonomy.getTaxonomyList().remove(taxonomyListNewTaxonomy);
                        oldTaxonomyTypesOfTaxonomyListNewTaxonomy = em.merge(oldTaxonomyTypesOfTaxonomyListNewTaxonomy);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = taxonomyTypes.getId();
                if (findTaxonomyTypes(id) == null) {
                    throw new NonexistentEntityException("The taxonomyTypes with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TaxonomyTypes taxonomyTypes;
            try {
                taxonomyTypes = em.getReference(TaxonomyTypes.class, id);
                taxonomyTypes.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The taxonomyTypes with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Taxonomy> taxonomyListOrphanCheck = taxonomyTypes.getTaxonomyList();
            for (Taxonomy taxonomyListOrphanCheckTaxonomy : taxonomyListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TaxonomyTypes (" + taxonomyTypes + ") cannot be destroyed since the Taxonomy " + taxonomyListOrphanCheckTaxonomy + " in its taxonomyList field has a non-nullable taxonomyTypes field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(taxonomyTypes);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TaxonomyTypes> findTaxonomyTypesEntities() {
        return findTaxonomyTypesEntities(true, -1, -1);
    }

    public List<TaxonomyTypes> findTaxonomyTypesEntities(int maxResults, int firstResult) {
        return findTaxonomyTypesEntities(false, maxResults, firstResult);
    }

    private List<TaxonomyTypes> findTaxonomyTypesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TaxonomyTypes.class));
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

    public TaxonomyTypes findTaxonomyTypes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TaxonomyTypes.class, id);
        } finally {
            em.close();
        }
    }

    public int getTaxonomyTypesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TaxonomyTypes> rt = cq.from(TaxonomyTypes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
