package com.isxcode.star.modules.datasource.service.biz;

import com.alibaba.fastjson2.JSON;
import com.isxcode.star.api.datasource.constants.DatasourceStatus;
import com.isxcode.star.api.datasource.constants.DatasourceType;
import com.isxcode.star.api.datasource.dto.ConnectInfo;
import com.isxcode.star.api.datasource.dto.KafkaConfig;
import com.isxcode.star.api.datasource.req.*;
import com.isxcode.star.api.datasource.res.*;
import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import com.isxcode.star.backend.api.base.properties.IsxAppProperties;
import com.isxcode.star.common.utils.aes.AesUtils;
import com.isxcode.star.common.utils.path.PathUtils;
import com.isxcode.star.modules.datasource.entity.DatabaseDriverEntity;
import com.isxcode.star.modules.datasource.entity.DatasourceEntity;
import com.isxcode.star.modules.datasource.mapper.DatasourceMapper;
import com.isxcode.star.modules.datasource.repository.DatabaseDriverRepository;
import com.isxcode.star.modules.datasource.repository.DatasourceRepository;
import com.isxcode.star.modules.datasource.service.DatabaseDriverService;
import com.isxcode.star.modules.datasource.service.DatasourceService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;

import com.isxcode.star.modules.datasource.source.DataSourceFactory;
import com.isxcode.star.modules.datasource.source.Datasource;
import com.isxcode.star.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.isxcode.star.common.config.CommonConfig.JPA_TENANT_MODE;
import static com.isxcode.star.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.star.modules.datasource.service.DatasourceService.ALL_EXIST_DRIVER;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DatasourceBizService {

    private final DatasourceRepository datasourceRepository;

    private final DatasourceMapper datasourceMapper;

    private final AesUtils aesUtils;

    private final DatasourceService datasourceService;

    private final DatabaseDriverRepository databaseDriverRepository;

    private final IsxAppProperties isxAppProperties;

    private final DatabaseDriverService databaseDriverService;

    private final DataSourceFactory dataSourceFactory;
    private final UserService userService;

    public void addDatasource(AddDatasourceReq addDatasourceReq) {

        // 检测数据源名称重复
        Optional<DatasourceEntity> datasourceByName = datasourceRepository.findByName(addDatasourceReq.getName());
        if (datasourceByName.isPresent()) {
            throw new IsxAppException("数据名称重复");
        }

        DatasourceEntity datasource = datasourceMapper.dasAddDatasourceReqToDatasourceEntity(addDatasourceReq);

        // 密码对成加密
        datasource.setPasswd(aesUtils.encrypt(datasource.getPasswd()));

        // 判断如果是hive数据源，metastore_uris没有填写，附加默认值，thrift://localhost:9083
        if (DatasourceType.HIVE.equals(addDatasourceReq.getDbType())
            && Strings.isEmpty(addDatasourceReq.getMetastoreUris())) {
            datasource.setMetastoreUris("thrift://localhost:9083");
        }

        // 如果是kafka数据源，添加kafka配置
        if (DatasourceType.KAFKA.equals(addDatasourceReq.getDbType())) {
            addDatasourceReq.getKafkaConfig().setBootstrapServers(addDatasourceReq.getJdbcUrl());
            datasource.setKafkaConfig(JSON.toJSONString(addDatasourceReq.getKafkaConfig()));
        }

        datasource.setCheckDateTime(LocalDateTime.now());
        datasource.setStatus(DatasourceStatus.UN_CHECK);
        datasourceRepository.save(datasource);
    }

    public void updateDatasource(UpdateDatasourceReq updateDatasourceReq) {

        // 检测数据源名称重复
        Optional<DatasourceEntity> datasourceByName = datasourceRepository.findByName(updateDatasourceReq.getName());
        if (datasourceByName.isPresent() && !datasourceByName.get().getId().equals(updateDatasourceReq.getId())) {
            throw new IsxAppException("数据名称重复");
        }

        DatasourceEntity datasource = datasourceService.getDatasource(updateDatasourceReq.getId());

        datasource = datasourceMapper.dasUpdateDatasourceReqToDatasourceEntity(updateDatasourceReq, datasource);

        // 密码对成加密
        datasource.setPasswd(aesUtils.encrypt(datasource.getPasswd()));

        // 如果是kafka数据源，赋予kafka配置
        if (DatasourceType.KAFKA.equals(datasource.getDbType())) {
            updateDatasourceReq.getKafkaConfig().setBootstrapServers(updateDatasourceReq.getJdbcUrl());
            datasource.setKafkaConfig(JSON.toJSONString(updateDatasourceReq.getKafkaConfig()));
        }

        // 判断如果是hive数据源，metastore_uris没有填写，附加默认值，thrift://localhost:9083
        if (DatasourceType.HIVE.equals(updateDatasourceReq.getDbType())
            && Strings.isEmpty(updateDatasourceReq.getMetastoreUris())) {
            datasource.setMetastoreUris("thrift://localhost:9083");
        }

        datasource.setCheckDateTime(LocalDateTime.now());
        datasource.setStatus(DatasourceStatus.UN_CHECK);
        datasourceRepository.save(datasource);
    }

    public Page<PageDatasourceRes> pageDatasource(PageDatasourceReq dasQueryDatasourceReq) {

        if (Strings.isEmpty(dasQueryDatasourceReq.getDatasourceType())) {
            dasQueryDatasourceReq.setDatasourceType(null);
        }

        Page<DatasourceEntity> datasourceEntityPage = datasourceRepository.searchAll(
            dasQueryDatasourceReq.getSearchKeyWord(), dasQueryDatasourceReq.getDatasourceType(),
            PageRequest.of(dasQueryDatasourceReq.getPage(), dasQueryDatasourceReq.getPageSize()));

        Page<PageDatasourceRes> pageDatasourceRes =
            datasourceEntityPage.map(datasourceMapper::datasourceEntityToQueryDatasourceRes);
        pageDatasourceRes.getContent().forEach(e -> {
            if (!Strings.isEmpty(e.getDriverId())) {
                if (!Strings.isEmpty(e.getDriverId())) {
                    e.setDriverName(databaseDriverService.getDriverName(e.getDriverId()));
                }
            }
            if (DatasourceType.KAFKA.equals(e.getDbType())) {
                e.setKafkaConfig(JSON.parseObject(e.getKafkaConfigStr(), KafkaConfig.class));
            }
        });

        return pageDatasourceRes;
    }

    public void deleteDatasource(DeleteDatasourceReq deleteDatasourceReq) {

        datasourceRepository.deleteById(deleteDatasourceReq.getDatasourceId());
    }

    public TestConnectRes testConnect(GetConnectLogReq testConnectReq) {

        DatasourceEntity datasourceEntity = datasourceService.getDatasource(testConnectReq.getDatasourceId());

        // 测试连接
        datasourceEntity.setCheckDateTime(LocalDateTime.now());

        if (DatasourceType.KAFKA.equals(datasourceEntity.getDbType())) {
            try {
                datasourceService.checkKafka(JSON.parseObject(datasourceEntity.getKafkaConfig(), KafkaConfig.class));
                datasourceEntity.setStatus(DatasourceStatus.ACTIVE);
                datasourceEntity.setConnectLog("测试连接成功！");
                datasourceRepository.save(datasourceEntity);
                return new TestConnectRes(true, "连接成功");
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                datasourceEntity.setStatus(DatasourceStatus.FAIL);
                datasourceEntity.setConnectLog("测试连接失败：" + e.getMessage());
                datasourceRepository.save(datasourceEntity);
                return new TestConnectRes(false, e.getMessage());
            }
        } else {
            ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
            Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
            connectInfo.setLoginTimeout(5);
            try (Connection connection = datasource.getConnection(connectInfo)) {
                if (connection != null) {
                    datasourceEntity.setStatus(DatasourceStatus.ACTIVE);
                    datasourceEntity.setConnectLog("测试连接成功！");
                    datasourceRepository.save(datasourceEntity);
                    return new TestConnectRes(true, "连接成功");
                } else {
                    datasourceEntity.setStatus(DatasourceStatus.FAIL);
                    datasourceEntity.setConnectLog("测试连接失败: 请检查连接协议");
                    datasourceRepository.save(datasourceEntity);
                    return new TestConnectRes(false, "请检查连接协议");
                }
            } catch (IsxAppException exception) {
                log.debug(exception.getMessage(), exception);

                datasourceEntity.setStatus(DatasourceStatus.FAIL);
                datasourceEntity.setConnectLog("测试连接失败：" + exception.getMsg());
                datasourceRepository.save(datasourceEntity);
                return new TestConnectRes(false, exception.getMessage());
            } catch (Exception e) {
                log.debug(e.getMessage(), e);

                datasourceEntity.setStatus(DatasourceStatus.FAIL);
                datasourceEntity.setConnectLog("测试连接失败：" + e.getMessage());
                datasourceRepository.save(datasourceEntity);
                return new TestConnectRes(false, e.getMessage());
            }
        }
    }

    public CheckConnectRes checkConnect(CheckConnectReq checkConnectReq) {

        DatasourceEntity datasourceEntity = datasourceMapper.checkConnectReqToDatasourceEntity(checkConnectReq);
        if (Strings.isNotEmpty(checkConnectReq.getPasswd())) {
            datasourceEntity.setPasswd(aesUtils.encrypt(checkConnectReq.getPasswd()));
        }

        if (DatasourceType.KAFKA.equals(datasourceEntity.getDbType())) {
            try {
                datasourceService.checkKafka(checkConnectReq.getKafkaConfig());
                return new CheckConnectRes(true, "连接成功");
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                return new CheckConnectRes(false, e.getMessage());
            }
        } else {
            ConnectInfo connectInfo = datasourceMapper.datasourceEntityToConnectInfo(datasourceEntity);
            Datasource datasource = dataSourceFactory.getDatasource(connectInfo.getDbType());
            try (Connection connection = datasource.getConnection(connectInfo)) {
                if (connection != null) {
                    return new CheckConnectRes(true, "连接成功");
                } else {
                    return new CheckConnectRes(false, "请检查连接协议");
                }
            } catch (IsxAppException exception) {
                log.debug(exception.getMessage(), exception);
                return new CheckConnectRes(false, exception.getMsg());
            } catch (Exception e) {
                log.debug(e.getMessage(), e);
                return new CheckConnectRes(false, e.getMessage());
            }
        }
    }

    public GetConnectLogRes getConnectLog(GetConnectLogReq getConnectLogReq) {

        DatasourceEntity datasource = datasourceService.getDatasource(getConnectLogReq.getDatasourceId());

        return GetConnectLogRes.builder().connectLog(datasource.getConnectLog()).build();
    }

    public void uploadDatabaseDriver(MultipartFile driverFile, String dbType, String name, String remark) {

        // 判断驱动文件夹是否存在，没有则创建
        String driverDirPath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "jdbc"
            + File.separator + TENANT_ID.get();
        if (!new File(driverDirPath).exists()) {
            try {
                Files.createDirectories(Paths.get(driverDirPath));
            } catch (IOException e) {
                log.debug(e.getMessage(), e);
                throw new IsxAppException("上传驱动，目录创建失败");
            }
        }

        // 保存驱动文件
        try (InputStream inputStream = driverFile.getInputStream()) {
            Files.copy(inputStream, Paths.get(driverDirPath).resolve(driverFile.getOriginalFilename()),
                StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
            throw new IsxAppException("上传许可证失败");
        }

        // 初始化驱动对象
        DatabaseDriverEntity databaseDriver =
            DatabaseDriverEntity.builder().name(name).dbType(dbType).driverType("TENANT_DRIVER").remark(remark)
                .isDefaultDriver(false).fileName(driverFile.getOriginalFilename()).build();

        // 持久化
        databaseDriverRepository.save(databaseDriver);
    }

    public Page<PageDatabaseDriverRes> pageDatabaseDriver(PageDatabaseDriverReq pageDatabaseDriverReq) {

        JPA_TENANT_MODE.set(false);
        Page<DatabaseDriverEntity> pageDatabaseDriver =
            databaseDriverRepository.searchAll(pageDatabaseDriverReq.getSearchKeyWord(), TENANT_ID.get(),
                PageRequest.of(pageDatabaseDriverReq.getPage(), pageDatabaseDriverReq.getPageSize()));

        Page<PageDatabaseDriverRes> map =
            pageDatabaseDriver.map(datasourceMapper::dataDriverEntityToPageDatabaseDriverRes);

        map.getContent().forEach(e -> e.setCreateUsername(userService.getUserName(e.getCreateBy())));

        return map;
    }

    public void deleteDatabaseDriver(DeleteDatabaseDriverReq deleteDatabaseDriverReq) {

        // 支持查询所有的数据
        JPA_TENANT_MODE.set(false);
        DatabaseDriverEntity driver = databaseDriverService.getDriver(deleteDatabaseDriverReq.getDriverId());
        JPA_TENANT_MODE.set(true);

        // 系统驱动无法删除
        if ("SYSTEM_DRIVER".equals(driver.getDriverType())) {
            throw new IsxAppException("系统数据源驱动无法删除");
        }

        // 判断驱动是否被别人使用，使用则不能删除
        List<DatasourceEntity> allDrivers = datasourceRepository.findAllByDriverId(driver.getId());
        if (!allDrivers.isEmpty()) {
            throw new IsxAppException("有数据源已使用当前驱动，无法删除");
        }

        // 卸载Map中的驱动
        ALL_EXIST_DRIVER.remove(driver.getId());

        // 将文件名改名字 xxx.jar ${driverId}_xxx.jar_bak
        try {
            String jdbcDirPath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator
                + "jdbc" + File.separator + TENANT_ID.get();
            Files.copy(Paths.get(jdbcDirPath).resolve(driver.getFileName()),
                Paths.get(jdbcDirPath).resolve(driver.getId() + "_" + driver.getFileName() + "_bak"),
                StandardCopyOption.REPLACE_EXISTING);
            Files.delete(Paths.get(jdbcDirPath).resolve(driver.getFileName()));
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
            throw new IsxAppException("删除驱动文件异常");
        }

        // 删除数据库
        databaseDriverRepository.deleteById(driver.getId());
    }

    public void settingDefaultDatabaseDriver(SettingDefaultDatabaseDriverReq settingDefaultDatabaseDriverReq) {

        JPA_TENANT_MODE.set(false);
        Optional<DatabaseDriverEntity> databaseDriverEntityOptional =
            databaseDriverRepository.findById(settingDefaultDatabaseDriverReq.getDriverId());
        JPA_TENANT_MODE.set(true);

        if (!databaseDriverEntityOptional.isPresent()) {
            throw new IsxAppException("数据源驱动不存在");
        }

        DatabaseDriverEntity databaseDriver = databaseDriverEntityOptional.get();

        if ("SYSTEM_DRIVER".equals(databaseDriver.getDriverType())) {
            throw new IsxAppException("系统驱动无法默认");
        }

        if (settingDefaultDatabaseDriverReq.getIsDefaultDriver()) {
            // 将租户中其他的同类型驱动，默认状态都改成false
            List<DatabaseDriverEntity> allDriver = databaseDriverRepository.findAllByDbType(databaseDriver.getDbType());
            allDriver.forEach(e -> e.setIsDefaultDriver(false));
            databaseDriverRepository.saveAll(allDriver);
        }

        databaseDriver.setIsDefaultDriver(settingDefaultDatabaseDriverReq.getIsDefaultDriver());
        databaseDriverRepository.save(databaseDriver);
    }

    public GetDefaultDatabaseDriverRes getDefaultDatabaseDriver(
        GetDefaultDatabaseDriverReq getDefaultDatabaseDriverReq) {

        // 先查询租户的如果有直接返回
        Optional<DatabaseDriverEntity> defaultDriver =
            databaseDriverRepository.findByDriverTypeAndDbTypeAndIsDefaultDriver("TENANT_DRIVER",
                getDefaultDatabaseDriverReq.getDbType(), true);
        if (defaultDriver.isPresent()) {
            return datasourceMapper.databaseDriverEntityToGetDefaultDatabaseDriverRes(defaultDriver.get());
        }

        // 查询系统默认的返回
        JPA_TENANT_MODE.set(false);
        Optional<DatabaseDriverEntity> systemDriver =
            databaseDriverRepository.findByDriverTypeAndDbTypeAndIsDefaultDriver("SYSTEM_DRIVER",
                getDefaultDatabaseDriverReq.getDbType(), true);
        return datasourceMapper.databaseDriverEntityToGetDefaultDatabaseDriverRes(systemDriver.get());
    }

}
