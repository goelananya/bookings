package com.bmk.bmkbookings.util;

import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.response.in.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class RestClient {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    @Autowired
    public RestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Long authorize(String jwt, String apiType) throws UnauthorizedUserException {
        try {
            logger.info("Calling authorize service");
            String baseUrl = "https://bmkauth.herokuapp.com/api/v1/user/authorize";
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("token", jwt);
            headers.set("apiType", apiType);
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            AuthResponse authResponse = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, AuthResponse.class).getBody();
            if(!authResponse.getResponseCode().equals("200")) throw new UnauthorizedUserException();
            return Long.parseLong(authResponse.getMessage().split(":")[1]);
        } catch (Exception e){
            throw new UnauthorizedUserException();
        }
    }
}