package com.blog.personal_blog.dto;

import com.blog.personal_blog.Enum.ReactionType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReactionRequestDTO {
    private ReactionType reactionType;
}
