package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FireStationQueryService {

    public List<Person> getPersonsStationCoverage(int stationNumber) {
        return null;
    }

    public List<Person> getHomesByStations(List<Integer> stations) {
        return null;
    }

    public List<Person> getPersonsPhonesByStation(int stationNumber) {
        return null;
    }

    public List<Person> getPersonsAndStationByAddress(String address) {
        return null;
    }
}