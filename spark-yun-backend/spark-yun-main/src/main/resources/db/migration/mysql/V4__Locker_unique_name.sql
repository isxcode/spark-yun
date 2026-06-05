DELETE l1
FROM sy_locker l1
INNER JOIN sy_locker l2
  ON l1.name = l2.name
  AND l1.id > l2.id;

ALTER TABLE sy_locker ADD CONSTRAINT uk_sy_locker_name UNIQUE (name);
