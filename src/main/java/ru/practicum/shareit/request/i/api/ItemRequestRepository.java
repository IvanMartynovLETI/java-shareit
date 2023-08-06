package ru.practicum.shareit.request.i.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    ItemRequest findItemRequestById(Long itemRequestId);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    Page<ItemRequest> findAllByRequestorIdNotIn(Collection<Long> userIdsToBeExcluded, Pageable page);
}