package OauthAPI;


public interface OauthUser {
	
    //void setaccess_token(String ac_token);
    String getaccess_token();
    
    //void setuserName(String name);
    String getuserName();
    String getUID();
    //void setImgURL(String url);
    String getImgURL();
    //void setEmail(String email);
    String getEmail();
    String getProvider();
    String advanceOauthProvider();   	
    
    //Boolean getloginstatus();
    
    //void connectOauthProvider();
    //void parseCode(String code);
    void logout();
    
}
