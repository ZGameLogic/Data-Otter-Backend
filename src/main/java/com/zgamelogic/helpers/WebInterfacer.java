package com.zgamelogic.helpers;

import com.zgamelogic.data.WebMonitor;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public abstract class WebInterfacer {

    public static boolean pingWeb(WebMonitor webMonitor){
        final String url = webMonitor.getUrl()  + ":" + webMonitor.getPort();
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(new URI(url), String.class);
            webMonitor.setStatus(response.contains(webMonitor.getRegex()));
            return true;
        } catch (Exception e) {
            webMonitor.setStatus(false);
            return false;
        }
    }
}
