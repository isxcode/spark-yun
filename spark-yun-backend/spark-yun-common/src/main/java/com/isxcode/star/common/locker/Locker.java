package com.isxcode.star.common.locker;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Locker {

  private final LockerRepository lockerRepository;

  /** 加锁. */
  @SneakyThrows
  public Integer lock(String name) {

    // 给数据库加一条数据
    Integer id = lockerRepository.save(LockerEntity.builder().name(name).build()).getId();

    // 判断当前线程是否为最小值
    Integer minId;
    do {
      minId = lockerRepository.getMinId(name);
      Thread.sleep(500);
    } while (!Objects.equals(id, minId));

    // 返回id
    return id;
  }

  /** 解锁. */
  public void unlock(Integer id) {

    // 将自己的id删掉
    lockerRepository.deleteById(id);
  }
}
