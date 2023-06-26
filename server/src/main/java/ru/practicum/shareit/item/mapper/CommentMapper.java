package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {

    public static Comment mapToNewComment(User user, Item item, CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(commentDto.getCreated());

        return comment;
    }

    public static CommentDtoResponse mapToCommentDtoResponse(Comment comment) {
        return new CommentDtoResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}