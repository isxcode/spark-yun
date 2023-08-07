package com.isxcode.star.modules.locker;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Locker {

  private final LockerRepository lockerRepository;

  /** 加锁. */
  public Integer lock(String name) {

    return lockerRepository.save(LockerEntity.builder().name(name).build()).getId();
  }

  /** 解锁. */
  public void unlock(Integer id) {

    // 将自己的id删掉
    lockerRepository.deleteById(id);
  }

  /** 等待解锁 */
  @SneakyThrows
  public void waitUnLock(String name, Integer id) {

    Integer minId;
    do {
      minId = lockerRepository.getMinId(name);
      Thread.sleep(500);
    } while (!Objects.equals(id, minId));
  }
}
