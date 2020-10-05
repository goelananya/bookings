package com.bmk.bmkbookings.util;

import com.bmk.bmkbookings.bo.Booking;
import com.bmk.bmkbookings.bo.Notification;
import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.request.out.FcmRequest;
import com.bmk.bmkbookings.response.in.AuthResponse;
import com.bmk.bmkbookings.response.in.DeviceIdResponse;
import com.bmk.bmkbookings.response.in.PortfolioResponse;
import com.bmk.bmkbookings.response.in.RazorpayCreateOrderId;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class RestClient {
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
    private final static String fcmKey = System.getenv("fcmKey");
    private static final String razorPayKey = System.getenv("rpkey");
    private static final String razorPaySecret = System.getenv("rpSec");
    private static final String superuserToken = System.getenv("superuserToken");

    @Autowired
    public RestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Long authorize(String jwt, String apiType) throws UnauthorizedUserException {
        try {
            logger.info("Calling authorize service");
            String baseUrl = "https://bmkauth.herokuapp.com/api/v1/user/authorize";
            HttpHeaders headers = getHttpHeaders();
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
            DeviceIdResponse deviceIdResponse;
            try {
                deviceIdResponse = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, DeviceIdResponse.class).getBody();
            } catch (HttpClientErrorException e){
                return null;
            }
            String deviceId = deviceIdResponse.getDeviceId();
            logger.info("deviceId:"+ deviceId);
            return deviceId;
    }

    public void sendBookingNotification(Booking booking) throws JsonProcessingException {
            logger.info("Calling firebase cloud messaging");
            String imageUrl = "https://images.pexels.com/photos/1319460/pexels-photo-1319460.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260";
            String deviceId = getDeviceId(booking.getMerchantId());
            if(deviceId==null) {
                logger.info("Unable to send notification as device id not registered for"+booking);
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put(booking.getBookingId().toString(), new ObjectMapper().writeValueAsString(booking));
            Notification notification = new Notification("New Booking", "Launch application to accept the booking", imageUrl);
            FcmRequest fcmRequest = new FcmRequest(deviceId, map, notification);
            String baseUrl = "https://fcm.googleapis.com/fcm/send";
            HttpHeaders headers = getHttpHeaders();
            headers.set("Authorization", "Bearer "+fcmKey);
            HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(fcmRequest), headers);
            Object object = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, Object.class).getBody();
            logger.info("fcm connection success"+ object);
    }

    public void sendStatusUpdateNotification(Booking booking, UserType toUserType) throws JsonProcessingException {
        logger.info("Calling firebase cloud messaging");
        String imageUrl = "https://images.pexels.com/photos/1319460/pexels-photo-1319460.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260";
        String deviceId = toUserType.equals(UserType.merchant)?getDeviceId(booking.getMerchantId()):getDeviceId(booking.getClientId());

        if(deviceId==null) {
            logger.info("Unable to send notification as device id not registered for"+booking);
            return;
        }
        Notification notification = new Notification("Booking Status Updated", "Status updated for booking id ".concat(booking.getBookingId().toString()), imageUrl);
        FcmRequest fcmRequest = new FcmRequest(deviceId, null, notification);
        String baseUrl = "https://fcm.googleapis.com/fcm/send";
        HttpHeaders headers = getHttpHeaders();
        headers.set("Authorization", "Bearer "+fcmKey);
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(fcmRequest), headers);
        Object object = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, Object.class).getBody();
        logger.info("fcm connection success"+ object);
    }

    public static HttpHeaders getHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public RazorpayCreateOrderId createOrder(int amount, Long orderId) {
        try {
            logger.info(amount+" "+orderId);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount); // amount in the smallest currency unit
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", orderId.toString());
            RazorpayClient razorpayClient = new RazorpayClient(razorPayKey, razorPaySecret);
            Order order = razorpayClient.Orders.create(orderRequest);
            System.out.println(order);
            return new ObjectMapper().readValue(order.toString(), RazorpayCreateOrderId.class);
        } catch (RazorpayException | JsonProcessingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public PortfolioResponse getPortfolio(Long merchantId) {
        HttpHeaders headers = getHttpHeaders();
        headers.set("token", superuserToken);
        String url = "https://bmkservicesendpoints.herokuapp.com/api/v1/portfolio?merchantId="+merchantId+"&internal=true";
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        PortfolioResponse portfolioResponse = restTemplate.exchange(url, HttpMethod.GET, entity, PortfolioResponse.class).getBody();
        logger.info(portfolioResponse.getMessage());
        return portfolioResponse;
    }
}