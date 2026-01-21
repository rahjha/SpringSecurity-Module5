package com.codetoelevate.SecurityApp.SecurityApplication.controllers;

import com.codetoelevate.SecurityApp.SecurityApplication.dto.PostDTO;
import com.codetoelevate.SecurityApp.SecurityApplication.entities.User;
import com.codetoelevate.SecurityApp.SecurityApplication.exceptions.ResourceNotFoundException;
import com.codetoelevate.SecurityApp.SecurityApplication.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path="/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDTO> createNewPost(@RequestBody PostDTO newPost){
        System.out.println("point-1");
        PostDTO post = postService.createNewPost(newPost);
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPost(){
        List<PostDTO> allPosts = postService.getAllPost();
        return ResponseEntity.ok(allPosts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id){

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("user {}", user);
        Optional<PostDTO> postDTO = postService.getPostById(id);
        return postDTO
                .map(postDTO1 -> ResponseEntity.ok(postDTO1))
                .orElseThrow(()->new ResourceNotFoundException("Post not found with id :"+id));
    }
}
