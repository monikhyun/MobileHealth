// com/example/health/Request/Home/EditProfileRequest.java
package com.example.health.Request.Home;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

public class EditProfileRequest extends VolleyMultipartRequest {
    private static final String URL = "http://10.0.2.2:8080/api/home/edit/profile/";

    /**
     * @param jwtToken     "Bearer " 제외한 토큰
     * @param userId       사용자 ID
     * @param imageBytes   선택한 이미지 바이트 (null 가능)
     * @param imageName    이미지 파일명 (null 가능)
     * @param gender       "MALE" or "FEMALE"
     * @param height       cm 단위 문자열 (예: "180")
     * @param weight       kg 단위 문자열 (예: "75")
     * @param age          나이 문자열 (예: "25")
     */
    public EditProfileRequest(String jwtToken,
                              String userId,
                              byte[] imageBytes,
                              String imageName,
                              String gender,
                              String height,
                              String weight,
                              String age,
                              Response.Listener<NetworkResponse> listener,
                              Response.ErrorListener errorListener) {
        super(Method.PUT, URL + userId, listener, errorListener);

        // 헤더
        setHeader("Authorization", "Bearer " + jwtToken);

        // 텍스트 필드
        addStringParam("gender", gender);
        addStringParam("height", height);
        addStringParam("weight", weight);
        addStringParam("age", age);

        // 이미지 파일 (MultipartFile)
        if (imageBytes != null && imageName != null) {
            // Spring-side ProfileDto.image 에 매핑
            addFileParam("image", imageName, imageBytes, "image/jpeg");
        }
    }
}