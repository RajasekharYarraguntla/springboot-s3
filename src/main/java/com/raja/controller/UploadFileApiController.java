package com.raja.controller;

import com.raja.entity.UploadFile;
import com.raja.service.UploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/upload")
public class UploadFileApiController {

    @Autowired
    private UploadFileService service;

    @PostMapping
    public ResponseEntity<UploadFile> save(@RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(service.save(file), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<String>> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UploadFile> update(@RequestParam("file") MultipartFile multipartFile) {
        return new ResponseEntity<>(service.update(multipartFile), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable("id") Long uploadFileId) {
        final boolean status = service.deleteById(uploadFileId);
        if (status) {
            return ResponseEntity.noContent().build();
        } else return ResponseEntity.notFound().build();

    }
}
