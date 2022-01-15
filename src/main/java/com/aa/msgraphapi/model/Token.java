package com.aa.msgraphapi.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Token
{
    private long expiryTime;

    private String value;
}
