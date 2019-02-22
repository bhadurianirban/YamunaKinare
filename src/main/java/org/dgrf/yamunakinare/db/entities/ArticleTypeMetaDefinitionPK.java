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
public class ArticleTypeMetaDefinitionPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "article_type_id")
    private int articleTypeId;

    public ArticleTypeMetaDefinitionPK() {
    }

    public ArticleTypeMetaDefinitionPK(int id, int articleTypeId) {
        this.id = id;
        this.articleTypeId = articleTypeId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getArticleTypeId() {
        return articleTypeId;
    }

    public void setArticleTypeId(int articleTypeId) {
        this.articleTypeId = articleTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) articleTypeId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArticleTypeMetaDefinitionPK)) {
            return false;
        }
        ArticleTypeMetaDefinitionPK other = (ArticleTypeMetaDefinitionPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.articleTypeId != other.articleTypeId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.ArticleTypeMetaDefinitionPK[ id=" + id + ", articleTypeId=" + articleTypeId + " ]";
    }
    
}
