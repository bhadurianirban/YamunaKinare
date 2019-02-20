/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dgrf.yamunakinare.ui.login;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.dgrf.cloud.client.DGRFTenantListClient;

import org.dgrf.cloud.dto.TenantDTO;
import org.dgrf.cloud.dto.UserAuthDTO;
import org.dgrf.cloud.client.UserAuthClient;
import org.dgrf.cloud.response.DGRFResponseCode;

import org.dgrf.cloud.dto.DGRFCloudAuthCredentials;
import org.dgrf.cms.constants.AppearenceMeta;
import org.dgrf.cms.constants.CMSConstants;
import org.dgrf.cms.dto.TermInstanceDTO;
import org.dgrf.cms.constants.MediaMeta;

/**
 *
 * @author dgrf-vi
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    /**
     * Creates a new instance of LoginController
     */
    private int tenantID;
    private String tenantName;
    private int productID;
    private String userID;
    private String password;
    private UserAuthDTO userAuthDTO;
    private String selectedTheme;
    private String productName;
    private String selectedHomeImg;
    private String productCaption;
    private String homeScreenLogo;
    private String iconAndLogo;

    private Map<String, Object> tenantMap;

    public LoginController() {
    }

    @PostConstruct
    private void init() {
        userAuthDTO = new UserAuthDTO();
        userAuthDTO.setUserId(null);

    }

    public UserAuthDTO getUserAuthDTO() {
        return userAuthDTO;
    }

    public void setUserAuthDTO(UserAuthDTO userAuthDTO) {
        this.userAuthDTO = userAuthDTO;
    }

    public void fillLOginFormValues() {
        DGRFTenantListClient dgrftlc = new DGRFTenantListClient();
        List<TenantDTO> tenantDTOs = dgrftlc.getTenantList(productID);
        tenantMap = new HashMap<>();
        for (TenantDTO tdto : tenantDTOs) {
            tenantMap.put(tdto.getName(), tdto.getTenantId());
        }
    }

    public String login() {
        
        UserAuthClient uac = new UserAuthClient();
        userAuthDTO.setUserId(userID);
        userAuthDTO.setPassword(password);
        userAuthDTO.setProductId(productID);
        userAuthDTO.setTenantId(tenantID);
        userAuthDTO = uac.authenticateUser(userAuthDTO);
        FacesMessage message;
        
        if (userAuthDTO.getResponseCode() == DGRFResponseCode.SUCCESS) {
        
            setAuthCredentials(userAuthDTO);
            
            return "Landing";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", userAuthDTO.getResponseMessage());
            FacesContext f = FacesContext.getCurrentInstance();
            f.addMessage(null, message);
            return "Login";
        }

    }

    public String logout() {
        userAuthDTO.setUserId(null);
        userID = null;
        password = null;
        return "/Login?faces-redirect=true";
    }

    public Map<String, Object> getTenantMap() {
        return tenantMap;
    }

    public void setTenantMap(Map<String, Object> tenantMap) {
        this.tenantMap = tenantMap;
    }

    public int getTenantID() {
        return tenantID;
    }

    public void setTenantID(int tenantID) {
        this.tenantID = tenantID;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String selectTenant() {
        return "welcome?faces-redirect=true&tenant=" + tenantID;
    }

    public String moveToDefaultHost() {
        productID = 5;

        DGRFTenantListClient dgrftlc = new DGRFTenantListClient();
        List<TenantDTO> tenantDTOs = dgrftlc.getTenantList(productID);
        if (tenantDTOs == null) {
            return "access";
        }

        if (tenantID == 0) {
            tenantID = 1;
        }
        List<TenantDTO> tenantDTOMatched = tenantDTOs.stream().filter(tenant -> tenant.getTenantId() == tenantID).collect(Collectors.toList());
        if (tenantDTOMatched.isEmpty()) {
            return "access";
        }
        tenantName = tenantDTOMatched.get(0).getName();
        setSelectedThemeCss(tenantID, productID);
        //this.selectedTheme = "css/term-green.css";
        return "index";

    }

    private void setSelectedThemeCss(int tenantID, int productID) {
        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String contextPath = servletContext.getContextPath();

        StringBuilder sb = new StringBuilder(contextPath);
        sb.deleteCharAt(0);

        
        productName = sb.toString();
        productCaption = "Time you enjoy wasting is not wasted time.";
        selectedTheme = "css/term-blue.css";
        homeScreenLogo = contextPath + "/faces/javax.faces.resource/yamuna/images/default-butterfly.png";
        iconAndLogo = contextPath + "/faces/javax.faces.resource/yamuna/images/dgrflogo.png";
        selectedHomeImg = contextPath + "/faces/javax.faces.resource/yamuna/images/dgrf-default-home-img.jpg";
        setAuthCredentials();



    }

    private void setAuthCredentials(UserAuthDTO userAuthDTO) {
        DGRFCloudAuthCredentials authCredentials = new DGRFCloudAuthCredentials();
        authCredentials.setProductId(productID);
        authCredentials.setTenantId(tenantID);
        authCredentials.setUserId(userAuthDTO.getUserId());
        authCredentials.setPassword(userAuthDTO.getPassword());
        authCredentials.setRoleId(userAuthDTO.getRoleId());
        CMSClientAuthCredentialValue.AUTH_CREDENTIALS = authCredentials;

    }
    private void setAuthCredentials() {
        DGRFCloudAuthCredentials authCredentials = new DGRFCloudAuthCredentials();
        authCredentials.setProductId(productID);
        authCredentials.setTenantId(tenantID);
        CMSClientAuthCredentialValue.AUTH_CREDENTIALS = authCredentials;

    }    

    public void goToUserRegister() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String ipAddress = request.getHeader("Host");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String urlPrefix = request.getScheme();
        
        String redirectUrl = urlPrefix + "://"+ipAddress+"/" + "DGRFCloud/faces/UserRegister.xhtml?tenant="+tenantID+"&product="+productID;
        //System.out.println("IP redirectUrl:" +redirectUrl);
         
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        try {
            externalContext.redirect(redirectUrl);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getSelectedTheme() {
        return selectedTheme;
    }

    public void setSelectedTheme(String selectedTheme) {
        this.selectedTheme = selectedTheme;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSelectedHomeImg() {
        return selectedHomeImg;
    }

    public void setSelectedHomeImg(String selectedHomeImg) {
        this.selectedHomeImg = selectedHomeImg;
    }

    public String getProductCaption() {
        return productCaption;
    }

    public void setProductCaption(String productCaption) {
        this.productCaption = productCaption;
    }

    public String getHomeScreenLogo() {
        return homeScreenLogo;
    }

    public void setHomeScreenLogo(String homeScreenLogo) {
        this.homeScreenLogo = homeScreenLogo;
    }

    public String getIconAndLogo() {
        return iconAndLogo;
    }

    public void setIconAndLogo(String iconAndLogo) {
        this.iconAndLogo = iconAndLogo;
    }

}
