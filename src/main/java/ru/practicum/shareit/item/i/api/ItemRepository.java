package ru.practicum.shareit.item.i.api;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findItemById(Long id);

    List<Item> findAllItemsByOwner(User owner);

    @Query("SELECT i FROM Item i WHERE lower(i.name) LIKE lower(concat('%', ?1, '%')) OR" +
            " lower(i.description) like lower(concat('%', ?1, '%')) AND i.available = true")
    List<Item> findAllItemsByNameOrDescriptionContainingIgnoreCase(String text);
}