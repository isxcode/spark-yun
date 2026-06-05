package com.isxcode.spark.common.locker;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import com.isxcode.spark.common.cluster.ClusterNodeOwner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Locker {

    private static final long LOCK_EXPIRE_HOURS = 24;

    private final LockerRepository lockerRepository;

    /**
     * 加锁.
     */
    public Integer lockOnly(String name) {

        clearExpiredLocks();

        // 给数据库加一条数据
        return lockerRepository.saveAndFlush(buildLocker(name, null)).getId();
    }

    /**
     * 加锁.
     */
    public Integer lockOnly(String name, String box) {

        clearExpiredLocks();

        Optional<LockerEntity> lockerEntityOptional = lockerRepository.findByBox(box);
        if (lockerEntityOptional.isPresent()) {
            return lockerEntityOptional.get().getId();
        } else {
            // 给数据库加一条数据
            return lockerRepository.save(buildLocker(name, box)).getId();
        }
    }

    /**
     * 加锁.
     */
    public Integer lock(String name) {

        clearExpiredLocks();

        // 给数据库加一条数据
        Integer id = lockerRepository.save(buildLocker(name, null)).getId();

        // 判断当前线程是否为最小值
        Integer minId;
        do {
            clearExpiredLocks();
            minId = lockerRepository.getMinId(name);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        } while (!Objects.equals(id, minId));

        // 返回id
        return id;
    }

    /**
     * 是否加锁.
     */
    public Boolean isLocked(String name, Integer id) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
            // 防止没有写进去
        }

        return !Objects.equals(lockerRepository.getMinId(name), id);
    }

    /**
     * 判断是否可行.
     */
    public Boolean isLocked(String name, String box) {

        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
            // 防止没有写进去
        }

        return !Objects.equals(lockerRepository.getMinBox(name), box);
    }

    /**
     * 解锁.
     */
    public void unlock(Integer id) {

        lockerRepository.deleteById(id);
    }

    /**
     * 清理锁.
     */
    public void clearLock(String name) {

        // 将name相关的删掉
        lockerRepository.deleteAll(lockerRepository.findAllByName(name));
    }

    /**
     * 清理锁.
     */
    public void deleteLock(String name) {

        lockerRepository.findByName(name).ifPresent(lockerRepository::delete);
    }

    /**
     * Clear locks that belong to this application instance and expired locks.
     */
    public void clearCurrentOwnerAndExpiredLocks() {

        lockerRepository.deleteAllByOwner(getOwner());
        clearExpiredLocks();
    }

    private void clearExpiredLocks() {

        lockerRepository.deleteAllByExpireTimeBefore(LocalDateTime.now());
    }

    private LockerEntity buildLocker(String name, String box) {

        LocalDateTime now = LocalDateTime.now();
        return LockerEntity.builder().name(name).box(box).owner(getOwner()).createDateTime(now)
            .expireTime(now.plusHours(LOCK_EXPIRE_HOURS)).build();
    }

    private String getOwner() {

        return ClusterNodeOwner.getOwner();
    }
}
