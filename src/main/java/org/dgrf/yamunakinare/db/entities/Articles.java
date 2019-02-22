/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.db.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author bhaduri
 */
@Entity
@Table(name = "articles")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Articles.findAll", query = "SELECT a FROM Articles a")})
public class Articles implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ArticlesPK articlesPK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 2147483647)
    @Column(name = "content")
    private String content;
    @ManyToMany(mappedBy = "articlesList")
    private List<ArticleTypeMetaDefinition> articleTypeMetaDefinitionList;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "articles")
    private TaxonomyKirtanXref taxonomyKirtanXref;
    @JoinColumns({
        @JoinColumn(name = "article_type_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "article_type_id", referencedColumnName = "id", insertable = false, updatable = false)})
    @OneToOne(optional = false)
    private ArticleType articleType;

    public Articles() {
    }

    public Articles(ArticlesPK articlesPK) {
        this.articlesPK = articlesPK;
    }

    public Articles(ArticlesPK articlesPK, String name, String content) {
        this.articlesPK = articlesPK;
        this.name = name;
        this.content = content;
    }

    public Articles(int id, int articleTypeId) {
        this.articlesPK = new ArticlesPK(id, articleTypeId);
    }

    public ArticlesPK getArticlesPK() {
        return articlesPK;
    }

    public void setArticlesPK(ArticlesPK articlesPK) {
        this.articlesPK = articlesPK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @XmlTransient
    public List<ArticleTypeMetaDefinition> getArticleTypeMetaDefinitionList() {
        return articleTypeMetaDefinitionList;
    }

    public void setArticleTypeMetaDefinitionList(List<ArticleTypeMetaDefinition> articleTypeMetaDefinitionList) {
        this.articleTypeMetaDefinitionList = articleTypeMetaDefinitionList;
    }

    public TaxonomyKirtanXref getTaxonomyKirtanXref() {
        return taxonomyKirtanXref;
    }

    public void setTaxonomyKirtanXref(TaxonomyKirtanXref taxonomyKirtanXref) {
        this.taxonomyKirtanXref = taxonomyKirtanXref;
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
        hash += (articlesPK != null ? articlesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Articles)) {
            return false;
        }
        Articles other = (Articles) object;
        if ((this.articlesPK == null && other.articlesPK != null) || (this.articlesPK != null && !this.articlesPK.equals(other.articlesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.Articles[ articlesPK=" + articlesPK + " ]";
    }
    
}
