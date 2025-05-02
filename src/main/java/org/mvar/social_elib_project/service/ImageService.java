package org.mvar.social_elib_project.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class ImageService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations gridFsOperations;

    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        System.out.println("Saving file: " + file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            ObjectId id = gridFsTemplate.store(inputStream, file.getOriginalFilename(), file.getContentType());
            System.out.println("Stored with ID: " + id);
            return id.toHexString();
        }
    }


    public GridFSFile getImageById(String id) {
        return gridFsTemplate.findOne(query(where("_id").is(new ObjectId(id))));
    }

    public InputStream getImageStream(GridFSFile file) throws IOException {
        return gridFsOperations.getResource(file).getInputStream();
    }

    public String getContentType(GridFSFile file) {
        return gridFsOperations.getResource(file).getContentType();
    }
}

