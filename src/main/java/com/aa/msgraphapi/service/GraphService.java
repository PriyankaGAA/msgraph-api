package com.aa.msgraphapi.service;

import com.aa.msgraphapi.request.GraphTransactionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

public interface GraphService {
    ResponseEntity<String> invoke(GraphTransactionRequest graphTransactionRequest) throws RestClientException;
}
