package com.raja.service;

import com.raja.entity.UploadFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadFileService {

    UploadFile save(MultipartFile multipartFile);

    List<String> getAll();

    boolean deleteById(Long uploadFileId);

    UploadFile update(MultipartFile multipartFile);
}
