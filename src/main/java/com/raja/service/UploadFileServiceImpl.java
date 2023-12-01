package com.raja.service;

import com.raja.config.UploadFileConfiguration;
import com.raja.entity.UploadFile;
import com.raja.repository.UploadFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UploadFileServiceImpl implements UploadFileService {

    @Autowired
    private UploadFileRepository repository;
    @Autowired
    private S3Client s3Client;
    @Autowired
    private UploadFileConfiguration configuration;

    @Override
    public UploadFile save(MultipartFile multipartFile) {
        final UploadFile uploadFile = uploadFile(multipartFile);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(configuration.getBucketName())
                .key(configuration.getMainFolder() + "/" + uploadFile.getFileName())
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(multipartFile.getBytes()));
        } catch (S3Exception | IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        log.info("File uploaded successfully to S3!");

        return repository.save(uploadFile);
    }

    @Override
    public List<String> getAll() {

        final List<String> list = new ArrayList<>();
        final ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(ListObjectsV2Request.builder().
                bucket(configuration.getBucketName()).
                prefix(configuration.getMainFolder()).build());
        listObjectsV2Response.contents().forEach(object -> {
            if (!object.key().endsWith("/")) {
                final GetUrlRequest urlRequest = GetUrlRequest.builder().bucket(configuration.getBucketName())
                        .key(object.key()).build();
                URL objectUrl = s3Client.utilities().getUrl(urlRequest);
                list.add(String.valueOf(objectUrl));
            }
        });
        return list;
    }

    @Override
    public boolean deleteById(Long uploadFileId) {
        final Optional<UploadFile> optional = repository.findById(uploadFileId);
        if (optional.isPresent()) {
            final DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(configuration.getBucketName())
                    .key(configuration.getMainFolder() + "/" + optional.get().getFileName())
                    .build();
            final DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(deleteObjectRequest);
            if (deleteObjectResponse.deleteMarker()) {
                log.info("Successfully deleted the object");
                return true;
            } else {
                log.info("Failed to delete the object");
                return false;
            }
        }
        return false;
    }

    @Override
    public UploadFile update(MultipartFile multipartFile) {
        final Optional<UploadFile> optional = repository.findByOriginalFileName(multipartFile.getOriginalFilename());
        if (optional.isPresent()) {
            final CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(configuration.getBucketName())
                    .destinationBucket(configuration.getBucketName())
                    .destinationKey(configuration.getMainFolder() + "/" + optional.get().getFileName())
                    .sourceKey(configuration.getMainFolder() + "/" + optional.get().getFileName())
                    .build();
            s3Client.copyObject(copyObjectRequest);
            return optional.get();
        } else {
            return save(multipartFile);
        }
    }


    private UploadFile uploadFile(MultipartFile multipartFile) {
        final UploadFile uploadFile = new UploadFile();
        uploadFile.setFileSize(multipartFile.getSize());
        uploadFile.setFileName(getUuidFilename(multipartFile.getOriginalFilename()));
        uploadFile.setOriginalFileName(multipartFile.getOriginalFilename());
        uploadFile.setMimeType(multipartFile.getContentType());
        return uploadFile;
    }

    private String getUuidFilename(String filenameWithExt) {
        return UUID.randomUUID().toString().replace("-", "") + "." + getFileExtension(filenameWithExt);
    }

    private static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf('.') != -1 && fileName.lastIndexOf('.') != 0) {
            return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        }
        return "";
    }

}
