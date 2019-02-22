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
public class TaxonomyPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "taxonomy_types_id")
    private int taxonomyTypesId;

    public TaxonomyPK() {
    }

    public TaxonomyPK(int id, int taxonomyTypesId) {
        this.id = id;
        this.taxonomyTypesId = taxonomyTypesId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaxonomyTypesId() {
        return taxonomyTypesId;
    }

    public void setTaxonomyTypesId(int taxonomyTypesId) {
        this.taxonomyTypesId = taxonomyTypesId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) id;
        hash += (int) taxonomyTypesId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TaxonomyPK)) {
            return false;
        }
        TaxonomyPK other = (TaxonomyPK) object;
        if (this.id != other.id) {
            return false;
        }
        if (this.taxonomyTypesId != other.taxonomyTypesId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.TaxonomyPK[ id=" + id + ", taxonomyTypesId=" + taxonomyTypesId + " ]";
    }
    
}
