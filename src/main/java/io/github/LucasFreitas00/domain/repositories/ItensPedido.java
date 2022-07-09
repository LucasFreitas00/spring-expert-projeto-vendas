package io.github.LucasFreitas00.domain.repositories;

import io.github.LucasFreitas00.domain.entities.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItensPedido extends JpaRepository<ItemPedido, Integer> {
}