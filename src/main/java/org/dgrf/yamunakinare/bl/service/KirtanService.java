/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.bl.service;

import java.util.List;
import java.util.stream.Collectors;
import org.dgrf.yamunakinare.db.DAO.DatabaseConnection;
import org.dgrf.yamunakinare.db.DAO.KirtanDAO;
import org.dgrf.yamunakinare.db.entities.Kirtan;
import org.dgrf.yamunakinare.dto.KirtanDTO;

/**
 *
 * @author dgrfi
 */
public class KirtanService {
    public List<KirtanDTO> getListOfAllKirtans () {
        KirtanDAO kirtanDAO = new KirtanDAO(DatabaseConnection.EMF);
        List<Kirtan> kirtanListAll = kirtanDAO.findKirtanEntities();
        List<KirtanDTO> kirtanDTOListAll = kirtanListAll.stream().map (kirtan-> {
            KirtanDTO kirtanDTO = new KirtanDTO();
            kirtanDTO.setId(kirtan.getId());
            kirtanDTO.setName(kirtan.getName());
            kirtanDTO.setContent(kirtan.getContent());
            return kirtanDTO;
        }).collect(Collectors.toList());
        return kirtanDTOListAll;
    }
    
}
