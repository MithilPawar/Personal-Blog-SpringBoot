package com.blog.personal_blog.exception;

public class BlogNotFoundException extends RuntimeException{
    public BlogNotFoundException(String message){
        super(message);
    }
}
