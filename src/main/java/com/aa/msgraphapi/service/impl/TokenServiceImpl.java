package com.aa.msgraphapi.service.impl;

import com.aa.msgraphapi.model.Token;
import com.aa.msgraphapi.properties.MSConnectionProperties;
import com.aa.msgraphapi.service.TokenService;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.aa.msgraphapi.config.MSGraphApiConfig;
import com.aa.msgraphapi.config.TokenCacheConfig;
import org.springframework.stereotype.Service;
import java.time.Instant;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

@Service
@Slf4j
@Getter
@Setter
public class TokenServiceImpl implements TokenService {

    @Autowired
    MSGraphApiConfig msGraphApiConfig;

    @Autowired CacheManager ehCacheManager;

    private static final int MAX_RETRY = 3;

//    private MSConnectionProperties  msConnectionProperties;

    private Cache<String, Token> cacheToken;

    @Override
    public Token getToken(MSConnectionProperties msConnectionProperties) {
       try
        {
            if(cacheToken == null)
            {
                cacheToken = ehCacheManager.getCache(TokenCacheConfig.TOKEN, String.class, Token.class);
            }
            if (null == msConnectionProperties)
            {
                log.info("Using default connection properties {} ", msGraphApiConfig.getDefaultMsConnectionProperties().getTag());
                msConnectionProperties = msGraphApiConfig.getDefaultMsConnectionProperties();
            }

            Token token =cacheToken.get(msConnectionProperties.getAppId());
            if (null == token)
            {
                log.info("Getting new Token for {} ",msConnectionProperties.getTag());
                token = new Token();
            }
            if (token.getExpiryTime() < 1 || Instant.ofEpochMilli(token.getExpiryTime()).isBefore(Instant.now()))
            {
                log.info("getToken()  : Fetching token for  {} ",msConnectionProperties.getTag());
                authorise(msConnectionProperties, token,0);
                if (StringUtils.isEmpty(token.getValue())) {
                    log.info("getToken()  : Retrieval Failed for {} ", msConnectionProperties.getTag());
                    return null;
                }
                if (cacheToken.containsKey((msConnectionProperties.getAppId())))
                {
                    log.info("getToken()  : Updating cache token for {} ", msConnectionProperties.getTag());
                    cacheToken.replace(msConnectionProperties.getAppId(), token);
                }
                else
                {
                    log.info("getToken()  : Added new cache token for {} ", msConnectionProperties.getTag());
                    cacheToken.put(msConnectionProperties.getAppId(), token);
                }
                log.info("getToken()  : Retrieved Successfully from MS Graph and updated Cache for {} ", msConnectionProperties.getTag());
            }
            else
            {
                log.info("Re-using previous token for {} until expiry {} ",msConnectionProperties.getTag(), Instant.ofEpochMilli(token.getExpiryTime()).toString());
            }

            return token;
        }
        catch(Exception e)
        {
            log.info( "getToken() : Exception : "+ e.getMessage());
            log.error( "getToken() : Exception : ", e);
        }
        return null;
    }

    private void authorise(MSConnectionProperties msConnectionProperties,Token token, int retryCount)
    {
        try
        {
            String APP_ID = msConnectionProperties.getAppId();
            String TENANT_ID = msConnectionProperties.getTenantId();
            String CONFIDENTIAL_CLIENT_SECRET = msConnectionProperties.getSecret();
            log.info("authorise() : begin : for {} ", msConnectionProperties.getTag());
            log.debug( "authorise() : begin : '{}' : '{}' " , APP_ID , TENANT_ID);
            String GRAPH_DEFAULT_SCOPE = msConnectionProperties.getDefaultScope();
            ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(Collections.singleton(GRAPH_DEFAULT_SCOPE)).build();

            String TENANT_SPECIFIC_AUTHORITY = "https://login.microsoftonline.com/";
            ConfidentialClientApplication app = ConfidentialClientApplication.builder(APP_ID, ClientCredentialFactory.createFromSecret(CONFIDENTIAL_CLIENT_SECRET)).authority(TENANT_SPECIFIC_AUTHORITY + TENANT_ID + "/").build();

            CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);

            BiConsumer<IAuthenticationResult, Throwable> processAuthResult = (res, ex) ->
            {
                if (ex != null)
                {
                    log.info( "authorise()! We have an exception - {} for {} " , ex.getMessage(), msConnectionProperties.getTag());
                    if (retryCount < MAX_RETRY)
                    {
                        authorise(msConnectionProperties, token, retryCount);
                    }
                }
            };
            future.whenCompleteAsync(processAuthResult);
            future.join();
            token.setValue(future.get().accessToken());
            token.setExpiryTime((future.get().expiresOnDate().toInstant().toEpochMilli() - 60 * 1000));
            log.info( "authorise() Token Expires for {} , on : {} " ,  msConnectionProperties.getTag(),  future.get().expiresOnDate());
            log.info( "authorise() complete for {} ", msConnectionProperties.getTag());
        }
        catch (MalformedURLException | InterruptedException | ExecutionException e)
        {
            log.info("Exception : {} when authorizing token for {} ", e.getMessage(), msConnectionProperties.getTag());
            log.error("Exception when authorizing token for {}, {} ", msConnectionProperties.getTag(), e);
            if (retryCount < MAX_RETRY)
            {

                log.info("retry() : count : "+retryCount+" : After Exception : "+e.getMessage());
                authorise(msConnectionProperties, token, retryCount);
            }
        }

    }
}
