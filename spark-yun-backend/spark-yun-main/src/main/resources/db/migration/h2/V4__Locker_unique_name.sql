DELETE FROM sy_locker
WHERE id NOT IN (
  SELECT min_id
  FROM (
    SELECT MIN(id) AS min_id
    FROM sy_locker
    GROUP BY name
  )
);

ALTER TABLE sy_locker ADD CONSTRAINT uk_sy_locker_name UNIQUE (name);
