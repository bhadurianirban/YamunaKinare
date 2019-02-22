/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.ui.login;

import org.dgrf.cloud.client.DataConnClient;
import org.dgrf.cloud.dto.DataConnDTO;
import org.dgrf.cloud.dto.DGRFCloudAuthCredentials;

/**
 *
 * @author bhaduri
 */
public class CMSAuthentication {

    public static DataConnDTO authenticateSubcription(DGRFCloudAuthCredentials authCredentials) {
        int productId = authCredentials.getProductId();
        int tenantId = authCredentials.getTenantId();
        DataConnClient dataConnClient = new DataConnClient();
        DataConnDTO dataConnDTO = new DataConnDTO();
        dataConnDTO.setCloudAuthCredentials(authCredentials);
        dataConnDTO = dataConnClient.getDataConnParams(dataConnDTO);
        return dataConnDTO;
    }
}
