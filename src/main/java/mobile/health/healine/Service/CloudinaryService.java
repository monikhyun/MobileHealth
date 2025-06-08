package mobile.health.healine.Service;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.upload-preset}")
    private String uploadPreset;

    public String uploadImage(MultipartFile file) {
        try {
            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("upload_preset", uploadPreset);

            File tempFile = File.createTempFile("temp", file.getOriginalFilename());
            file.transferTo(tempFile);

            Map uploadResult = cloudinary.uploader().upload(tempFile, uploadOptions);

            return uploadResult.get("secure_url").toString(); // 🔥 이미지 URL 반환
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }
}