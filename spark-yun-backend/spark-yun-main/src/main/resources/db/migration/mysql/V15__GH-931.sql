-- 将ojdbc10-19.20.0.0.jar更新为ojdbc8-19.23.0.0.jar
UPDATE SY_DATABASE_DRIVER SET ID = 'ojdbc8-19.23.0.0', NAME = 'ojdbc8-19.23.0.0', FILE_NAME = 'ojdbc8-19.23.0.0.jar' WHERE ID LIKE 'oracle#_19.20.0.0'