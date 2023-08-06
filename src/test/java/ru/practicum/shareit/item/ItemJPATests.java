package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.i.api.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Sql(scripts = {"/schema.sql", "/testData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ItemJPATests {
    @Autowired
    TestEntityManager em;

    @Autowired
    ItemRepository itemRepository;

    @Test
    public void contextLoads() {

        Assertions.assertNotNull(em);
    }

    @Test
    public void findItemByName_ThenReturnItemTest() {
        int from = 0;
        int size = 25;
        String searchStr = "item1";
        Pageable page = PageRequest.of(from, size);

        List<Item> items = itemRepository.findAllItemsByNameOrDescriptionContainingIgnoreCase(searchStr, page);

        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getName()).isEqualTo(searchStr);
    }

    @Test
    public void findItemByDescription_ThenReturnItemTest() {
        int from = 0;
        int size = 25;
        String searchStr = "description of item1";
        Pageable page = PageRequest.of(from, size);

        List<Item> items = itemRepository.findAllItemsByNameOrDescriptionContainingIgnoreCase(searchStr, page);

        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).getDescription()).isEqualTo(searchStr);
    }
}