package com.backendjavacode.blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backendjavacode.blog.entities.Comment;

public interface CommentRepo  extends JpaRepository<Comment	, Integer> {

}
