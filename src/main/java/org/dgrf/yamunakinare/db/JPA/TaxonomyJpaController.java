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
import org.dgrf.yamunakinare.db.entities.Bishoy;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.dgrf.yamunakinare.db.JPA.exceptions.NonexistentEntityException;
import org.dgrf.yamunakinare.db.JPA.exceptions.PreexistingEntityException;
import org.dgrf.yamunakinare.db.entities.Kirtan;
import org.dgrf.yamunakinare.db.entities.Taxonomy;
import org.dgrf.yamunakinare.db.entities.TaxonomyPK;

/**
 *
 * @author dgrfi
 */
public class TaxonomyJpaController implements Serializable {

    public TaxonomyJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Taxonomy taxonomy) throws PreexistingEntityException, Exception {
        if (taxonomy.getTaxonomyPK() == null) {
            taxonomy.setTaxonomyPK(new TaxonomyPK());
        }
        if (taxonomy.getBishoyList() == null) {
            taxonomy.setBishoyList(new ArrayList<Bishoy>());
        }
        if (taxonomy.getKirtanList() == null) {
            taxonomy.setKirtanList(new ArrayList<Kirtan>());
        }
        taxonomy.getTaxonomyPK().setTaxonomyTypesId(taxonomy.getTaxonomyTypes().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            TaxonomyTypes taxonomyTypes = taxonomy.getTaxonomyTypes();
            if (taxonomyTypes != null) {
                taxonomyTypes = em.getReference(taxonomyTypes.getClass(), taxonomyTypes.getId());
                taxonomy.setTaxonomyTypes(taxonomyTypes);
            }
            List<Bishoy> attachedBishoyList = new ArrayList<Bishoy>();
            for (Bishoy bishoyListBishoyToAttach : taxonomy.getBishoyList()) {
                bishoyListBishoyToAttach = em.getReference(bishoyListBishoyToAttach.getClass(), bishoyListBishoyToAttach.getId());
                attachedBishoyList.add(bishoyListBishoyToAttach);
            }
            taxonomy.setBishoyList(attachedBishoyList);
            List<Kirtan> attachedKirtanList = new ArrayList<Kirtan>();
            for (Kirtan kirtanListKirtanToAttach : taxonomy.getKirtanList()) {
                kirtanListKirtanToAttach = em.getReference(kirtanListKirtanToAttach.getClass(), kirtanListKirtanToAttach.getId());
                attachedKirtanList.add(kirtanListKirtanToAttach);
            }
            taxonomy.setKirtanList(attachedKirtanList);
            em.persist(taxonomy);
            if (taxonomyTypes != null) {
                taxonomyTypes.getTaxonomyList().add(taxonomy);
                taxonomyTypes = em.merge(taxonomyTypes);
            }
            for (Bishoy bishoyListBishoy : taxonomy.getBishoyList()) {
                bishoyListBishoy.getTaxonomyList().add(taxonomy);
                bishoyListBishoy = em.merge(bishoyListBishoy);
            }
            for (Kirtan kirtanListKirtan : taxonomy.getKirtanList()) {
                kirtanListKirtan.getTaxonomyList().add(taxonomy);
                kirtanListKirtan = em.merge(kirtanListKirtan);
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

    public void edit(Taxonomy taxonomy) throws NonexistentEntityException, Exception {
        taxonomy.getTaxonomyPK().setTaxonomyTypesId(taxonomy.getTaxonomyTypes().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Taxonomy persistentTaxonomy = em.find(Taxonomy.class, taxonomy.getTaxonomyPK());
            TaxonomyTypes taxonomyTypesOld = persistentTaxonomy.getTaxonomyTypes();
            TaxonomyTypes taxonomyTypesNew = taxonomy.getTaxonomyTypes();
            List<Bishoy> bishoyListOld = persistentTaxonomy.getBishoyList();
            List<Bishoy> bishoyListNew = taxonomy.getBishoyList();
            List<Kirtan> kirtanListOld = persistentTaxonomy.getKirtanList();
            List<Kirtan> kirtanListNew = taxonomy.getKirtanList();
            if (taxonomyTypesNew != null) {
                taxonomyTypesNew = em.getReference(taxonomyTypesNew.getClass(), taxonomyTypesNew.getId());
                taxonomy.setTaxonomyTypes(taxonomyTypesNew);
            }
            List<Bishoy> attachedBishoyListNew = new ArrayList<Bishoy>();
            for (Bishoy bishoyListNewBishoyToAttach : bishoyListNew) {
                bishoyListNewBishoyToAttach = em.getReference(bishoyListNewBishoyToAttach.getClass(), bishoyListNewBishoyToAttach.getId());
                attachedBishoyListNew.add(bishoyListNewBishoyToAttach);
            }
            bishoyListNew = attachedBishoyListNew;
            taxonomy.setBishoyList(bishoyListNew);
            List<Kirtan> attachedKirtanListNew = new ArrayList<Kirtan>();
            for (Kirtan kirtanListNewKirtanToAttach : kirtanListNew) {
                kirtanListNewKirtanToAttach = em.getReference(kirtanListNewKirtanToAttach.getClass(), kirtanListNewKirtanToAttach.getId());
                attachedKirtanListNew.add(kirtanListNewKirtanToAttach);
            }
            kirtanListNew = attachedKirtanListNew;
            taxonomy.setKirtanList(kirtanListNew);
            taxonomy = em.merge(taxonomy);
            if (taxonomyTypesOld != null && !taxonomyTypesOld.equals(taxonomyTypesNew)) {
                taxonomyTypesOld.getTaxonomyList().remove(taxonomy);
                taxonomyTypesOld = em.merge(taxonomyTypesOld);
            }
            if (taxonomyTypesNew != null && !taxonomyTypesNew.equals(taxonomyTypesOld)) {
                taxonomyTypesNew.getTaxonomyList().add(taxonomy);
                taxonomyTypesNew = em.merge(taxonomyTypesNew);
            }
            for (Bishoy bishoyListOldBishoy : bishoyListOld) {
                if (!bishoyListNew.contains(bishoyListOldBishoy)) {
                    bishoyListOldBishoy.getTaxonomyList().remove(taxonomy);
                    bishoyListOldBishoy = em.merge(bishoyListOldBishoy);
                }
            }
            for (Bishoy bishoyListNewBishoy : bishoyListNew) {
                if (!bishoyListOld.contains(bishoyListNewBishoy)) {
                    bishoyListNewBishoy.getTaxonomyList().add(taxonomy);
                    bishoyListNewBishoy = em.merge(bishoyListNewBishoy);
                }
            }
            for (Kirtan kirtanListOldKirtan : kirtanListOld) {
                if (!kirtanListNew.contains(kirtanListOldKirtan)) {
                    kirtanListOldKirtan.getTaxonomyList().remove(taxonomy);
                    kirtanListOldKirtan = em.merge(kirtanListOldKirtan);
                }
            }
            for (Kirtan kirtanListNewKirtan : kirtanListNew) {
                if (!kirtanListOld.contains(kirtanListNewKirtan)) {
                    kirtanListNewKirtan.getTaxonomyList().add(taxonomy);
                    kirtanListNewKirtan = em.merge(kirtanListNewKirtan);
                }
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

    public void destroy(TaxonomyPK id) throws NonexistentEntityException {
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
            TaxonomyTypes taxonomyTypes = taxonomy.getTaxonomyTypes();
            if (taxonomyTypes != null) {
                taxonomyTypes.getTaxonomyList().remove(taxonomy);
                taxonomyTypes = em.merge(taxonomyTypes);
            }
            List<Bishoy> bishoyList = taxonomy.getBishoyList();
            for (Bishoy bishoyListBishoy : bishoyList) {
                bishoyListBishoy.getTaxonomyList().remove(taxonomy);
                bishoyListBishoy = em.merge(bishoyListBishoy);
            }
            List<Kirtan> kirtanList = taxonomy.getKirtanList();
            for (Kirtan kirtanListKirtan : kirtanList) {
                kirtanListKirtan.getTaxonomyList().remove(taxonomy);
                kirtanListKirtan = em.merge(kirtanListKirtan);
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
