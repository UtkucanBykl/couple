package com.example.couple.service;

import com.example.couple.config.UploadProperties;
import com.example.couple.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

  private final UploadProperties uploadProperties;

  public String saveImage(MultipartFile file, String folderName) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new IllegalArgumentException("Sadece image dosyası yüklenebilir");
    }

    String originalFilename = file.getOriginalFilename();
    String extension = getExtension(originalFilename);

    String safeFilename = UUID.randomUUID() + extension;

    Path uploadRoot = Paths.get(uploadProperties.dir()).toAbsolutePath().normalize();
    Path targetDir = uploadRoot.resolve(folderName).normalize();
    Path targetFile = targetDir.resolve(safeFilename).normalize();

    try {
      Files.createDirectories(targetDir);
      file.transferTo(targetFile);
    } catch (IOException e) {
      throw new BadRequestException("Dosya kaydedilemedi " + e);
    }
    String mediaUrl = uploadProperties.mediaUrl();
    return mediaUrl + folderName + "/" + safeFilename;
  }

  private String getExtension(String filename) {
    if (filename == null || !filename.contains(".")) {
      return "";
    }
    return filename.substring(filename.lastIndexOf("."));
  }
}
