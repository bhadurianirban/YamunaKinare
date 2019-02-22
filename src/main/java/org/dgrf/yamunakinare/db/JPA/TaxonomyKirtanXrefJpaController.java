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
import org.dgrf.yamunakinare.db.entities.Articles;
import org.dgrf.yamunakinare.db.entities.Taxonomy;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.dgrf.yamunakinare.db.JPA.exceptions.IllegalOrphanException;
import org.dgrf.yamunakinare.db.JPA.exceptions.NonexistentEntityException;
import org.dgrf.yamunakinare.db.JPA.exceptions.PreexistingEntityException;
import org.dgrf.yamunakinare.db.entities.TaxonomyKirtanXref;
import org.dgrf.yamunakinare.db.entities.TaxonomyKirtanXrefPK;

/**
 *
 * @author bhaduri
 */
public class TaxonomyKirtanXrefJpaController implements Serializable {

    public TaxonomyKirtanXrefJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(TaxonomyKirtanXref taxonomyKirtanXref) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (taxonomyKirtanXref.getTaxonomyKirtanXrefPK() == null) {
            taxonomyKirtanXref.setTaxonomyKirtanXrefPK(new TaxonomyKirtanXrefPK());
        }
        taxonomyKirtanXref.getTaxonomyKirtanXrefPK().setTaxonomyTypesId(taxonomyKirtanXref.getTaxonomy().getTaxonomyPK().getTaxonomyTypesId());
        taxonomyKirtanXref.getTaxonomyKirtanXrefPK().setTaxonomyId(taxonomyKirtanXref.getTaxonomy().getTaxonomyPK().getId());
        taxonomyKirtanXref.getTaxonomyKirtanXrefPK().setArticlesArticleTypeId(taxonomyKirtanXref.getArticles().getArticlesPK().getArticleTypeId());
        taxonomyKirtanXref.getTaxonomyKirtanXrefPK().setArticlesId(taxonomyKirtanXref.getArticles().getArticlesPK().getId());
        List<String> illegalOrphanMessages = null;
        Articles articlesOrphanCheck = taxonomyKirtanXref.getArticles();
        if (articlesOrphanCheck != null) {
            TaxonomyKirtanXref oldTaxonomyKirtanXrefOfArticles = articlesOrphanCheck.getTaxonomyKirtanXref();
            if (oldTaxonomyKirtanXrefOfArticles != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Articles " + articlesOrphanCheck + " already has an item of type TaxonomyKirtanXref whose articles column cannot be null. Please make another selection for the articles field.");
            }
        }
        Taxonomy taxonomyOrphanCheck = taxonomyKirtanXref.getTaxonomy();
        if (taxonomyOrphanCheck != null) {
            TaxonomyKirtanXref oldTaxonomyKirtanXrefOfTaxonomy = taxonomyOrphanCheck.getTaxonomyKirtanXref();
            if (oldTaxonomyKirtanXrefOfTaxonomy != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The Taxonomy " + taxonomyOrphanCheck + " already has an item of type TaxonomyKirtanXref whose taxonomy column cannot be null. Please make another selection for the taxonomy field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articles articles = taxonomyKirtanXref.getArticles();
            if (articles != null) {
                articles = em.getReference(articles.getClass(), articles.getArticlesPK());
                taxonomyKirtanXref.setArticles(articles);
            }
            Taxonomy taxonomy = taxonomyKirtanXref.getTaxonomy();
            if (taxonomy != null) {
                taxonomy = em.getReference(taxonomy.getClass(), taxonomy.getTaxonomyPK());
                taxonomyKirtanXref.setTaxonomy(taxonomy);
            }
            em.persist(taxonomyKirtanXref);
            if (articles != null) {
                articles.setTaxonomyKirtanXref(taxonomyKirtanXref);
                articles = em.merge(articles);
            }
            if (taxonomy != null) {
                taxonomy.setTaxonomyKirtanXref(taxonomyKirtanXref);
                taxonomy = em.merge(taxonomy);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findTaxonomyKirtanXref(taxonomyKirtanXref.getTaxonomyKirtanXrefPK()) != null) {
                throw new PreexistingEntityException("TaxonomyKirtanXref " + taxonomyKirtanXref + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(TaxonomyKirtanXref taxonomyKirtanXref) throws IllegalOrphanException, NonexistentEntityException, Exception {
        taxonomyKirtanXref.getTaxonomyKirtanXrefPK().setTaxonomyTypesId(taxonomyKirtanXref.getTaxonomy().getTaxonomyPK().getTaxonomyTypesId());
        taxonomyKirtanXref.getTaxonomyKirtanXrefPK().setTaxonomyId(taxonomyKirtanXref.getTaxonomy().getTaxonomyPK().getId());
        taxonomyKirtanXref.getTaxonomyKirtanXrefPK().setArticlesArticleTypeId(taxonomyKirtanXref.getArticles().getArticlesPK().getArticleTypeId());
        taxonomyKirtanXref.getTaxonomyKirtanXrefPK().setArticlesId(taxonomyKirtanXref.getArticles().getArticlesPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TaxonomyKirtanXref persistentTaxonomyKirtanXref = em.find(TaxonomyKirtanXref.class, taxonomyKirtanXref.getTaxonomyKirtanXrefPK());
            Articles articlesOld = persistentTaxonomyKirtanXref.getArticles();
            Articles articlesNew = taxonomyKirtanXref.getArticles();
            Taxonomy taxonomyOld = persistentTaxonomyKirtanXref.getTaxonomy();
            Taxonomy taxonomyNew = taxonomyKirtanXref.getTaxonomy();
            List<String> illegalOrphanMessages = null;
            if (articlesNew != null && !articlesNew.equals(articlesOld)) {
                TaxonomyKirtanXref oldTaxonomyKirtanXrefOfArticles = articlesNew.getTaxonomyKirtanXref();
                if (oldTaxonomyKirtanXrefOfArticles != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Articles " + articlesNew + " already has an item of type TaxonomyKirtanXref whose articles column cannot be null. Please make another selection for the articles field.");
                }
            }
            if (taxonomyNew != null && !taxonomyNew.equals(taxonomyOld)) {
                TaxonomyKirtanXref oldTaxonomyKirtanXrefOfTaxonomy = taxonomyNew.getTaxonomyKirtanXref();
                if (oldTaxonomyKirtanXrefOfTaxonomy != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The Taxonomy " + taxonomyNew + " already has an item of type TaxonomyKirtanXref whose taxonomy column cannot be null. Please make another selection for the taxonomy field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (articlesNew != null) {
                articlesNew = em.getReference(articlesNew.getClass(), articlesNew.getArticlesPK());
                taxonomyKirtanXref.setArticles(articlesNew);
            }
            if (taxonomyNew != null) {
                taxonomyNew = em.getReference(taxonomyNew.getClass(), taxonomyNew.getTaxonomyPK());
                taxonomyKirtanXref.setTaxonomy(taxonomyNew);
            }
            taxonomyKirtanXref = em.merge(taxonomyKirtanXref);
            if (articlesOld != null && !articlesOld.equals(articlesNew)) {
                articlesOld.setTaxonomyKirtanXref(null);
                articlesOld = em.merge(articlesOld);
            }
            if (articlesNew != null && !articlesNew.equals(articlesOld)) {
                articlesNew.setTaxonomyKirtanXref(taxonomyKirtanXref);
                articlesNew = em.merge(articlesNew);
            }
            if (taxonomyOld != null && !taxonomyOld.equals(taxonomyNew)) {
                taxonomyOld.setTaxonomyKirtanXref(null);
                taxonomyOld = em.merge(taxonomyOld);
            }
            if (taxonomyNew != null && !taxonomyNew.equals(taxonomyOld)) {
                taxonomyNew.setTaxonomyKirtanXref(taxonomyKirtanXref);
                taxonomyNew = em.merge(taxonomyNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                TaxonomyKirtanXrefPK id = taxonomyKirtanXref.getTaxonomyKirtanXrefPK();
                if (findTaxonomyKirtanXref(id) == null) {
                    throw new NonexistentEntityException("The taxonomyKirtanXref with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(TaxonomyKirtanXrefPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TaxonomyKirtanXref taxonomyKirtanXref;
            try {
                taxonomyKirtanXref = em.getReference(TaxonomyKirtanXref.class, id);
                taxonomyKirtanXref.getTaxonomyKirtanXrefPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The taxonomyKirtanXref with id " + id + " no longer exists.", enfe);
            }
            Articles articles = taxonomyKirtanXref.getArticles();
            if (articles != null) {
                articles.setTaxonomyKirtanXref(null);
                articles = em.merge(articles);
            }
            Taxonomy taxonomy = taxonomyKirtanXref.getTaxonomy();
            if (taxonomy != null) {
                taxonomy.setTaxonomyKirtanXref(null);
                taxonomy = em.merge(taxonomy);
            }
            em.remove(taxonomyKirtanXref);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<TaxonomyKirtanXref> findTaxonomyKirtanXrefEntities() {
        return findTaxonomyKirtanXrefEntities(true, -1, -1);
    }

    public List<TaxonomyKirtanXref> findTaxonomyKirtanXrefEntities(int maxResults, int firstResult) {
        return findTaxonomyKirtanXrefEntities(false, maxResults, firstResult);
    }

    private List<TaxonomyKirtanXref> findTaxonomyKirtanXrefEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(TaxonomyKirtanXref.class));
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

    public TaxonomyKirtanXref findTaxonomyKirtanXref(TaxonomyKirtanXrefPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(TaxonomyKirtanXref.class, id);
        } finally {
            em.close();
        }
    }

    public int getTaxonomyKirtanXrefCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<TaxonomyKirtanXref> rt = cq.from(TaxonomyKirtanXref.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
