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
 * @author bhaduri
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
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Taxonomy taxonomy = taxonomyTypes.getTaxonomy();
            if (taxonomy != null) {
                taxonomy = em.getReference(taxonomy.getClass(), taxonomy.getTaxonomyPK());
                taxonomyTypes.setTaxonomy(taxonomy);
            }
            em.persist(taxonomyTypes);
            if (taxonomy != null) {
                TaxonomyTypes oldTaxonomyTypesOfTaxonomy = taxonomy.getTaxonomyTypes();
                if (oldTaxonomyTypesOfTaxonomy != null) {
                    oldTaxonomyTypesOfTaxonomy.setTaxonomy(null);
                    oldTaxonomyTypesOfTaxonomy = em.merge(oldTaxonomyTypesOfTaxonomy);
                }
                taxonomy.setTaxonomyTypes(taxonomyTypes);
                taxonomy = em.merge(taxonomy);
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
            Taxonomy taxonomyOld = persistentTaxonomyTypes.getTaxonomy();
            Taxonomy taxonomyNew = taxonomyTypes.getTaxonomy();
            List<String> illegalOrphanMessages = null;
            if (taxonomyOld != null && !taxonomyOld.equals(taxonomyNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Taxonomy " + taxonomyOld + " since its taxonomyTypes field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (taxonomyNew != null) {
                taxonomyNew = em.getReference(taxonomyNew.getClass(), taxonomyNew.getTaxonomyPK());
                taxonomyTypes.setTaxonomy(taxonomyNew);
            }
            taxonomyTypes = em.merge(taxonomyTypes);
            if (taxonomyNew != null && !taxonomyNew.equals(taxonomyOld)) {
                TaxonomyTypes oldTaxonomyTypesOfTaxonomy = taxonomyNew.getTaxonomyTypes();
                if (oldTaxonomyTypesOfTaxonomy != null) {
                    oldTaxonomyTypesOfTaxonomy.setTaxonomy(null);
                    oldTaxonomyTypesOfTaxonomy = em.merge(oldTaxonomyTypesOfTaxonomy);
                }
                taxonomyNew.setTaxonomyTypes(taxonomyTypes);
                taxonomyNew = em.merge(taxonomyNew);
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
            Taxonomy taxonomyOrphanCheck = taxonomyTypes.getTaxonomy();
            if (taxonomyOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This TaxonomyTypes (" + taxonomyTypes + ") cannot be destroyed since the Taxonomy " + taxonomyOrphanCheck + " in its taxonomy field has a non-nullable taxonomyTypes field.");
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
