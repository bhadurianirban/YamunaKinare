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
import org.dgrf.yamunakinare.db.entities.ArticleTypeMetaDefinition;
import org.dgrf.yamunakinare.db.entities.Articles;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.dgrf.yamunakinare.db.JPA.exceptions.IllegalOrphanException;
import org.dgrf.yamunakinare.db.JPA.exceptions.NonexistentEntityException;
import org.dgrf.yamunakinare.db.JPA.exceptions.PreexistingEntityException;
import org.dgrf.yamunakinare.db.entities.ArticleType;

/**
 *
 * @author bhaduri
 */
public class ArticleTypeJpaController implements Serializable {

    public ArticleTypeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ArticleType articleType) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArticleTypeMetaDefinition articleTypeMetaDefinition = articleType.getArticleTypeMetaDefinition();
            if (articleTypeMetaDefinition != null) {
                articleTypeMetaDefinition = em.getReference(articleTypeMetaDefinition.getClass(), articleTypeMetaDefinition.getArticleTypeMetaDefinitionPK());
                articleType.setArticleTypeMetaDefinition(articleTypeMetaDefinition);
            }
            Articles articles = articleType.getArticles();
            if (articles != null) {
                articles = em.getReference(articles.getClass(), articles.getArticlesPK());
                articleType.setArticles(articles);
            }
            em.persist(articleType);
            if (articleTypeMetaDefinition != null) {
                ArticleType oldArticleTypeOfArticleTypeMetaDefinition = articleTypeMetaDefinition.getArticleType();
                if (oldArticleTypeOfArticleTypeMetaDefinition != null) {
                    oldArticleTypeOfArticleTypeMetaDefinition.setArticleTypeMetaDefinition(null);
                    oldArticleTypeOfArticleTypeMetaDefinition = em.merge(oldArticleTypeOfArticleTypeMetaDefinition);
                }
                articleTypeMetaDefinition.setArticleType(articleType);
                articleTypeMetaDefinition = em.merge(articleTypeMetaDefinition);
            }
            if (articles != null) {
                ArticleType oldArticleTypeOfArticles = articles.getArticleType();
                if (oldArticleTypeOfArticles != null) {
                    oldArticleTypeOfArticles.setArticles(null);
                    oldArticleTypeOfArticles = em.merge(oldArticleTypeOfArticles);
                }
                articles.setArticleType(articleType);
                articles = em.merge(articles);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findArticleType(articleType.getId()) != null) {
                throw new PreexistingEntityException("ArticleType " + articleType + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ArticleType articleType) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ArticleType persistentArticleType = em.find(ArticleType.class, articleType.getId());
            ArticleTypeMetaDefinition articleTypeMetaDefinitionOld = persistentArticleType.getArticleTypeMetaDefinition();
            ArticleTypeMetaDefinition articleTypeMetaDefinitionNew = articleType.getArticleTypeMetaDefinition();
            Articles articlesOld = persistentArticleType.getArticles();
            Articles articlesNew = articleType.getArticles();
            List<String> illegalOrphanMessages = null;
            if (articleTypeMetaDefinitionOld != null && !articleTypeMetaDefinitionOld.equals(articleTypeMetaDefinitionNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain ArticleTypeMetaDefinition " + articleTypeMetaDefinitionOld + " since its articleType field is not nullable.");
            }
            if (articlesOld != null && !articlesOld.equals(articlesNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Articles " + articlesOld + " since its articleType field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (articleTypeMetaDefinitionNew != null) {
                articleTypeMetaDefinitionNew = em.getReference(articleTypeMetaDefinitionNew.getClass(), articleTypeMetaDefinitionNew.getArticleTypeMetaDefinitionPK());
                articleType.setArticleTypeMetaDefinition(articleTypeMetaDefinitionNew);
            }
            if (articlesNew != null) {
                articlesNew = em.getReference(articlesNew.getClass(), articlesNew.getArticlesPK());
                articleType.setArticles(articlesNew);
            }
            articleType = em.merge(articleType);
            if (articleTypeMetaDefinitionNew != null && !articleTypeMetaDefinitionNew.equals(articleTypeMetaDefinitionOld)) {
                ArticleType oldArticleTypeOfArticleTypeMetaDefinition = articleTypeMetaDefinitionNew.getArticleType();
                if (oldArticleTypeOfArticleTypeMetaDefinition != null) {
                    oldArticleTypeOfArticleTypeMetaDefinition.setArticleTypeMetaDefinition(null);
                    oldArticleTypeOfArticleTypeMetaDefinition = em.merge(oldArticleTypeOfArticleTypeMetaDefinition);
                }
                articleTypeMetaDefinitionNew.setArticleType(articleType);
                articleTypeMetaDefinitionNew = em.merge(articleTypeMetaDefinitionNew);
            }
            if (articlesNew != null && !articlesNew.equals(articlesOld)) {
                ArticleType oldArticleTypeOfArticles = articlesNew.getArticleType();
                if (oldArticleTypeOfArticles != null) {
                    oldArticleTypeOfArticles.setArticles(null);
                    oldArticleTypeOfArticles = em.merge(oldArticleTypeOfArticles);
                }
                articlesNew.setArticleType(articleType);
                articlesNew = em.merge(articlesNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = articleType.getId();
                if (findArticleType(id) == null) {
                    throw new NonexistentEntityException("The articleType with id " + id + " no longer exists.");
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
            ArticleType articleType;
            try {
                articleType = em.getReference(ArticleType.class, id);
                articleType.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articleType with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            ArticleTypeMetaDefinition articleTypeMetaDefinitionOrphanCheck = articleType.getArticleTypeMetaDefinition();
            if (articleTypeMetaDefinitionOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ArticleType (" + articleType + ") cannot be destroyed since the ArticleTypeMetaDefinition " + articleTypeMetaDefinitionOrphanCheck + " in its articleTypeMetaDefinition field has a non-nullable articleType field.");
            }
            Articles articlesOrphanCheck = articleType.getArticles();
            if (articlesOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ArticleType (" + articleType + ") cannot be destroyed since the Articles " + articlesOrphanCheck + " in its articles field has a non-nullable articleType field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(articleType);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ArticleType> findArticleTypeEntities() {
        return findArticleTypeEntities(true, -1, -1);
    }

    public List<ArticleType> findArticleTypeEntities(int maxResults, int firstResult) {
        return findArticleTypeEntities(false, maxResults, firstResult);
    }

    private List<ArticleType> findArticleTypeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ArticleType.class));
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

    public ArticleType findArticleType(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ArticleType.class, id);
        } finally {
            em.close();
        }
    }

    public int getArticleTypeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ArticleType> rt = cq.from(ArticleType.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
