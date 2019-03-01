/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.ui.kirtan;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.dgrf.yamunakinare.dto.KirtanDTO;

/**
 *
 * @author dgrfi
 */
@Named(value = "kirtanListController")
@ViewScoped
public class KirtanListController implements Serializable{
    private List<KirtanDTO> kirtanList;
    /**
     * Creates a new instance of KirtanListController
     */
    public KirtanListController() {
    }
    public void fillGrid() {
        
    }

    public List<KirtanDTO> getKirtanList() {
        return kirtanList;
    }

    public void setKirtanList(List<KirtanDTO> kirtanList) {
        this.kirtanList = kirtanList;
    }
    
}
