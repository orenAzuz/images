package com.images.images;


import com.images.images.models.MetaData;
import com.images.images.models.UpFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@RestController
public class ImagesController {
    @Autowired
    private ImageService service;
    private final String clientPath = "/client/src/";
    private final String startName = "assets/images/";

    @PostMapping("/api/addImage")
    public ResponseEntity<String> uploadImage(@RequestBody UpFile image) {

        if (image == null) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        String fullPath = System.getProperty("user.dir");

        try{
            Path path = Paths.get(fullPath);
            Path parentPath = path.getParent();
            String name = startName + System.currentTimeMillis() + image.getName();
            String fileName = parentPath.toString() + clientPath + name;
            File tempFile = new File(fileName);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(image.getFile());
            }
            MetaData metaData = new MetaData();
            metaData.setFileName(fileName);
            metaData.setName(name);
            metaData.setContentType(image.getType());
            metaData.setCreatedDate(new Date());
            service.save(metaData);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
    @GetMapping("/api/images")
    public ResponseEntity<List<MetaData>> getImages(){
        return ResponseEntity.ok(service.getAllMetaData());
    }
    @DeleteMapping("/api/deleteImage/{id}")
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        MetaData metaData = service.findOne(id);
        if (metaData == null) {
            return ResponseEntity.notFound().build();
        }
        File fileToDelete = new File(metaData.getFileName());
        try {
            if (fileToDelete.exists()) {
                if (fileToDelete.delete()) {
                    service.deleteMetaData(id);
                    return ResponseEntity.ok("Image Deleted Successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image file");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image file not found");
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image file: " + e.getMessage());
        }
    }
}
