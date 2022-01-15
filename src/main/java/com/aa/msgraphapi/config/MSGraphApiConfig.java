package com.aa.msgraphapi.config;

import  com.aa.msgraphapi.properties.MSConnectionProperties;
//import com.aa.cme.teams.graph.txn.util.Constants;
//import com.microsoft.azure.keyvault.KeyVaultClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@Slf4j
public class MSGraphApiConfig
{
    /*@Value("${azure.keyvault.uri}")
    private String keyVaultUrl;

    @Value("${azure.keyvault.enabled}")
    private boolean keyVaultEnabled;*/

    @Value("${app.id}")
    private String appId;

    @Value("${tenant.id}")
    private String tenantId;

    @Value("${secret}")
    private String secret;

    @Value("${defaultScope}")
    private String defaultScope;

    @Value("${tag}")
    private String tag;

   /* @Autowired
    KeyVaultClient keyVaultClient;*/
//
//    @Autowired
//    SecretClient secretClient;

    public MSConnectionProperties getDefaultMsConnectionProperties()
    {
	/*if (keyVaultEnabled)
	{
	    if (!StringUtils.isEmpty(System.getProperty("AZURE_KEY_VAULT_URI")))
	    {
		log.info("Getting key Vault URI From Environment through System Property");
		keyVaultUrl = System.getProperty("AZURE_KEY_VAULT_URI");
	    }

	    if (StringUtils.isEmpty(keyVaultUrl))
	    {
		log.info("Getting key Vault URI From Environment through System Env");
		keyVaultUrl = System.getenv("AZURE_KEY_VAULT_URI");
	    }

	    appId = keyVaultClient.getSecret(keyVaultUrl,Constants.graphAppKvKey).value();
	    secret = keyVaultClient.getSecret(keyVaultUrl, Constants.graphAppSecretKvKey).value();
	}*/

	MSConnectionProperties msConnectionProperties = new MSConnectionProperties();

	if (StringUtils.isEmpty(appId))
	{
	    appId = System.getenv("app.id");
	}
	if (StringUtils.isEmpty(appId))
	{
	    tenantId = System.getenv("tenant.id");
	}
	if (StringUtils.isEmpty(appId))
	{
	    secret = System.getenv("secret");
	}

	if (StringUtils.isEmpty(defaultScope))
	{
	    defaultScope = System.getenv("defaultScope");
	}

	if (StringUtils.isEmpty(tag))
	{
	    tag = System.getenv("tag");
	}

	if (StringUtils.isEmpty(appId))
	{
	    appId = System.getProperty("app.id");
	}
	if (StringUtils.isEmpty(appId))
	{
	    tenantId = System.getProperty("tenant.id");
	}
	if (StringUtils.isEmpty(appId))
	{
	    secret = System.getProperty("secret");
	}
	if (StringUtils.isEmpty(defaultScope))
	{
	    defaultScope = System.getProperty("defaultScope");
	}

	if (StringUtils.isEmpty(tag))
	{
	    tag = System.getProperty("tag");
	}
	log.debug(" values {}, {}, {}, {}", appId, tenantId, secret, defaultScope);

	if (StringUtils.isEmpty(appId))
	{
	    throw new IllegalStateException("Couldn't find the application id, please add the value to configuration settings.");
	}

	if (StringUtils.isEmpty(tenantId))
	{
	    throw new IllegalStateException("Couldn't find the tenant id, please add the value to configuration settings.");
	}

	if (StringUtils.isEmpty(secret))
	{
	    throw new IllegalStateException("Couldn't find the secret token, please add the value to configuration settings.");
	}
	if (StringUtils.isEmpty(tag))
	{
	    throw new IllegalStateException("Couldn't find the tag , please add the value to configuration settings.");
	}
	msConnectionProperties.setAppId(appId);
	msConnectionProperties.setSecret(secret);
	msConnectionProperties.setTenantId(tenantId);
	msConnectionProperties.setDefaultScope(defaultScope);
	msConnectionProperties.setTag(tag);
	return msConnectionProperties;
    }

}
