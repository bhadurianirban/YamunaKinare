/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.db.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author bhaduri
 */
@Entity
@Table(name = "taxonomy")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Taxonomy.findAll", query = "SELECT t FROM Taxonomy t")})
public class Taxonomy implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TaxonomyPK taxonomyPK;
    @Basic(optional = false)
    @NotNull
    @Lob
    @Size(min = 1, max = 65535)
    @Column(name = "name")
    private String name;
    @Lob
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "parent_id")
    private int parentId;
    @JoinColumns({
        @JoinColumn(name = "taxonomy_types_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "taxonomy_types_id", referencedColumnName = "id", insertable = false, updatable = false)})
    @OneToOne(optional = false)
    private TaxonomyTypes taxonomyTypes;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "taxonomy")
    private TaxonomyKirtanXref taxonomyKirtanXref;

    public Taxonomy() {
    }

    public Taxonomy(TaxonomyPK taxonomyPK) {
        this.taxonomyPK = taxonomyPK;
    }

    public Taxonomy(TaxonomyPK taxonomyPK, String name, int parentId) {
        this.taxonomyPK = taxonomyPK;
        this.name = name;
        this.parentId = parentId;
    }

    public Taxonomy(int id, int taxonomyTypesId) {
        this.taxonomyPK = new TaxonomyPK(id, taxonomyTypesId);
    }

    public TaxonomyPK getTaxonomyPK() {
        return taxonomyPK;
    }

    public void setTaxonomyPK(TaxonomyPK taxonomyPK) {
        this.taxonomyPK = taxonomyPK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public TaxonomyTypes getTaxonomyTypes() {
        return taxonomyTypes;
    }

    public void setTaxonomyTypes(TaxonomyTypes taxonomyTypes) {
        this.taxonomyTypes = taxonomyTypes;
    }

    public TaxonomyKirtanXref getTaxonomyKirtanXref() {
        return taxonomyKirtanXref;
    }

    public void setTaxonomyKirtanXref(TaxonomyKirtanXref taxonomyKirtanXref) {
        this.taxonomyKirtanXref = taxonomyKirtanXref;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taxonomyPK != null ? taxonomyPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Taxonomy)) {
            return false;
        }
        Taxonomy other = (Taxonomy) object;
        if ((this.taxonomyPK == null && other.taxonomyPK != null) || (this.taxonomyPK != null && !this.taxonomyPK.equals(other.taxonomyPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.Taxonomy[ taxonomyPK=" + taxonomyPK + " ]";
    }
    
}
