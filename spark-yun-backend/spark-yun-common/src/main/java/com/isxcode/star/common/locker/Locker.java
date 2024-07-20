package com.isxcode.star.common.locker;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Locker {

    private final LockerRepository lockerRepository;

    /** 加锁. */
    public Integer lockOnly(String name) {

        // 给数据库加一条数据
        return lockerRepository.save(LockerEntity.builder().name(name).build()).getId();
    }

    /** 加锁. */
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

    /** 解锁. */
    public void unlock(Integer id) {

        // 将自己的id删掉
        lockerRepository.delete(lockerRepository.findById(id).get());
    }

    /** 清理锁. */
    public void clearLock(String name) {

        // 将name相关的删掉
        lockerRepository.deleteAll(lockerRepository.findAllByName(name));
    }
}
