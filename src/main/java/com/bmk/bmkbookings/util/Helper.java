package com.bmk.bmkbookings.util;

import com.bmk.bmkbookings.bo.Invoice;
import com.bmk.bmkbookings.exception.ServiceNotAvailableException;
import com.bmk.bmkbookings.response.in.PortfolioItem;
import com.bmk.bmkbookings.response.in.PortfolioResponse;
import com.bmk.bmkbookings.response.in.Service;
import com.bmk.bmkbookings.response.in.User;
import com.bmk.bmkbookings.response.out.InvoiceItem;
import com.bmk.bmkbookings.response.out.MerchantResponse;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class Helper {

    public static Invoice getInvoice(PortfolioResponse portfolioResponse, String serviceIdCsv) throws ServiceNotAvailableException {
       log.info(portfolioResponse.getData());
       log.info(serviceIdCsv);
        String[] services = serviceIdCsv.split(",");
        List<InvoiceItem> list = new ArrayList<>();
        double total=0.0;
        for (String service: services) {
            PortfolioItem portfolioItem = portfolioResponse.getData().get(Integer.parseInt(service));
            if(portfolioItem == null)   throw new ServiceNotAvailableException(Long.parseLong(service));
            double cost = portfolioItem.getPrice()*(100-portfolioItem.getDiscountPercentage())/100;
            total = total + cost;
            log.info("adding"+portfolioItem.getService().getService());
            list.add(new InvoiceItem(portfolioItem.getService().getService(), cost));
        }
        Invoice invoice = new Invoice();
        invoice.setTotalAmount(total);
        invoice.setInvoiceItems(list);
        return invoice;
    }

    public static Map<Long, Service> convertServicesListToMap(Service[] list){
        Map<Long, Service> map = new HashMap<>();
        for(Service service: list){
            map.put(service.getServiceId(), service);
        }
        log.info(map);
        return map;
    }

    public static Map<Long, String> convertUserListToMap(User[] list) {
        Map<Long, String> map = new HashMap<>();
        for(User user: list) {
            map.put(user.getStaticUserId(), user.getName());
        }
        log.info(map);
        return map;
    }

    public static Map<Long, MerchantResponse> convertMerchantListToMap(MerchantResponse[] merchantResponses) {
        Map<Long, MerchantResponse> map = new HashMap<>();
        for(MerchantResponse merchantResponse: merchantResponses) {
                map.put(merchantResponse.getMerchantId(), merchantResponse);
        }
        log.info(map);
        return map;
    }
}
