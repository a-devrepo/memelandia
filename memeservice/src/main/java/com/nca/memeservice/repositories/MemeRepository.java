package com.nca.memeservice.repositories;

import com.nca.memeservice.entities.Meme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MemeRepository extends JpaRepository<Meme, UUID> {
}
