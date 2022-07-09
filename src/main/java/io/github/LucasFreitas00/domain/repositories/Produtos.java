package io.github.LucasFreitas00.domain.repositories;

import io.github.LucasFreitas00.domain.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Produtos extends JpaRepository<Produto,Integer> {
}