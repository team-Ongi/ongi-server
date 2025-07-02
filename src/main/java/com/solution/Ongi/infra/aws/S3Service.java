package com.solution.Ongi.infra.aws;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.solution.Ongi.global.properties.AwsProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    // Config에서 s3Client라는 이름으로 등록된 bean을 사용한다.
    private final AmazonS3 amazonS3;
    private final AwsProperties awsProperties;

    public String uploadFile(MultipartFile multipartFile, Integer userId) throws IOException {
        //  MultipartFile을 File로 변환
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환에 실패했습니다."));

        //  파일 이름 설정
        String fileName = userId + "/" + uploadFile.getName();

        // S3에 파일을 업로드. 업로드 완료 여부와 관계 없이 임시 경로에 생성된 파일을 삭제
        try {
            return putS3(uploadFile, fileName);
        } finally {
            removeNewFile(uploadFile);
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        // 임시 경로에 file을 생성한다.
        File convertFile = new File(System.getProperty("java.io.tmpdir"), Objects.requireNonNull(file.getOriginalFilename()));

        // MultipartFile의 내용을 convertFile에 작성한다.
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    private String putS3(File uploadFile, String fileName) {
        // S3에 파일을 업로드한다.
        amazonS3.putObject(new PutObjectRequest(awsProperties.getBucket(), fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        // 업로드된 파일의 경로를 가져온다.
        return amazonS3.getUrl(awsProperties.getBucket(), fileName).toString();
    }

    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

    // S3 파일 삭제 함수
    public void deleteFile(String filePath) {

        if (amazonS3.doesObjectExist(awsProperties.getBucket(), filePath)) {
            amazonS3.deleteObject(awsProperties.getBucket(), filePath);
            log.info("S3에서 파일 삭제 완료: {}", filePath);
        } else {
            log.warn("삭제할 파일이 존재하지 않습니다: {}", filePath);
        }
    }
}