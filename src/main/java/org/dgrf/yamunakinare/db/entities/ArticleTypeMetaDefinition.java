/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.db.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author bhaduri
 */
@Entity
@Table(name = "article_type_meta_definition")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ArticleTypeMetaDefinition.findAll", query = "SELECT a FROM ArticleTypeMetaDefinition a")})
public class ArticleTypeMetaDefinition implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ArticleTypeMetaDefinitionPK articleTypeMetaDefinitionPK;
    @Size(max = 45)
    @Column(name = "name")
    private String name;
    @JoinTable(name = "article_meta_data", joinColumns = {
        @JoinColumn(name = "article_type_meta_definition_id", referencedColumnName = "id"),
        @JoinColumn(name = "article_type_meta_definition_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "articles_id", referencedColumnName = "id"),
        @JoinColumn(name = "articles_id", referencedColumnName = "id"),
        @JoinColumn(name = "articles_article_type_id", referencedColumnName = "article_type_id"),
        @JoinColumn(name = "articles_article_type_id", referencedColumnName = "article_type_id")})
    @ManyToMany
    private List<Articles> articlesList;
    @JoinColumns({
        @JoinColumn(name = "article_type_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "article_type_id", referencedColumnName = "id", insertable = false, updatable = false)})
    @OneToOne(optional = false)
    private ArticleType articleType;

    public ArticleTypeMetaDefinition() {
    }

    public ArticleTypeMetaDefinition(ArticleTypeMetaDefinitionPK articleTypeMetaDefinitionPK) {
        this.articleTypeMetaDefinitionPK = articleTypeMetaDefinitionPK;
    }

    public ArticleTypeMetaDefinition(int id, int articleTypeId) {
        this.articleTypeMetaDefinitionPK = new ArticleTypeMetaDefinitionPK(id, articleTypeId);
    }

    public ArticleTypeMetaDefinitionPK getArticleTypeMetaDefinitionPK() {
        return articleTypeMetaDefinitionPK;
    }

    public void setArticleTypeMetaDefinitionPK(ArticleTypeMetaDefinitionPK articleTypeMetaDefinitionPK) {
        this.articleTypeMetaDefinitionPK = articleTypeMetaDefinitionPK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public List<Articles> getArticlesList() {
        return articlesList;
    }

    public void setArticlesList(List<Articles> articlesList) {
        this.articlesList = articlesList;
    }

    public ArticleType getArticleType() {
        return articleType;
    }

    public void setArticleType(ArticleType articleType) {
        this.articleType = articleType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (articleTypeMetaDefinitionPK != null ? articleTypeMetaDefinitionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ArticleTypeMetaDefinition)) {
            return false;
        }
        ArticleTypeMetaDefinition other = (ArticleTypeMetaDefinition) object;
        if ((this.articleTypeMetaDefinitionPK == null && other.articleTypeMetaDefinitionPK != null) || (this.articleTypeMetaDefinitionPK != null && !this.articleTypeMetaDefinitionPK.equals(other.articleTypeMetaDefinitionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.ArticleTypeMetaDefinition[ articleTypeMetaDefinitionPK=" + articleTypeMetaDefinitionPK + " ]";
    }
    
}
