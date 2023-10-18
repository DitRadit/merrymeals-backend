package com.lithan.merrymeals.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;



@Configuration
public class PaypalConfig {

    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;



    @Bean
    public Map<String, String> paypalSdkConfig(){
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode); // Use "mode" as the key, not mode variable
        return configMap;
    }
    

    @Bean
    public OAuthTokenCredential oAuthTokenCredential(){
        return new OAuthTokenCredential(clientId, clientSecret,paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() {
        try {
            APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
            context.setConfigurationMap(paypalSdkConfig());
            return context;
        } catch (PayPalRESTException e) {
            // Handle the exception, log it, or throw a different exception
            throw new RuntimeException("Error creating APIContext", e);
        }
    }
    


}
