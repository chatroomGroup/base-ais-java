package com.cai.ais.config.dlx;

import com.cai.ais.config.AisMessage;
import com.cai.ais.config.AisService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;


public class DefaultDlxConsumer extends DLXConsumer {

    @Override
    public Object process(AisMessage msg) {
        System.out.println("dlx-" + msg.getBody());
        return "dlx-" + msg.getBody();
    }
}
