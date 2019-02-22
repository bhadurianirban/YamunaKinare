/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.db.entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhaduri
 */
@Entity
@Table(name = "taxonomy_kirtan_xref")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TaxonomyKirtanXref.findAll", query = "SELECT t FROM TaxonomyKirtanXref t")})
public class TaxonomyKirtanXref implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TaxonomyKirtanXrefPK taxonomyKirtanXrefPK;
    @JoinColumns({
        @JoinColumn(name = "articles_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "articles_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "articles_article_type_id", referencedColumnName = "article_type_id", insertable = false, updatable = false),
        @JoinColumn(name = "articles_article_type_id", referencedColumnName = "article_type_id", insertable = false, updatable = false)})
    @OneToOne(optional = false)
    private Articles articles;
    @JoinColumns({
        @JoinColumn(name = "taxonomy_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "taxonomy_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "taxonomy_types_id", referencedColumnName = "taxonomy_types_id", insertable = false, updatable = false),
        @JoinColumn(name = "taxonomy_types_id", referencedColumnName = "taxonomy_types_id", insertable = false, updatable = false)})
    @OneToOne(optional = false)
    private Taxonomy taxonomy;

    public TaxonomyKirtanXref() {
    }

    public TaxonomyKirtanXref(TaxonomyKirtanXrefPK taxonomyKirtanXrefPK) {
        this.taxonomyKirtanXrefPK = taxonomyKirtanXrefPK;
    }

    public TaxonomyKirtanXref(int taxonomyId, int taxonomyTypesId, int articlesId, int articlesArticleTypeId) {
        this.taxonomyKirtanXrefPK = new TaxonomyKirtanXrefPK(taxonomyId, taxonomyTypesId, articlesId, articlesArticleTypeId);
    }

    public TaxonomyKirtanXrefPK getTaxonomyKirtanXrefPK() {
        return taxonomyKirtanXrefPK;
    }

    public void setTaxonomyKirtanXrefPK(TaxonomyKirtanXrefPK taxonomyKirtanXrefPK) {
        this.taxonomyKirtanXrefPK = taxonomyKirtanXrefPK;
    }

    public Articles getArticles() {
        return articles;
    }

    public void setArticles(Articles articles) {
        this.articles = articles;
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taxonomyKirtanXrefPK != null ? taxonomyKirtanXrefPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TaxonomyKirtanXref)) {
            return false;
        }
        TaxonomyKirtanXref other = (TaxonomyKirtanXref) object;
        if ((this.taxonomyKirtanXrefPK == null && other.taxonomyKirtanXrefPK != null) || (this.taxonomyKirtanXrefPK != null && !this.taxonomyKirtanXrefPK.equals(other.taxonomyKirtanXrefPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.TaxonomyKirtanXref[ taxonomyKirtanXrefPK=" + taxonomyKirtanXrefPK + " ]";
    }
    
}
