package com.isxcode.spark.common.locker;

import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Locker {

    private final LockerRepository lockerRepository;

    /**
     * 加锁.
     */
    public Integer lockOnly(String name) {

        // 给数据库加一条数据
        return lockerRepository.saveAndFlush(LockerEntity.builder().name(name).build()).getId();
    }

    /**
     * 加锁.
     */
    public Integer lockOnly(String name, String box) {

        Optional<LockerEntity> lockerEntityOptional = lockerRepository.findByBox(box);
        if (lockerEntityOptional.isPresent()) {
            return lockerEntityOptional.get().getId();
        } else {
            // 给数据库加一条数据
            return lockerRepository.save(LockerEntity.builder().name(name).box(box).build()).getId();
        }
    }

    /**
     * 加锁.
     */
    public Integer lock(String name) {

        // 给数据库加一条数据
        Integer id = lockerRepository.save(LockerEntity.builder().name(name).build()).getId();

        // 判断当前线程是否为最小值
        Integer minId;
        do {
            minId = lockerRepository.getMinId(name);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
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
}
