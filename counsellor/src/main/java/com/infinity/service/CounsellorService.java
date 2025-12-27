package com.infinity.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infinity.repository.CounsellorRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class CounsellorService {

    @Autowired
    private CounsellorRepository counsellorRepository;

    public Map<String, Object> registerCounsellor(Map<String, Object> payload) {

        return counsellorRepository.registerCounsellor(payload);
    }

     public Map<String, Object> addEnquiry(HttpServletRequest request, Map<String, Object> payload) {
        return counsellorRepository.addEnquiry(request, payload);

    }

    public Map<String, Object> getDropdownsData() {
        return counsellorRepository.getDropDownsData();
    }

    public Map<String, Object> getDashBoardData(HttpServletRequest request) {
        return counsellorRepository.getDashBoardData(request);
    }

    public Map<String, Object> getEnquiries(HttpServletRequest request, Map<String, Object> filters) {
        return counsellorRepository.getEnquiries(request, filters);
    }
}