-- 扩长running_log
ALTER TABLE SY_REAL
    ALTER COLUMN running_log TYPE TEXT USING running_log::TEXT;

COMMENT ON COLUMN SY_REAL.running_log IS '运行日志';