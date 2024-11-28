package com.zgamelogic.controllers;

import com.zgamelogic.data.components.DynamicApplicationRepository;
import com.zgamelogic.data.components.DynamicTagRepository;
import com.zgamelogic.data.entities.Application;
import com.zgamelogic.data.entities.Tag;
import com.zgamelogic.data.repositories.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tags")
public class TagController {
    private final DynamicApplicationRepository applicationRepository;
    private final DynamicTagRepository tagRepository;

    public TagController(DynamicApplicationRepository applicationRepository, DynamicTagRepository tagRepository) {
        this.applicationRepository = applicationRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getTags(){
        List<Tag> tags = tagRepository.findAll();
        return ResponseEntity.ok(tags);
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable String tagId) {
        if(!tagRepository.existsById(tagId)) return ResponseEntity.notFound().build();
        applicationRepository.findAllByTagName(tagId).forEach(app -> {
            app.getTags().removeIf(tag -> tag.getName().equals(tagId));
            applicationRepository.save(app);
        });
        tagRepository.deleteById(tagId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/remove/{appId}/{tagId}")
    public ResponseEntity<?> removeTag(@PathVariable long appId, @PathVariable String tagId) {
        if(!tagRepository.existsById(tagId)) return ResponseEntity.notFound().build();
        if(!applicationRepository.existsById(appId)) return ResponseEntity.notFound().build();
        Application app = applicationRepository.getReferenceById(appId);
        app.getTags().removeIf(tag -> tag.getName().equals(tagId));
        applicationRepository.save(app);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/add/{appId}/{tagId}")
    public ResponseEntity<?> addTag(@PathVariable long appId, @PathVariable String tagId) {
        if(!tagRepository.existsById(tagId)) return ResponseEntity.notFound().build();
        if(!applicationRepository.existsById(appId)) return ResponseEntity.notFound().build();
        Application app = applicationRepository.getReferenceById(appId);
        app.getTags().add(tagRepository.getReferenceById(tagId));
        applicationRepository.save(app);
        return ResponseEntity.ok().build();
    }
}
