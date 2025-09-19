package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
