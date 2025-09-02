package zavorotnii.dmytro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zavorotnii.dmytro.model.Chat;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, String> {
    @Query("SELECT c FROM Chat c ORDER BY c.createdAt DESC")
    List<Chat> findAllWithoutHistory();

    @Query("SELECT c FROM Chat c LEFT JOIN FETCH c.history WHERE c.id = :id")
    Optional<Chat> findByIdWithHistory(@Param("id") String id);
}
