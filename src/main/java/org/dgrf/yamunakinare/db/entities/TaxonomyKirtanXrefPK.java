/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.db.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author bhaduri
 */
@Embeddable
public class TaxonomyKirtanXrefPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "taxonomy_id")
    private int taxonomyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "taxonomy_types_id")
    private int taxonomyTypesId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "articles_id")
    private int articlesId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "articles_article_type_id")
    private int articlesArticleTypeId;

    public TaxonomyKirtanXrefPK() {
    }

    public TaxonomyKirtanXrefPK(int taxonomyId, int taxonomyTypesId, int articlesId, int articlesArticleTypeId) {
        this.taxonomyId = taxonomyId;
        this.taxonomyTypesId = taxonomyTypesId;
        this.articlesId = articlesId;
        this.articlesArticleTypeId = articlesArticleTypeId;
    }

    public int getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(int taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public int getTaxonomyTypesId() {
        return taxonomyTypesId;
    }

    public void setTaxonomyTypesId(int taxonomyTypesId) {
        this.taxonomyTypesId = taxonomyTypesId;
    }

    public int getArticlesId() {
        return articlesId;
    }

    public void setArticlesId(int articlesId) {
        this.articlesId = articlesId;
    }

    public int getArticlesArticleTypeId() {
        return articlesArticleTypeId;
    }

    public void setArticlesArticleTypeId(int articlesArticleTypeId) {
        this.articlesArticleTypeId = articlesArticleTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) taxonomyId;
        hash += (int) taxonomyTypesId;
        hash += (int) articlesId;
        hash += (int) articlesArticleTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TaxonomyKirtanXrefPK)) {
            return false;
        }
        TaxonomyKirtanXrefPK other = (TaxonomyKirtanXrefPK) object;
        if (this.taxonomyId != other.taxonomyId) {
            return false;
        }
        if (this.taxonomyTypesId != other.taxonomyTypesId) {
            return false;
        }
        if (this.articlesId != other.articlesId) {
            return false;
        }
        if (this.articlesArticleTypeId != other.articlesArticleTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.TaxonomyKirtanXrefPK[ taxonomyId=" + taxonomyId + ", taxonomyTypesId=" + taxonomyTypesId + ", articlesId=" + articlesId + ", articlesArticleTypeId=" + articlesArticleTypeId + " ]";
    }
    
}
