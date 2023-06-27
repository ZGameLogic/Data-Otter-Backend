package com.zgamelogic.helpers;

import com.zgamelogic.data.APIMonitor;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public abstract class APIInterfacer {

    public static boolean pingAPI(APIMonitor apiMonitor){
        apiMonitor.setCompletedInMilliseconds(System.currentTimeMillis());
        final String url = apiMonitor.getUrl()  + ":" + apiMonitor.getPort() + "/" + apiMonitor.getHealthCheckUrl();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(new URI(url), String.class);
            apiMonitor.setStatus(response.toLowerCase().contains("health"));
            apiMonitor.setCompletedInMilliseconds(System.currentTimeMillis() - apiMonitor.getCompletedInMilliseconds());
            return true;
        } catch (Exception e) {
            apiMonitor.setStatus(false);
            apiMonitor.setCompletedInMilliseconds(System.currentTimeMillis() - apiMonitor.getCompletedInMilliseconds());
            return false;
        }
    }

}
