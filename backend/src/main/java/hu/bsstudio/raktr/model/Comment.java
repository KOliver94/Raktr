package hu.bsstudio.raktr.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "comment")
@JsonDeserialize(builder = Comment.CommentBuilder.class)
@AllArgsConstructor
@Data
public class Comment extends Commentable {

    @Builder
    public Comment(Long id, @NotBlank String body, @NotNull Date dateOfWriting, @NotNull User writer) {
        super(id, body, dateOfWriting, writer);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class CommentBuilder {
    }
}
