package com.bmk.bmkbookings.controller;

import com.bmk.bmkbookings.exception.UnauthorizedUserException;
import com.bmk.bmkbookings.mapper.BookingsMapper;
import com.bmk.bmkbookings.response.out.GenericResponse;
import com.bmk.bmkbookings.service.MerchantUserRelationService;
import com.bmk.bmkbookings.util.RestClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("relations")
@RestController
@Log4j2
public class MerchantUserRelationController {

    private static MerchantUserRelationService merchantUserRelationService;
    private static RestClient restClient;

    public MerchantUserRelationController(MerchantUserRelationService merchantUserRelationService, RestClient restClient) {
        this.merchantUserRelationService = merchantUserRelationService;
        this.restClient = restClient;
    }

    @GetMapping("merchant")
    public ResponseEntity getRelations(@RequestHeader String token) throws UnauthorizedUserException {
        Long merchantId = restClient.authorize(token, "gamma");
        return ResponseEntity.ok(new GenericResponse("200", "Success", merchantUserRelationService.getRelationships(merchantId)));
    }
}