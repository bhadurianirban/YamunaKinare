/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.db.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author dgrfi
 */
@Entity
@Table(name = "kirtan")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Kirtan.findAll", query = "SELECT k FROM Kirtan k")})
public class Kirtan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
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
    @JoinTable(name = "taxonomy_has_kirtan", joinColumns = {
        @JoinColumn(name = "kirtan_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "taxonomy_taxonomy_types_id", referencedColumnName = "taxonomy_types_id"),
        @JoinColumn(name = "taxonomy_id", referencedColumnName = "id")})
    @ManyToMany
    private List<Taxonomy> taxonomyList;

    public Kirtan() {
    }

    public Kirtan(Integer id) {
        this.id = id;
    }

    public Kirtan(Integer id, String name, String content) {
        this.id = id;
        this.name = name;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public List<Taxonomy> getTaxonomyList() {
        return taxonomyList;
    }

    public void setTaxonomyList(List<Taxonomy> taxonomyList) {
        this.taxonomyList = taxonomyList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kirtan)) {
            return false;
        }
        Kirtan other = (Kirtan) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.dgrf.yamunakinare.db.entities.Kirtan[ id=" + id + " ]";
    }
    
}
