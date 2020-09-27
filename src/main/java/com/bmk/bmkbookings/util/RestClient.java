package com.bmk.bmkbookings.util;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.bo.Notification;
import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.request.out.FcmRequest;
import com.bmk.bmkbookings.response.in.AuthResponse;
import com.bmk.bmkbookings.response.in.DeviceIdResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final static String fcmKey = System.getenv("fcmKey");

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

    public String getDeviceId(Long userId){
            logger.info("Calling get deviceId service");
            String baseUrl = "https://bmkauth.herokuapp.com/api/v1/user/deviceId?userId=".concat(userId.toString());
            logger.info("baseUrl:"+ baseUrl);
            HttpHeaders headers = getHttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
            DeviceIdResponse deviceIdResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, DeviceIdResponse.class).getBody();
            if(!deviceIdResponse.getResponseCode().equals("200")) return null;
            String deviceId = deviceIdResponse.getDeviceId();
            logger.info("deviceId:"+ deviceId);
            return deviceId;
    }

    public void sendNotification(Booking booking) throws JsonProcessingException {
        //try {
            logger.info("Calling firebase cloud messaging");
            Notification notification = new Notification("New Booking", new ObjectMapper().writeValueAsString(booking), null);
            FcmRequest fcmRequest = new FcmRequest(getDeviceId(booking.getMerchantId()), null, notification);
            String baseUrl = "https://fcm.googleapis.com/fcm/send";
            HttpHeaders headers = getHttpHeaders();
            headers.set("Authorization", "Bearer "+fcmKey);
            HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(fcmRequest), headers);
            Object object = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, Object.class).getBody();
            logger.info("fcm connection success"+ object);
        /*} catch (Exception e){
            logger.info("Something went wrong in fcm");
        }*/
    }

    public static HttpHeaders getHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}