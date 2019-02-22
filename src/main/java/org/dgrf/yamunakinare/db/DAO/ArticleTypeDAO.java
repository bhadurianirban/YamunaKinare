/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.db.DAO;

import javax.persistence.EntityManagerFactory;
import org.dgrf.yamunakinare.db.JPA.ArticleTypeJpaController;

/**
 *
 * @author bhaduri
 */
public class ArticleTypeDAO extends ArticleTypeJpaController {
    
    public ArticleTypeDAO(EntityManagerFactory emf) {
        super(emf);
    }
    
}
