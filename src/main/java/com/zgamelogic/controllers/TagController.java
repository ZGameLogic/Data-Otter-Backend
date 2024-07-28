package com.zgamelogic.controllers;

import com.zgamelogic.data.tags.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tags")
public class TagController {
    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<?> deleteTag(@PathVariable String tagId) {
        if(tagRepository.existsById(tagId)) return ResponseEntity.notFound().build();
        tagRepository.deleteById(tagId);
        return ResponseEntity.ok().build();
    }
}
