package com.isxcode.spark.annotation.vip;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.modules.license.repository.LicenseStore;
import com.isxcode.spark.security.user.TenantEntity;
import com.isxcode.spark.modules.tenant.service.TenantService;
import com.isxcode.spark.modules.workflow.repository.WorkflowRepository;
import com.isxcode.spark.security.user.TenantRepository;
import com.isxcode.spark.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class LicenseApiAdvice {

    private final LicenseStore licenseStore;

    private final TenantRepository tenantRepository;

    private final UserRepository userRepository;

    private final WorkflowRepository workflowRepository;

    private final TenantService tenantService;

    @Pointcut("@annotation(com.isxcode.spark.annotation.vip.LicenseApi)")
    public void operateLicenseApi() {}

    @Before(value = "operateLicenseApi()")
    public void before() {

        if (licenseStore.getLicense() != null) {

            // 超出租户许可证最大值
            if (licenseStore.getLicense().getMaxTenantNum() < tenantRepository.count()) {
                throw new IsxAppException("租户数超出许可证限制数:" + licenseStore.getLicense().getMaxTenantNum() + ",请升级许可证");
            }

            // 超出成员许可证最大值，默认有个admin用户不算
            if (licenseStore.getLicense().getMaxMemberNum() < userRepository.count() - 1) {
                throw new IsxAppException("成员数超出许可证限制数:" + licenseStore.getLicense().getMaxMemberNum() + ",请升级许可证");
            }

            // 任何一个租户下的作业流超出作业流许可证最大值
            List<String> tenantIds =
                tenantRepository.findAll().stream().map(TenantEntity::getId).collect(Collectors.toList());
            for (String tenantId : tenantIds) {
                if (licenseStore.getLicense().getMaxWorkflowNum() < workflowRepository.countByTenantId(tenantId)) {
                    throw new IsxAppException("租户 " + tenantService.getTenantName(tenantId) + " 的作业流数超出许可证限制数:"
                        + licenseStore.getLicense().getMaxWorkflowNum() + ",请升级许可证");
                }
            }
        }
    }
}
