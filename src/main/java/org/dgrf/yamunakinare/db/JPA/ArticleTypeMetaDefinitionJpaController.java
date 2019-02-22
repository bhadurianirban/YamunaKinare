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
import org.dgrf.yamunakinare.db.entities.ArticleType;
import org.dgrf.yamunakinare.db.entities.Articles;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.dgrf.yamunakinare.db.JPA.exceptions.IllegalOrphanException;
import org.dgrf.yamunakinare.db.JPA.exceptions.NonexistentEntityException;
import org.dgrf.yamunakinare.db.JPA.exceptions.PreexistingEntityException;
import org.dgrf.yamunakinare.db.entities.ArticleTypeMetaDefinition;
import org.dgrf.yamunakinare.db.entities.ArticleTypeMetaDefinitionPK;

/**
 *
 * @author bhaduri
 */
public class ArticleTypeMetaDefinitionJpaController implements Serializable {

    public ArticleTypeMetaDefinitionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ArticleTypeMetaDefinition articleTypeMetaDefinition) throws IllegalOrphanException, PreexistingEntityException, Exception {
        if (articleTypeMetaDefinition.getArticleTypeMetaDefinitionPK() == null) {
            articleTypeMetaDefinition.setArticleTypeMetaDefinitionPK(new ArticleTypeMetaDefinitionPK());
        }
        if (articleTypeMetaDefinition.getArticlesList() == null) {
            articleTypeMetaDefinition.setArticlesList(new ArrayList<Articles>());
        }
        articleTypeMetaDefinition.getArticleTypeMetaDefinitionPK().setArticleTypeId(articleTypeMetaDefinition.getArticleType().getId());
        List<String> illegalOrphanMessages = null;
        ArticleType articleTypeOrphanCheck = articleTypeMetaDefinition.getArticleType();
        if (articleTypeOrphanCheck != null) {
            ArticleTypeMetaDefinition oldArticleTypeMetaDefinitionOfArticleType = articleTypeOrphanCheck.getArticleTypeMetaDefinition();
            if (oldArticleTypeMetaDefinitionOfArticleType != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The ArticleType " + articleTypeOrphanCheck + " already has an item of type ArticleTypeMetaDefinition whose articleType column cannot be null. Please make another selection for the articleType field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArticleType articleType = articleTypeMetaDefinition.getArticleType();
            if (articleType != null) {
                articleType = em.getReference(articleType.getClass(), articleType.getId());
                articleTypeMetaDefinition.setArticleType(articleType);
            }
            List<Articles> attachedArticlesList = new ArrayList<Articles>();
            for (Articles articlesListArticlesToAttach : articleTypeMetaDefinition.getArticlesList()) {
                articlesListArticlesToAttach = em.getReference(articlesListArticlesToAttach.getClass(), articlesListArticlesToAttach.getArticlesPK());
                attachedArticlesList.add(articlesListArticlesToAttach);
            }
            articleTypeMetaDefinition.setArticlesList(attachedArticlesList);
            em.persist(articleTypeMetaDefinition);
            if (articleType != null) {
                articleType.setArticleTypeMetaDefinition(articleTypeMetaDefinition);
                articleType = em.merge(articleType);
            }
            for (Articles articlesListArticles : articleTypeMetaDefinition.getArticlesList()) {
                articlesListArticles.getArticleTypeMetaDefinitionList().add(articleTypeMetaDefinition);
                articlesListArticles = em.merge(articlesListArticles);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findArticleTypeMetaDefinition(articleTypeMetaDefinition.getArticleTypeMetaDefinitionPK()) != null) {
                throw new PreexistingEntityException("ArticleTypeMetaDefinition " + articleTypeMetaDefinition + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ArticleTypeMetaDefinition articleTypeMetaDefinition) throws IllegalOrphanException, NonexistentEntityException, Exception {
        articleTypeMetaDefinition.getArticleTypeMetaDefinitionPK().setArticleTypeId(articleTypeMetaDefinition.getArticleType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArticleTypeMetaDefinition persistentArticleTypeMetaDefinition = em.find(ArticleTypeMetaDefinition.class, articleTypeMetaDefinition.getArticleTypeMetaDefinitionPK());
            ArticleType articleTypeOld = persistentArticleTypeMetaDefinition.getArticleType();
            ArticleType articleTypeNew = articleTypeMetaDefinition.getArticleType();
            List<Articles> articlesListOld = persistentArticleTypeMetaDefinition.getArticlesList();
            List<Articles> articlesListNew = articleTypeMetaDefinition.getArticlesList();
            List<String> illegalOrphanMessages = null;
            if (articleTypeNew != null && !articleTypeNew.equals(articleTypeOld)) {
                ArticleTypeMetaDefinition oldArticleTypeMetaDefinitionOfArticleType = articleTypeNew.getArticleTypeMetaDefinition();
                if (oldArticleTypeMetaDefinitionOfArticleType != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The ArticleType " + articleTypeNew + " already has an item of type ArticleTypeMetaDefinition whose articleType column cannot be null. Please make another selection for the articleType field.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (articleTypeNew != null) {
                articleTypeNew = em.getReference(articleTypeNew.getClass(), articleTypeNew.getId());
                articleTypeMetaDefinition.setArticleType(articleTypeNew);
            }
            List<Articles> attachedArticlesListNew = new ArrayList<Articles>();
            for (Articles articlesListNewArticlesToAttach : articlesListNew) {
                articlesListNewArticlesToAttach = em.getReference(articlesListNewArticlesToAttach.getClass(), articlesListNewArticlesToAttach.getArticlesPK());
                attachedArticlesListNew.add(articlesListNewArticlesToAttach);
            }
            articlesListNew = attachedArticlesListNew;
            articleTypeMetaDefinition.setArticlesList(articlesListNew);
            articleTypeMetaDefinition = em.merge(articleTypeMetaDefinition);
            if (articleTypeOld != null && !articleTypeOld.equals(articleTypeNew)) {
                articleTypeOld.setArticleTypeMetaDefinition(null);
                articleTypeOld = em.merge(articleTypeOld);
            }
            if (articleTypeNew != null && !articleTypeNew.equals(articleTypeOld)) {
                articleTypeNew.setArticleTypeMetaDefinition(articleTypeMetaDefinition);
                articleTypeNew = em.merge(articleTypeNew);
            }
            for (Articles articlesListOldArticles : articlesListOld) {
                if (!articlesListNew.contains(articlesListOldArticles)) {
                    articlesListOldArticles.getArticleTypeMetaDefinitionList().remove(articleTypeMetaDefinition);
                    articlesListOldArticles = em.merge(articlesListOldArticles);
                }
            }
            for (Articles articlesListNewArticles : articlesListNew) {
                if (!articlesListOld.contains(articlesListNewArticles)) {
                    articlesListNewArticles.getArticleTypeMetaDefinitionList().add(articleTypeMetaDefinition);
                    articlesListNewArticles = em.merge(articlesListNewArticles);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                ArticleTypeMetaDefinitionPK id = articleTypeMetaDefinition.getArticleTypeMetaDefinitionPK();
                if (findArticleTypeMetaDefinition(id) == null) {
                    throw new NonexistentEntityException("The articleTypeMetaDefinition with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ArticleTypeMetaDefinitionPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArticleTypeMetaDefinition articleTypeMetaDefinition;
            try {
                articleTypeMetaDefinition = em.getReference(ArticleTypeMetaDefinition.class, id);
                articleTypeMetaDefinition.getArticleTypeMetaDefinitionPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articleTypeMetaDefinition with id " + id + " no longer exists.", enfe);
            }
            ArticleType articleType = articleTypeMetaDefinition.getArticleType();
            if (articleType != null) {
                articleType.setArticleTypeMetaDefinition(null);
                articleType = em.merge(articleType);
            }
            List<Articles> articlesList = articleTypeMetaDefinition.getArticlesList();
            for (Articles articlesListArticles : articlesList) {
                articlesListArticles.getArticleTypeMetaDefinitionList().remove(articleTypeMetaDefinition);
                articlesListArticles = em.merge(articlesListArticles);
            }
            em.remove(articleTypeMetaDefinition);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ArticleTypeMetaDefinition> findArticleTypeMetaDefinitionEntities() {
        return findArticleTypeMetaDefinitionEntities(true, -1, -1);
    }

    public List<ArticleTypeMetaDefinition> findArticleTypeMetaDefinitionEntities(int maxResults, int firstResult) {
        return findArticleTypeMetaDefinitionEntities(false, maxResults, firstResult);
    }

    private List<ArticleTypeMetaDefinition> findArticleTypeMetaDefinitionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ArticleTypeMetaDefinition.class));
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

    public ArticleTypeMetaDefinition findArticleTypeMetaDefinition(ArticleTypeMetaDefinitionPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ArticleTypeMetaDefinition.class, id);
        } finally {
            em.close();
        }
    }

    public int getArticleTypeMetaDefinitionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ArticleTypeMetaDefinition> rt = cq.from(ArticleTypeMetaDefinition.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
