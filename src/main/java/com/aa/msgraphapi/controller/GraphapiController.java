package com.aa.msgraphapi.controller;

import com.aa.msgraphapi.service.impl.GraphServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import com.aa.msgraphapi.request.GraphTransactionRequest;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class GraphapiController {

    @Autowired
    GraphServiceImpl graphServiceImpl;

    @GetMapping(value = "/health")
    public String health()
    {
        return "Graph API application is UP!!!";
    }

   @GetMapping(value = "/getTeam/{id}")
    public ResponseEntity<String> getTeam(@PathVariable("id") String id){
       ResponseEntity<String> response = null;
        try
        {
            log.info("getTeam: "+id);
            GraphTransactionRequest graphTransactionRequest = new GraphTransactionRequest();
            graphTransactionRequest.setHttpMethod(HttpMethod.GET);
            /*String uri = String.format(baseUrl.concat("users/%s").concat(usrPropQueryStr), id);*/
            String uri = String.format("https://graph.microsoft.com/v1.0/teams/%s", id);
            log.info("Fetching team with url : '{}' ", uri);
            graphTransactionRequest.setUri(uri);
            log.info("graphTransactionRequest : '{}'",graphTransactionRequest);

            response = graphServiceImpl.invoke(graphTransactionRequest);

            if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() <= 300)
            {
               /* userInfo = new ObjectMapper().readValue(response.getBody(), UserInfo.class);*/
                System.out.println(response.getBody());
                return response;
            }
            else
            {
               /* userInfo = retry(id, retryCount);*/
                return null;
            }
        } catch (RestClientException e)
        {
            log.info("Exception occurred while fetching team  '{}' :  '{}' ", id, e.getStackTrace());
            if (e instanceof HttpClientErrorException)
            {
                log.info("HttpClientErrorException while fetching team  '{}' : '{}' ", id, ((HttpClientErrorException) e).getResponseBodyAsString());
                int statusCode = ((HttpClientErrorException) e).getRawStatusCode();
                if (statusCode == 404)
                {
                    return null;
                }
                else
                {
                   /* userInfo = retry(id, retryCount);*/
                }
            }
            else
            {
                /*userInfo = retry(id, retryCount);*/
            }
        } /*catch (JsonProcessingException e)
        {
            log.info("JsonProcessingException while fetching user  '{}' : '{}' ", id, e.getMessage());

        }*/
        return response;
    }

    @DeleteMapping("/deleteTeam")
    public List<String> deleteTeam(@RequestBody List<String> idLst) {
        List<String> resultResponse = new ArrayList<String>();
        idLst.forEach(teamId-> {
        try
        {
            log.info("deleteTeam: "+teamId);
            GraphTransactionRequest graphTransactionRequest = new GraphTransactionRequest();
            graphTransactionRequest.setHttpMethod(HttpMethod.DELETE);
            /*String uri = String.format(baseUrl.concat("users/%s").concat(usrPropQueryStr), id);*/

                String result = "false";
                String uri = String.format("https://graph.microsoft.com/v1.0/groups/%s", teamId);
                log.info("Delete team with url : '{}' ", uri);
                graphTransactionRequest.setUri(uri);
                log.info("graphTransactionRequest : '{}'", graphTransactionRequest);

                ResponseEntity<String> response = graphServiceImpl.invoke(graphTransactionRequest);

                if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() <= 300) {
                    /* userInfo = new ObjectMapper().readValue(response.getBody(), UserInfo.class);*/
                    System.out.println(response.getBody());
                    if (response.getStatusCodeValue() == 204) {
                        System.out.println(teamId + " deleted successfully");
                        resultResponse.add(" teamId: "+teamId+" "+" -deleted successfully");
                    }
                } else {
                    /* userInfo = retry(id, retryCount);*/
                    //return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
                    System.out.println(teamId + "not deleted");
                    resultResponse.add(" teamId: "+teamId+" "+" -not deleted");
                }
        } catch (RestClientException e)
        {
            //log.info("Exception occurred while deleting team  '{}' :  '{}' ", id, e.getStackTrace());
            if (e instanceof HttpClientErrorException)
            {
                //log.info("HttpClientErrorException while deleting team  '{}' : '{}' ", id, ((HttpClientErrorException) e).getResponseBodyAsString());
                int statusCode = ((HttpClientErrorException) e).getRawStatusCode();
                if (statusCode == 404)
                {
                    resultResponse.add(" teamId: "+teamId+" "+" not found.");
                }
                else
                {
                    /* userInfo = retry(id, retryCount);*/
                }
            }
            else
            {
                /*userInfo = retry(id, retryCount);*/
            }
        } /*catch (JsonProcessingException e)
        {
            log.info("JsonProcessingException while fetching user  '{}' : '{}' ", id, e.getMessage());

        }*/catch (Exception e) {
            //return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        });

        return resultResponse;
    }
}
