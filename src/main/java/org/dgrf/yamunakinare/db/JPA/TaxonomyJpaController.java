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
import org.dgrf.yamunakinare.db.entities.TaxonomyTypes;
import org.dgrf.yamunakinare.db.entities.TaxonomyKirtanXref;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.dgrf.yamunakinare.db.JPA.exceptions.IllegalOrphanException;
import org.dgrf.yamunakinare.db.JPA.exceptions.NonexistentEntityException;
import org.dgrf.yamunakinare.db.JPA.exceptions.PreexistingEntityException;
import org.dgrf.yamunakinare.db.entities.Taxonomy;
import org.dgrf.yamunakinare.db.entities.TaxonomyPK;

/**
 *
 * @author bhaduri
 */
public class TaxonomyJpaController implements Serializable {

    public TaxonomyJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Taxonomy taxonomy) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (taxonomy.getTaxonomyPK() == null) {
            taxonomy.setTaxonomyPK(new TaxonomyPK());
        }
        taxonomy.getTaxonomyPK().setTaxonomyTypesId(taxonomy.getTaxonomyTypes().getId());
        List<String> illegalOrphanMessages = null;
        TaxonomyTypes taxonomyTypesOrphanCheck = taxonomy.getTaxonomyTypes();
        if (taxonomyTypesOrphanCheck != null) {
            Taxonomy oldTaxonomyOfTaxonomyTypes = taxonomyTypesOrphanCheck.getTaxonomy();
            if (oldTaxonomyOfTaxonomyTypes != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The TaxonomyTypes " + taxonomyTypesOrphanCheck + " already has an item of type Taxonomy whose taxonomyTypes column cannot be null. Please make another selection for the taxonomyTypes field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TaxonomyTypes taxonomyTypes = taxonomy.getTaxonomyTypes();
            if (taxonomyTypes != null) {
                taxonomyTypes = em.getReference(taxonomyTypes.getClass(), taxonomyTypes.getId());
                taxonomy.setTaxonomyTypes(taxonomyTypes);
            }
            TaxonomyKirtanXref taxonomyKirtanXref = taxonomy.getTaxonomyKirtanXref();
            if (taxonomyKirtanXref != null) {
                taxonomyKirtanXref = em.getReference(taxonomyKirtanXref.getClass(), taxonomyKirtanXref.getTaxonomyKirtanXrefPK());
                taxonomy.setTaxonomyKirtanXref(taxonomyKirtanXref);
            }
            em.persist(taxonomy);
            if (taxonomyTypes != null) {
                taxonomyTypes.setTaxonomy(taxonomy);
                taxonomyTypes = em.merge(taxonomyTypes);
            }
            if (taxonomyKirtanXref != null) {
                Taxonomy oldTaxonomyOfTaxonomyKirtanXref = taxonomyKirtanXref.getTaxonomy();
                if (oldTaxonomyOfTaxonomyKirtanXref != null) {
                    oldTaxonomyOfTaxonomyKirtanXref.setTaxonomyKirtanXref(null);
                    oldTaxonomyOfTaxonomyKirtanXref = em.merge(oldTaxonomyOfTaxonomyKirtanXref);
                }
                taxonomyKirtanXref.setTaxonomy(taxonomy);
                taxonomyKirtanXref = em.merge(taxonomyKirtanXref);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTaxonomy(taxonomy.getTaxonomyPK()) != null) {
                throw new PreexistingEntityException("Taxonomy " + taxonomy + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Taxonomy taxonomy) throws IllegalOrphanException, NonexistentEntityException, Exception {
        taxonomy.getTaxonomyPK().setTaxonomyTypesId(taxonomy.getTaxonomyTypes().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Taxonomy persistentTaxonomy = em.find(Taxonomy.class, taxonomy.getTaxonomyPK());
            TaxonomyTypes taxonomyTypesOld = persistentTaxonomy.getTaxonomyTypes();
            TaxonomyTypes taxonomyTypesNew = taxonomy.getTaxonomyTypes();
            TaxonomyKirtanXref taxonomyKirtanXrefOld = persistentTaxonomy.getTaxonomyKirtanXref();
            TaxonomyKirtanXref taxonomyKirtanXrefNew = taxonomy.getTaxonomyKirtanXref();
            List<String> illegalOrphanMessages = null;
            if (taxonomyTypesNew != null && !taxonomyTypesNew.equals(taxonomyTypesOld)) {
                Taxonomy oldTaxonomyOfTaxonomyTypes = taxonomyTypesNew.getTaxonomy();
                if (oldTaxonomyOfTaxonomyTypes != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The TaxonomyTypes " + taxonomyTypesNew + " already has an item of type Taxonomy whose taxonomyTypes column cannot be null. Please make another selection for the taxonomyTypes field.");
                }
            }
            if (taxonomyKirtanXrefOld != null && !taxonomyKirtanXrefOld.equals(taxonomyKirtanXrefNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain TaxonomyKirtanXref " + taxonomyKirtanXrefOld + " since its taxonomy field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (taxonomyTypesNew != null) {
                taxonomyTypesNew = em.getReference(taxonomyTypesNew.getClass(), taxonomyTypesNew.getId());
                taxonomy.setTaxonomyTypes(taxonomyTypesNew);
            }
            if (taxonomyKirtanXrefNew != null) {
                taxonomyKirtanXrefNew = em.getReference(taxonomyKirtanXrefNew.getClass(), taxonomyKirtanXrefNew.getTaxonomyKirtanXrefPK());
                taxonomy.setTaxonomyKirtanXref(taxonomyKirtanXrefNew);
            }
            taxonomy = em.merge(taxonomy);
            if (taxonomyTypesOld != null && !taxonomyTypesOld.equals(taxonomyTypesNew)) {
                taxonomyTypesOld.setTaxonomy(null);
                taxonomyTypesOld = em.merge(taxonomyTypesOld);
            }
            if (taxonomyTypesNew != null && !taxonomyTypesNew.equals(taxonomyTypesOld)) {
                taxonomyTypesNew.setTaxonomy(taxonomy);
                taxonomyTypesNew = em.merge(taxonomyTypesNew);
            }
            if (taxonomyKirtanXrefNew != null && !taxonomyKirtanXrefNew.equals(taxonomyKirtanXrefOld)) {
                Taxonomy oldTaxonomyOfTaxonomyKirtanXref = taxonomyKirtanXrefNew.getTaxonomy();
                if (oldTaxonomyOfTaxonomyKirtanXref != null) {
                    oldTaxonomyOfTaxonomyKirtanXref.setTaxonomyKirtanXref(null);
                    oldTaxonomyOfTaxonomyKirtanXref = em.merge(oldTaxonomyOfTaxonomyKirtanXref);
                }
                taxonomyKirtanXrefNew.setTaxonomy(taxonomy);
                taxonomyKirtanXrefNew = em.merge(taxonomyKirtanXrefNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TaxonomyPK id = taxonomy.getTaxonomyPK();
                if (findTaxonomy(id) == null) {
                    throw new NonexistentEntityException("The taxonomy with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(TaxonomyPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Taxonomy taxonomy;
            try {
                taxonomy = em.getReference(Taxonomy.class, id);
                taxonomy.getTaxonomyPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The taxonomy with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            TaxonomyKirtanXref taxonomyKirtanXrefOrphanCheck = taxonomy.getTaxonomyKirtanXref();
            if (taxonomyKirtanXrefOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Taxonomy (" + taxonomy + ") cannot be destroyed since the TaxonomyKirtanXref " + taxonomyKirtanXrefOrphanCheck + " in its taxonomyKirtanXref field has a non-nullable taxonomy field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            TaxonomyTypes taxonomyTypes = taxonomy.getTaxonomyTypes();
            if (taxonomyTypes != null) {
                taxonomyTypes.setTaxonomy(null);
                taxonomyTypes = em.merge(taxonomyTypes);
            }
            em.remove(taxonomy);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Taxonomy> findTaxonomyEntities() {
        return findTaxonomyEntities(true, -1, -1);
    }

    public List<Taxonomy> findTaxonomyEntities(int maxResults, int firstResult) {
        return findTaxonomyEntities(false, maxResults, firstResult);
    }

    private List<Taxonomy> findTaxonomyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Taxonomy.class));
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

    public Taxonomy findTaxonomy(TaxonomyPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Taxonomy.class, id);
        } finally {
            em.close();
        }
    }

    public int getTaxonomyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Taxonomy> rt = cq.from(Taxonomy.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
