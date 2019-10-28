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
 * @author dgrfi
 */
@Embeddable
public class TaxonomyPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "taxonomy_types_id")
    private int taxonomyTypesId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private int id;

    public TaxonomyPK() {
    }

    public TaxonomyPK(int taxonomyTypesId, int id) {
        this.taxonomyTypesId = taxonomyTypesId;
        this.id = id;
    }

    public int getTaxonomyTypesId() {
        return taxonomyTypesId;
    }

    public void setTaxonomyTypesId(int taxonomyTypesId) {
        this.taxonomyTypesId = taxonomyTypesId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) taxonomyTypesId;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TaxonomyPK)) {
            return false;
        }
        TaxonomyPK other = (TaxonomyPK) object;
        if (this.taxonomyTypesId != other.taxonomyTypesId) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.TaxonomyPK[ taxonomyTypesId=" + taxonomyTypesId + ", id=" + id + " ]";
    }
    
}
