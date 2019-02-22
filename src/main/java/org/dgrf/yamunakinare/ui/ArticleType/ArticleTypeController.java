/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.ui.ArticleType;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.yamunakinare.bl.service.ArticleTypeService;
import org.dgrf.yamunakinare.dto.ArticleTypeDTO;

/**
 *
 * @author bhaduri
 */
@Named(value = "articleTypeController")
@ViewScoped
public class ArticleTypeController implements Serializable {
    private List <ArticleTypeDTO> articleTypeDTOList;
    /**
     * Creates a new instance of ArticleTypeController
     */
    public ArticleTypeController() {
    }
    public void fillArticleTypeGrid () {
        ArticleTypeService articleTypeService = new ArticleTypeService();
        articleTypeDTOList = articleTypeService.getArticleTypeList();
    }
    public List<ArticleTypeDTO> getArticleTypeDTOList() {
        return articleTypeDTOList;
    }

    public void setArticleTypeDTOList(List<ArticleTypeDTO> articleTypeDTOList) {
        this.articleTypeDTOList = articleTypeDTOList;
    }
    
}
