package com.EventManager.API.repositories;

import com.EventManager.API.Domain.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {


    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.addresses a WHERE e.date >= :currentDate")
    public Page<Event> findUpcomingEvents(@Param("currentDate") LocalDateTime currentDate, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "LEFT JOIN e.addresses a " +
            "WHERE (:title = ' ' OR e.title LIKE %:title%) AND "+
            "(:city = ' ' OR e.city LIKE %:city%) AND "+
            "(:uf = ' ' OR a.uf LIKE %:uf%) AND "+
            "(e.date >= :startDate AND e.date <= :endDate)")
    public Page<Event> findFilterEvents(@Param("title") String title,
                                        @Param("city") String city,
                                        @Param("uf")String uf,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate,
                                        Pageable pageable);

}
