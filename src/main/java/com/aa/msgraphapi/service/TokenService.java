package com.aa.msgraphapi.service;

import com.aa.msgraphapi.model.Token;
import com.aa.msgraphapi.properties.MSConnectionProperties;

public interface TokenService
{
    Token getToken(MSConnectionProperties msConnectionProperties);
}
