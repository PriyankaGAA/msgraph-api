package com.aa.msgraphapi.service.impl;

import com.aa.msgraphapi.request.GraphTransactionRequest;
import com.aa.msgraphapi.service.GraphService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.aa.msgraphapi.model.Token;
import com.aa.msgraphapi.properties.MSConnectionProperties;

import java.util.Collections;

@Service
@Slf4j
@Getter
@Setter
@Scope("prototype")
public class GraphServiceImpl implements GraphService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenServiceImpl tokenService;

    private Token token;

    private MSConnectionProperties msConnectionProperties;

    @Override
    public ResponseEntity<String> invoke(GraphTransactionRequest graphTransactionRequest) throws RestClientException {
        try
        {
            HttpMethod verb = graphTransactionRequest.getHttpMethod();

            if (StringUtils.isEmpty(verb))
            {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "No HTTP Method defined.");
            }

            token = tokenService.getToken(msConnectionProperties);

            if (token == null)
            {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Token retrieval failed.");
            }

            HttpEntity<String> requestEntity;
            HttpHeaders headers = graphTransactionRequest.getHttpHeaders();

            if (headers == null)
            {
                headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.setContentType(MediaType.APPLICATION_JSON);
            }
            headers.add("Connection", "keep-alive");
            headers.set("Authorization", "Bearer " + token.getValue());

            if (StringUtils.isEmpty(graphTransactionRequest.getBody()))
            {
                requestEntity = new HttpEntity<>(headers);
            }
            else
            {
                requestEntity = new HttpEntity<>(graphTransactionRequest.getBody(), headers);
            }

            return restTemplate.exchange(graphTransactionRequest.getUri(), verb, requestEntity, String.class);

        } catch (HttpClientErrorException e)
        {
            log.debug("invoke() : HttpClientErrorException Status Code : " + e.getRawStatusCode() + " : Error Response " + e.getResponseBodyAsString() + " Request = " + graphTransactionRequest);

            throw e;
        } catch (RestClientException e)
        {
            log.debug("invoke() : RestClientException : " + e.getMessage() + " Request : " + graphTransactionRequest);
            log.debug("invoke() : RestClientException ", e);
            throw e;
        } catch (Exception e)
        {
            log.debug("invoke() : Exception : " + e.getMessage() + " Request = " + graphTransactionRequest);
            log.debug("invoke() : Exception ", e);
            throw new HttpClientErrorException(HttpStatus.BAD_GATEWAY, "Something went wrong, " + e.getMessage());
        }
    }
}
