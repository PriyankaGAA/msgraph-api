package com.aa.msgraphapi.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@ToString
public class GraphTransactionRequest
{
    private String uri;
    private HttpMethod httpMethod;
    private HttpHeaders httpHeaders;
    private String body;

}
