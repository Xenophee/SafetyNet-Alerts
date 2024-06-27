package com.safetynet.alerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tinylog.Logger;

@SpringBootApplication
public class SafetyNetAlertsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SafetyNetAlertsApplication.class, args);
        Logger.info("Safetynet Alert starting !");
    }

}
