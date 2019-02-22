/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.bl.service;

import java.util.List;
import java.util.stream.Collectors;
import org.dgrf.yamunakinare.db.DAO.ArticleTypeDAO;
import org.dgrf.yamunakinare.db.DAO.DatabaseConnection;
import org.dgrf.yamunakinare.db.entities.ArticleType;
import org.dgrf.yamunakinare.dto.ArticleTypeDTO;

/**
 *
 * @author bhaduri
 */
public class ArticleTypeService {
    public List <ArticleTypeDTO> getArticleTypeList() {
        ArticleTypeDAO articleTypeDAO = new ArticleTypeDAO(DatabaseConnection.EMF);
        List <ArticleType> articleTypeList = articleTypeDAO.findArticleTypeEntities();
        List <ArticleTypeDTO> articleTypeDTOList = articleTypeList.stream().map(art -> {
            ArticleTypeDTO articleTypeDTO = new ArticleTypeDTO();
            articleTypeDTO.setId(art.getId());
            articleTypeDTO.setDescription(art.getDescription());
            articleTypeDTO.setName(art.getName());
            return articleTypeDTO;
        }).collect(Collectors.toList());
        return articleTypeDTOList;
    }
    
}
