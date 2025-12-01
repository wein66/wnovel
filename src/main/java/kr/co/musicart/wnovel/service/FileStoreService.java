package kr.co.musicart.wnovel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStoreService {

    // application.yml 에서 설정할 업로드 루트 경로
    @Value("${file.upload-dir:./uploads/}")
    private String rootUploadDir;

    /**
     * [단일] 회차 이미지 업로드 처리
     * 저장 경로: ./uploads/novel/{novelId}/{episodeId}/
     */
    public String storeEpisodeImage(MultipartFile file, Long novelId, Long episodeId) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 1. 저장할 폴더 경로 생성
        String subPath = "novel/" + novelId + "/" + episodeId + "/";
        File folder = new File(rootUploadDir + subPath);

        if (!folder.exists()) {
            folder.mkdirs(); // 상위 폴더까지 한 번에 생성
        }

        // 2. 파일명 중복 방지를 위한 UUID 생성
        String originalFilename = file.getOriginalFilename();
        String storeFilename = UUID.randomUUID() + "_" + originalFilename;

        // 3. 파일 저장
        File dest = new File(folder, storeFilename);
        file.transferTo(dest);

        // 4. 접근 가능한 URL 경로 반환 (WebMvcConfig에서 매핑할 경로)
        // 예: /novel-img/1/10/uuid_abc.jpg
        return "/novel-img/" + novelId + "/" + episodeId + "/" + storeFilename;
    }

    /**
     * [추가됨] [다중] 회차 이미지 업로드 처리
     * 여러 개의 파일을 받아 각각 저장하고, 저장된 URL들의 리스트를 반환합니다.
     */
    public List<String> storeEpisodeImages(List<MultipartFile> files, Long novelId, Long episodeId) throws IOException {
        List<String> storedUrls = new ArrayList<>();
        
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // 각 파일을 단일 저장 메서드로 처리
                String storedUrl = storeEpisodeImage(file, novelId, episodeId);
                if (storedUrl != null) {
                    storedUrls.add(storedUrl);
                }
            }
        }
        return storedUrls;
    }

    /**
     * 소설 표지 이미지 업로드 처리
     * 저장 경로: ./uploads/novel/{novelId}/cover/
     */
    public String storeCoverImage(MultipartFile file, Long novelId) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String subPath = "novel/" + novelId + "/cover/";
        File folder = new File(rootUploadDir + subPath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String storeFilename = UUID.randomUUID() + "_" + originalFilename;

        File dest = new File(folder, storeFilename);
        file.transferTo(dest);

        return "/novel-img/" + novelId + "/cover/" + storeFilename;
    }

    /**
     * 파일 삭제 기능
     * DB에 저장된 URL 경로를 받아서 실제 파일을 삭제합니다.
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        // 1. URL 경로에서 실제 파일 경로로 변환
        // "/novel-img/" 접두사를 제거하고 실제 업로드 경로와 합침
        String relativePath = fileUrl.replace("/novel-img/", "");
        
        // 한글 파일명 등을 대비해 디코딩
        String decodedPath = URLDecoder.decode(relativePath, StandardCharsets.UTF_8);

        File file = new File(rootUploadDir + decodedPath);

        // 2. 파일이 존재하면 삭제
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("파일 삭제 성공: " + file.getAbsolutePath());
            } else {
                System.err.println("파일 삭제 실패: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * [추가됨] 여러 파일 일괄 삭제 기능
     * 소설이나 회차가 삭제될 때 연관된 이미지를 모두 지우기 위해 사용합니다.
     */
    public void deleteFiles(List<String> fileUrls) {
        if (fileUrls != null) {
            for (String url : fileUrls) {
                deleteFile(url);
            }
        }
    }
}