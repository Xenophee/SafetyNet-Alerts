package com.safetynet.alerts.util;

import java.time.LocalDate;


public class BirthdateUtil {

    public static boolean isChild(LocalDate birthdate) {
        int age = getAge(birthdate);

        return age <= 18;
    }

    public static int getAge(LocalDate birthdate) {
        LocalDate today = LocalDate.now();
        int age = today.getYear() - birthdate.getYear();

        // Si l'anniversaire de cette année n'est pas encore passé, soustraire un an
        if (today.getMonthValue() < birthdate.getMonthValue() ||
                (today.getMonthValue() == birthdate.getMonthValue() && today.getDayOfMonth() < birthdate.getDayOfMonth())) {
            age--;
        }

        return age;
    }
}
