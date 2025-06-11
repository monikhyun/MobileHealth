// com/example/health/Request/Home/VolleyMultipartRequest.java
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

public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {
    private final String twoHyphens = "--";
    private final String lineEnd    = "\r\n";
    private final String boundary   = "apiclient-" + System.currentTimeMillis();

    private Response.Listener<NetworkResponse> mListener;
    private Map<String, String> mHeaders    = new HashMap<>();
    private Map<String, String> mStringParts= new HashMap<>();
    private Map<String, DataPart> mFileParts= new HashMap<>();

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
    }

    public void setHeader(String key, String value) {
        mHeaders.put(key, value);
    }
    public void addStringParam(String key, String value) {
        mStringParts.put(key, value);
    }
    public void addFileParam(String key, String fileName, byte[] data, String mimeType) {
        mFileParts.put(key, new DataPart(fileName, data, mimeType));
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos    = new DataOutputStream(bos);

        try {
            // text params
            for (Map.Entry<String, String> entry : mStringParts.entrySet()) {
                buildTextPart(dos, entry.getKey(), entry.getValue());
            }
            // file params
            for (Map.Entry<String, DataPart> entry : mFileParts.entrySet()) {
                buildFilePart(dos, entry.getKey(), entry.getValue());
            }
            // finish boundary
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("IOException writing to ByteArrayOutputStream", e);
        }
    }

    private void buildTextPart(DataOutputStream dos, String key, String value) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
        dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
        dos.writeBytes(lineEnd);
        dos.writeBytes(value + lineEnd);
    }

    private void buildFilePart(DataOutputStream dos, String key, DataPart filePart) throws IOException {
        dos.writeBytes(twoHyphens + boundary + lineEnd);
        dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + filePart.getFileName() + "\"" + lineEnd);
        dos.writeBytes("Content-Type: " + filePart.getType() + lineEnd);
        dos.writeBytes(lineEnd);
        dos.write(filePart.getContent());
        dos.writeBytes(lineEnd);
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(
                response,
                HttpHeaderParser.parseCacheHeaders(response)
        );
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

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
        public byte[] getContent()  { return content; }
        public String getType()     { return type;    }
    }
}