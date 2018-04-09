package org.synyx.coffee.ssmdemo.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    Optional<Notification> findOneByToken(String token);
}
