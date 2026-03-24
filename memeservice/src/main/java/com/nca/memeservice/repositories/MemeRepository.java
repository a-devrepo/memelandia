package com.nca.memeservice.repositories;

import com.nca.memeservice.entities.Meme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemeRepository extends JpaRepository<Meme, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM memes WHERE status = 'VALIDADO' ORDER BY RANDOM() LIMIT 1")
    Optional<Meme> findRandomMeme();
}
