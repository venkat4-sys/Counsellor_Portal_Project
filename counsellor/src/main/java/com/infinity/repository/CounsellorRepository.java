package com.infinity.repository;

import java.util.HashMap;
import java.util.Map;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.infinity.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Repository
public class CounsellorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Map<String, Object> findUserByEmail(String email) {

        String sql = "SELECT * FROM COUNSELLOR WHERE email = ?";
        try {
            return jdbcTemplate.queryForMap(sql, email);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> registerCounsellor(Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        String query = """
                INSERT INTO COUNSELLOR (name, email, password, phnum)
                VALUES (:name, :email, :password, :phnum)
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", payload.get("name"));
        params.addValue("email", payload.get("email"));
        params.addValue("password", payload.get("password"));
        params.addValue("phnum", payload.get("phnum"));

        try {
            // Change this line right here:
            int i = namedParameterJdbcTemplate.update(query, params);

            if (i > 0) {

                response.put("message", "Counsellor registered successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "FAILURE");
            response.put("message", e.getMessage());
        }
        return response;
    }

    public Map<String, Object> addEnquiry(HttpServletRequest request, Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        String query = """
                INSERT INTO ENQUIRY (counsellor_id_fk, student_name, phno, classmode_id, status_id,course_id)
                VALUES (:counsellor_id_fk, :student_name, :phno, :classmode_id, :status_id,:course_id)
                """;
        // student_name,phno,classmode_id,status_id,counsellor_id_fk,course_id
        int counsellorId = jwtUtil.extractCounsellorId(request.getHeader("Authorization").substring(7));

        // get the classmode id from payload
        int classmodeId = Integer.parseInt(payload.get("classmode_id").toString());
        int statusId = Integer.parseInt(payload.get("status_id").toString());
        int courseId = Integer.parseInt(payload.get("course_id").toString());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("counsellor_id_fk", counsellorId);
        params.addValue("student_name", payload.get("student_name"));
        params.addValue("phno", payload.get("phno"));
        params.addValue("classmode_id", classmodeId);
        params.addValue("status_id", statusId);
        params.addValue("course_id", courseId);

        try {
            int i = namedParameterJdbcTemplate.update(query, params);

            if (i > 0) {
                response.put("status", "SUCCESS");
                response.put("message", "Enquiry added successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "FAILURE");
            response.put("message", e.getMessage());
        }
        return response;

    }

    public Map<String, Object> getDropDownsData() {
        Map<String, Object> response = new HashMap<>();
        try {
            String classModeQuery = "SELECT * FROM CLASSMODE";
            String statusQuery = "SELECT * FROM STATUS";
            String courseQuery = "SELECT * FROM COURSE";

            response.put("classModes", jdbcTemplate.queryForList(classModeQuery));
            response.put("statuses", jdbcTemplate.queryForList(statusQuery));
            response.put("courses", jdbcTemplate.queryForList(courseQuery));

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "FAILURE");
            response.put("message", e.getMessage());
        }
        return response;

    }

    public Map<String, Object> getDashBoardData(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        String query = """
                              SELECT
                    COUNT(e.id) AS total_enquiries,
                    SUM(CASE WHEN s.status_name = 'Open' THEN 1 ELSE 0 END) AS open_enquiries,
                    SUM(CASE WHEN s.status_name = 'Enrolled' THEN 1 ELSE 0 END) AS enrolled_enquiries,
                    SUM(CASE WHEN s.status_name = 'Lost' THEN 1 ELSE 0 END) AS dropped_enquiries
                FROM
                    ENQUIRY e
                JOIN
                    STATUS s ON e.status_id = s.id
                WHERE
                    e.counsellor_id_fk = :counsellorId;
                             """;
        try {
            int counsellorId = jwtUtil.extractCounsellorId(request.getHeader("Authorization").substring(7));
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("counsellorId", counsellorId);
            response.put("dashboardData", namedParameterJdbcTemplate.queryForList(query, params));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public Map<String, Object> getEnquiries(HttpServletRequest request, Map<String, Object> filters) {
        Map<String, Object> response = new HashMap<>();

        // 1. Dynamic SQL Query
        // The logic (:param IS NULL OR column = :param) allows optional filtering
        String query = """
                    SELECT
                        e.id,
                        e.student_name,
                        e.phno,
                        c.course_name,       -- Still need join to SELECT the name
                        cm.mode_name,        -- Still need join to SELECT the name
                        s.status_name        -- Still need join to SELECT the name
                    FROM
                        ENQUIRY e
                    JOIN
                        STATUS s ON e.status_id = s.id
                    JOIN
                        COURSE c ON e.course_id = c.id
                    JOIN
                        CLASSMODE cm ON e.classmode_id = cm.id
                    WHERE
                        e.counsellor_id_fk = :counsellorId
                        -- Filter by ID directly (Faster & Safer)
                        AND (:classModeId IS NULL OR e.classmode_id = :classModeId)
                        AND (:courseId IS NULL OR e.course_id = :courseId)
                        AND (:statusId IS NULL OR e.status_id = :statusId)
                """;

        try {
            // 2. Extract Counsellor ID from JWT
            String token = request.getHeader("Authorization").substring(7);
            int counsellorId = jwtUtil.extractCounsellorId(token);

            // 3. Extract and Clean Filter Values from the Map
            // We check if value is null, empty, or "-Select-"
            Integer classModeId = null;
            if (filters.get("classModeId") != null && !filters.get("classModeId").toString().equals("-Select-")) {
                classModeId = Integer.parseInt(filters.get("classModeId").toString());
            }

            Integer courseId = null;
            if (filters.get("courseId") != null && !filters.get("courseId").toString().equals("-Select-")) {
                courseId = Integer.parseInt(filters.get("courseId").toString());
            }

            Integer statusId = null;
            if (filters.get("statusId") != null && !filters.get("statusId").toString().equals("-Select-")) {
                statusId = Integer.parseInt(filters.get("statusId").toString());
            }

            // 4. Set Parameters
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("counsellorId", counsellorId);
            params.addValue("classModeId", classModeId);
            params.addValue("courseId", courseId);
            params.addValue("statusId", statusId);

            // 5. Execute
            List<Map<String, Object>> result = namedParameterJdbcTemplate.queryForList(query, params);

            response.put("enquiries", result);
            response.put("status", "success");

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", e.getMessage());
        }

        return response;
    }

    public Map<String, Object> editEnquiry(HttpServletRequest request, Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
       String query = """
                UPDATE ENQUIRY
                SET student_name = :student_name,
                    phno = :phno,
                    classmode_id = :classmode_id,
                    status_id = :status_id,
                    course_id = :course_id
                WHERE id = :id AND counsellor_id_fk = :counsellor_id_fk
                """;

        try {
            int counsellorId = jwtUtil.extractCounsellorId(request.getHeader("Authorization").substring(7));
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("student_name", payload.get("student_name"));
            params.addValue("phno", payload.get("phno"));
            params.addValue("classmode_id", payload.get("classmode_id"));
            params.addValue("status_id", payload.get("status_id"));
            params.addValue("course_id", payload.get("course_id"));
            params.addValue("id", payload.get("id"));
            params.addValue("counsellor_id_fk", counsellorId);
           int j= namedParameterJdbcTemplate.update(query, params);
           if(j>0){
            response.put("message", "Enquiry updated successfully");
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}