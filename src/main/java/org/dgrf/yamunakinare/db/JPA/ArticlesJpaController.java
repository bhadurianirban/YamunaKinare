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
import org.dgrf.yamunakinare.db.entities.TaxonomyKirtanXref;
import org.dgrf.yamunakinare.db.entities.ArticleType;
import org.dgrf.yamunakinare.db.entities.ArticleTypeMetaDefinition;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.dgrf.yamunakinare.db.JPA.exceptions.IllegalOrphanException;
import org.dgrf.yamunakinare.db.JPA.exceptions.NonexistentEntityException;
import org.dgrf.yamunakinare.db.JPA.exceptions.PreexistingEntityException;
import org.dgrf.yamunakinare.db.entities.Articles;
import org.dgrf.yamunakinare.db.entities.ArticlesPK;

/**
 *
 * @author bhaduri
 */
public class ArticlesJpaController implements Serializable {

    public ArticlesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Articles articles) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (articles.getArticlesPK() == null) {
            articles.setArticlesPK(new ArticlesPK());
        }
        if (articles.getArticleTypeMetaDefinitionList() == null) {
            articles.setArticleTypeMetaDefinitionList(new ArrayList<ArticleTypeMetaDefinition>());
        }
        articles.getArticlesPK().setArticleTypeId(articles.getArticleType().getId());
        List<String> illegalOrphanMessages = null;
        ArticleType articleTypeOrphanCheck = articles.getArticleType();
        if (articleTypeOrphanCheck != null) {
            Articles oldArticlesOfArticleType = articleTypeOrphanCheck.getArticles();
            if (oldArticlesOfArticleType != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The ArticleType " + articleTypeOrphanCheck + " already has an item of type Articles whose articleType column cannot be null. Please make another selection for the articleType field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TaxonomyKirtanXref taxonomyKirtanXref = articles.getTaxonomyKirtanXref();
            if (taxonomyKirtanXref != null) {
                taxonomyKirtanXref = em.getReference(taxonomyKirtanXref.getClass(), taxonomyKirtanXref.getTaxonomyKirtanXrefPK());
                articles.setTaxonomyKirtanXref(taxonomyKirtanXref);
            }
            ArticleType articleType = articles.getArticleType();
            if (articleType != null) {
                articleType = em.getReference(articleType.getClass(), articleType.getId());
                articles.setArticleType(articleType);
            }
            List<ArticleTypeMetaDefinition> attachedArticleTypeMetaDefinitionList = new ArrayList<ArticleTypeMetaDefinition>();
            for (ArticleTypeMetaDefinition articleTypeMetaDefinitionListArticleTypeMetaDefinitionToAttach : articles.getArticleTypeMetaDefinitionList()) {
                articleTypeMetaDefinitionListArticleTypeMetaDefinitionToAttach = em.getReference(articleTypeMetaDefinitionListArticleTypeMetaDefinitionToAttach.getClass(), articleTypeMetaDefinitionListArticleTypeMetaDefinitionToAttach.getArticleTypeMetaDefinitionPK());
                attachedArticleTypeMetaDefinitionList.add(articleTypeMetaDefinitionListArticleTypeMetaDefinitionToAttach);
            }
            articles.setArticleTypeMetaDefinitionList(attachedArticleTypeMetaDefinitionList);
            em.persist(articles);
            if (taxonomyKirtanXref != null) {
                Articles oldArticlesOfTaxonomyKirtanXref = taxonomyKirtanXref.getArticles();
                if (oldArticlesOfTaxonomyKirtanXref != null) {
                    oldArticlesOfTaxonomyKirtanXref.setTaxonomyKirtanXref(null);
                    oldArticlesOfTaxonomyKirtanXref = em.merge(oldArticlesOfTaxonomyKirtanXref);
                }
                taxonomyKirtanXref.setArticles(articles);
                taxonomyKirtanXref = em.merge(taxonomyKirtanXref);
            }
            if (articleType != null) {
                articleType.setArticles(articles);
                articleType = em.merge(articleType);
            }
            for (ArticleTypeMetaDefinition articleTypeMetaDefinitionListArticleTypeMetaDefinition : articles.getArticleTypeMetaDefinitionList()) {
                articleTypeMetaDefinitionListArticleTypeMetaDefinition.getArticlesList().add(articles);
                articleTypeMetaDefinitionListArticleTypeMetaDefinition = em.merge(articleTypeMetaDefinitionListArticleTypeMetaDefinition);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findArticles(articles.getArticlesPK()) != null) {
                throw new PreexistingEntityException("Articles " + articles + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Articles articles) throws IllegalOrphanException, NonexistentEntityException, Exception {
        articles.getArticlesPK().setArticleTypeId(articles.getArticleType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articles persistentArticles = em.find(Articles.class, articles.getArticlesPK());
            TaxonomyKirtanXref taxonomyKirtanXrefOld = persistentArticles.getTaxonomyKirtanXref();
            TaxonomyKirtanXref taxonomyKirtanXrefNew = articles.getTaxonomyKirtanXref();
            ArticleType articleTypeOld = persistentArticles.getArticleType();
            ArticleType articleTypeNew = articles.getArticleType();
            List<ArticleTypeMetaDefinition> articleTypeMetaDefinitionListOld = persistentArticles.getArticleTypeMetaDefinitionList();
            List<ArticleTypeMetaDefinition> articleTypeMetaDefinitionListNew = articles.getArticleTypeMetaDefinitionList();
            List<String> illegalOrphanMessages = null;
            if (taxonomyKirtanXrefOld != null && !taxonomyKirtanXrefOld.equals(taxonomyKirtanXrefNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain TaxonomyKirtanXref " + taxonomyKirtanXrefOld + " since its articles field is not nullable.");
            }
            if (articleTypeNew != null && !articleTypeNew.equals(articleTypeOld)) {
                Articles oldArticlesOfArticleType = articleTypeNew.getArticles();
                if (oldArticlesOfArticleType != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The ArticleType " + articleTypeNew + " already has an item of type Articles whose articleType column cannot be null. Please make another selection for the articleType field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (taxonomyKirtanXrefNew != null) {
                taxonomyKirtanXrefNew = em.getReference(taxonomyKirtanXrefNew.getClass(), taxonomyKirtanXrefNew.getTaxonomyKirtanXrefPK());
                articles.setTaxonomyKirtanXref(taxonomyKirtanXrefNew);
            }
            if (articleTypeNew != null) {
                articleTypeNew = em.getReference(articleTypeNew.getClass(), articleTypeNew.getId());
                articles.setArticleType(articleTypeNew);
            }
            List<ArticleTypeMetaDefinition> attachedArticleTypeMetaDefinitionListNew = new ArrayList<ArticleTypeMetaDefinition>();
            for (ArticleTypeMetaDefinition articleTypeMetaDefinitionListNewArticleTypeMetaDefinitionToAttach : articleTypeMetaDefinitionListNew) {
                articleTypeMetaDefinitionListNewArticleTypeMetaDefinitionToAttach = em.getReference(articleTypeMetaDefinitionListNewArticleTypeMetaDefinitionToAttach.getClass(), articleTypeMetaDefinitionListNewArticleTypeMetaDefinitionToAttach.getArticleTypeMetaDefinitionPK());
                attachedArticleTypeMetaDefinitionListNew.add(articleTypeMetaDefinitionListNewArticleTypeMetaDefinitionToAttach);
            }
            articleTypeMetaDefinitionListNew = attachedArticleTypeMetaDefinitionListNew;
            articles.setArticleTypeMetaDefinitionList(articleTypeMetaDefinitionListNew);
            articles = em.merge(articles);
            if (taxonomyKirtanXrefNew != null && !taxonomyKirtanXrefNew.equals(taxonomyKirtanXrefOld)) {
                Articles oldArticlesOfTaxonomyKirtanXref = taxonomyKirtanXrefNew.getArticles();
                if (oldArticlesOfTaxonomyKirtanXref != null) {
                    oldArticlesOfTaxonomyKirtanXref.setTaxonomyKirtanXref(null);
                    oldArticlesOfTaxonomyKirtanXref = em.merge(oldArticlesOfTaxonomyKirtanXref);
                }
                taxonomyKirtanXrefNew.setArticles(articles);
                taxonomyKirtanXrefNew = em.merge(taxonomyKirtanXrefNew);
            }
            if (articleTypeOld != null && !articleTypeOld.equals(articleTypeNew)) {
                articleTypeOld.setArticles(null);
                articleTypeOld = em.merge(articleTypeOld);
            }
            if (articleTypeNew != null && !articleTypeNew.equals(articleTypeOld)) {
                articleTypeNew.setArticles(articles);
                articleTypeNew = em.merge(articleTypeNew);
            }
            for (ArticleTypeMetaDefinition articleTypeMetaDefinitionListOldArticleTypeMetaDefinition : articleTypeMetaDefinitionListOld) {
                if (!articleTypeMetaDefinitionListNew.contains(articleTypeMetaDefinitionListOldArticleTypeMetaDefinition)) {
                    articleTypeMetaDefinitionListOldArticleTypeMetaDefinition.getArticlesList().remove(articles);
                    articleTypeMetaDefinitionListOldArticleTypeMetaDefinition = em.merge(articleTypeMetaDefinitionListOldArticleTypeMetaDefinition);
                }
            }
            for (ArticleTypeMetaDefinition articleTypeMetaDefinitionListNewArticleTypeMetaDefinition : articleTypeMetaDefinitionListNew) {
                if (!articleTypeMetaDefinitionListOld.contains(articleTypeMetaDefinitionListNewArticleTypeMetaDefinition)) {
                    articleTypeMetaDefinitionListNewArticleTypeMetaDefinition.getArticlesList().add(articles);
                    articleTypeMetaDefinitionListNewArticleTypeMetaDefinition = em.merge(articleTypeMetaDefinitionListNewArticleTypeMetaDefinition);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ArticlesPK id = articles.getArticlesPK();
                if (findArticles(id) == null) {
                    throw new NonexistentEntityException("The articles with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ArticlesPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Articles articles;
            try {
                articles = em.getReference(Articles.class, id);
                articles.getArticlesPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articles with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            TaxonomyKirtanXref taxonomyKirtanXrefOrphanCheck = articles.getTaxonomyKirtanXref();
            if (taxonomyKirtanXrefOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Articles (" + articles + ") cannot be destroyed since the TaxonomyKirtanXref " + taxonomyKirtanXrefOrphanCheck + " in its taxonomyKirtanXref field has a non-nullable articles field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            ArticleType articleType = articles.getArticleType();
            if (articleType != null) {
                articleType.setArticles(null);
                articleType = em.merge(articleType);
            }
            List<ArticleTypeMetaDefinition> articleTypeMetaDefinitionList = articles.getArticleTypeMetaDefinitionList();
            for (ArticleTypeMetaDefinition articleTypeMetaDefinitionListArticleTypeMetaDefinition : articleTypeMetaDefinitionList) {
                articleTypeMetaDefinitionListArticleTypeMetaDefinition.getArticlesList().remove(articles);
                articleTypeMetaDefinitionListArticleTypeMetaDefinition = em.merge(articleTypeMetaDefinitionListArticleTypeMetaDefinition);
            }
            em.remove(articles);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Articles> findArticlesEntities() {
        return findArticlesEntities(true, -1, -1);
    }

    public List<Articles> findArticlesEntities(int maxResults, int firstResult) {
        return findArticlesEntities(false, maxResults, firstResult);
    }

    private List<Articles> findArticlesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Articles.class));
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

    public Articles findArticles(ArticlesPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Articles.class, id);
        } finally {
            em.close();
        }
    }

    public int getArticlesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Articles> rt = cq.from(Articles.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
