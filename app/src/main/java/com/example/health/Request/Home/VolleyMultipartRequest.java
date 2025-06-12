package com.example.health.Request.Home;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * VolleyMultipartRequest
 * ----------------------------
 * 이미지 파일이나 여러 form-data 값을 포함한 Multipart 요청을 보내기 위한 추상 클래스
 * Volley에서 기본 지원하지 않는 multipart/form-data를 수동으로 구성해 전송함
 */
public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {

    // multipart 전송을 위한 구분자(boundary) 및 줄바꿈 문자열
    private final String twoHyphens = "--";
    private final String lineEnd    = "\r\n";
    private final String boundary   = "apiclient-" + System.currentTimeMillis();

    // 응답 리스너
    private Response.Listener<NetworkResponse> mListener;

    // 헤더, 텍스트 파라미터, 파일 파라미터 저장용 맵
    private Map<String, String> mHeaders     = new HashMap<>();
    private Map<String, String> mStringParts = new HashMap<>();
    private Map<String, DataPart> mFileParts = new HashMap<>();

    /** 생성자 */
    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
    }

    /** 요청 헤더 추가 */
    public void setHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    /** 일반 문자열 파라미터 추가 */
    public void addStringParam(String key, String value) {
        mStringParts.put(key, value);
    }

    /** 파일 파라미터 추가 */
    public void addFileParam(String key, String fileName, byte[] data, String mimeType) {
        mFileParts.put(key, new DataPart(fileName, data, mimeType));
    }

    /** Content-Type 설정 */
    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    /** 요청 헤더 반환 */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    /** 요청 본문 구성 (텍스트 + 파일 multipart 구성) */
    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            // 텍스트 파라미터 작성
            for (Map.Entry<String, String> entry : mStringParts.entrySet()) {
                buildTextPart(dos, entry.getKey(), entry.getValue());
            }
            // 파일 파라미터 작성
            for (Map.Entry<String, DataPart> entry : mFileParts.entrySet()) {
                buildFilePart(dos, entry.getKey(), entry.getValue());
            }
            // multipart 종료 마크 작성
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("IOException writing to ByteArrayOutputStream", e);
        }
    }

    /** 텍스트 파라미터 부분 작성 */
    private void buildTextPart(DataOutputStream dos, String key, String value) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
        dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(value + lineEnd);
    }

    /** 파일 파라미터 부분 작성 */
    private void buildFilePart(DataOutputStream dos, String key, DataPart filePart) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + filePart.getFileName() + "\"" + lineEnd);
        dos.writeBytes("Content-Type: " + filePart.getType() + lineEnd);
        dos.writeBytes(lineEnd);
        dos.write(filePart.getContent());
        dos.writeBytes(lineEnd);
    }

    /** 서버 응답 파싱 (byte 그대로 유지) */
    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(
                response,
                HttpHeaderParser.parseCacheHeaders(response)
        );
    }

    /** 응답 전달 */
    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    /**
     * 내부 클래스: 파일 업로드용 데이터 구조
     */
    public static class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart(String fileName, byte[] content, String type) {
            this.fileName = fileName;
            this.content  = content;
            this.type     = type;
        }

        public String getFileName() { return fileName; }
        public byte[] getContent()  { return content;  }
        public String getType()     { return type;     }
    }
}