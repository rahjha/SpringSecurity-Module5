package com.codetoelevate.SecurityApp.SecurityApplication.services;

import com.codetoelevate.SecurityApp.SecurityApplication.dto.PostDTO;
import com.codetoelevate.SecurityApp.SecurityApplication.entities.Post;
import com.codetoelevate.SecurityApp.SecurityApplication.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    public PostDTO createNewPost(PostDTO newPost) {
        Post post = modelMapper.map(newPost, Post.class); //converting DTO to Entity
        Post createdPost = postRepository.save(post);
        return modelMapper.map(createdPost, PostDTO.class);
    }

    public List<PostDTO> getAllPost() {
        List<Post> allPostEntity = postRepository.findAll();
        return allPostEntity
                .stream()
                .map(post->modelMapper.map(post, PostDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<PostDTO> getPostById(Long id) {
        Optional<Post> postEntity = postRepository.findById(id);
        return  postEntity
                .map(postEntity1 -> modelMapper.map(postEntity1, PostDTO.class));
    }
}
