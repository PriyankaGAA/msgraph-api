package com.aa.msgraphapi.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MSConnectionProperties
{

    private String tenantId;

    private String appId;

    private String secret;

    private String defaultScope;

    private String tag;

}
