// 패키지 선언
package com.example.health.Request.Exercise;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * ExerciseRecordDeleteRequest
 * ----------------------------
 * 특정 운동 기록 세트를 삭제하는 서버 요청 클래스.
 * 요청 방식: PUT
 * 요청 주소: /api/exercise/add/{userId}/{date}/{exerciseName}/delete/{setCount}
 *
 * 예시:
 * PUT /api/exercise/add/mjc/2025-06-12/풀%20업/delete/2
 */
public class ExerciseRecordDeleteRequest extends StringRequest {

    /**
     * 생성자
     *
     * @param userId       사용자 ID
     * @param date         운동 일자 (예: "2025-06-12")
     * @param exerciseName 운동 이름 (예: "풀 업")
     * @param setCount     삭제할 세트 번호 (1부터 시작)
     * @param listener     응답 성공 시 콜백
     * @param errorListener 오류 발생 시 콜백
     */
    public ExerciseRecordDeleteRequest(
            String userId,
            String date,
            String exerciseName,  // 공백이나 한글 포함 가능
            int setCount,
            Response.Listener<String> listener,
            Response.ErrorListener errorListener
    ) {
        // 부모 클래스(StringRequest)의 생성자 호출
        super(
                Method.PUT,                            // HTTP 메서드: PUT
                buildUrl(userId, date, exerciseName, setCount), // 최종 URL 생성
                listener,
                errorListener
        );
    }

    /**
     * URL 구성 메소드 (운동 이름은 UTF-8로 인코딩 필요)
     *
     * @param userId       사용자 ID
     * @param date         날짜 (yyyy-MM-dd)
     * @param exerciseName 운동 이름 (공백 가능)
     * @param setCount     삭제할 세트 번호
     * @return 최종 API 호출용 URL
     */
    private static String buildUrl(String userId, String date, String exerciseName, int setCount) {
        // 공백이 "+"로 변환되지 않도록 %20 처리
        String encodedName = URLEncoder.encode(exerciseName, StandardCharsets.UTF_8)
                .replace("+", "%20");
        return "http://10.0.2.2:8080/api/exercise/add/"
                + userId + "/" + date + "/" + encodedName + "/delete/" + setCount;
    }

    /**
     * HTTP 요청 헤더 설정
     *
     * @return 헤더 맵 (Accept: text/plain)
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<>();
        headers.put("Accept", "text/plain");  // 응답 형식 명시
        return headers;
    }
}