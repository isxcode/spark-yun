package com.isxcode.star.modules.user.mapper;

import com.isxcode.star.api.user.pojos.req.AddUserReq;
import com.isxcode.star.api.user.pojos.req.UpdateUserInfoReq;
import com.isxcode.star.api.user.pojos.req.UpdateUserReq;
import com.isxcode.star.api.user.pojos.res.PageEnableUserRes;
import com.isxcode.star.api.user.pojos.res.PageUserRes;
import com.isxcode.star.security.user.UserEntity;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-10-05T19:39:24+0800",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

    @Override
    public UserEntity usrAddUserReqToUserEntity(AddUserReq usrAddUserReq) {
        if ( usrAddUserReq == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUsername( usrAddUserReq.getUsername() );
        userEntity.setAccount( usrAddUserReq.getAccount() );
        userEntity.setPasswd( usrAddUserReq.getPasswd() );
        userEntity.setPhone( usrAddUserReq.getPhone() );
        userEntity.setEmail( usrAddUserReq.getEmail() );
        userEntity.setRemark( usrAddUserReq.getRemark() );

        userEntity.setStatus( "ENABLE" );
        userEntity.setRoleCode( "ROLE_NORMAL_MEMBER" );

        return userEntity;
    }

    @Override
    public UserEntity usrUpdateUserReqToUserEntity(UpdateUserReq usrUpdateUserReq, UserEntity userEntity) {
        if ( usrUpdateUserReq == null && userEntity == null ) {
            return null;
        }

        UserEntity userEntity1 = new UserEntity();

        if ( usrUpdateUserReq != null ) {
            userEntity1.setRemark( usrUpdateUserReq.getRemark() );
            userEntity1.setAccount( usrUpdateUserReq.getAccount() );
            userEntity1.setUsername( usrUpdateUserReq.getUsername() );
            userEntity1.setPhone( usrUpdateUserReq.getPhone() );
            userEntity1.setEmail( usrUpdateUserReq.getEmail() );
        }
        if ( userEntity != null ) {
            userEntity1.setPasswd( userEntity.getPasswd() );
            userEntity1.setId( userEntity.getId() );
            userEntity1.setIntroduce( userEntity.getIntroduce() );
            userEntity1.setRoleCode( userEntity.getRoleCode() );
            userEntity1.setStatus( userEntity.getStatus() );
            userEntity1.setCurrentTenantId( userEntity.getCurrentTenantId() );
            userEntity1.setCreateDateTime( userEntity.getCreateDateTime() );
            userEntity1.setLastModifiedDateTime( userEntity.getLastModifiedDateTime() );
            userEntity1.setCreateBy( userEntity.getCreateBy() );
            userEntity1.setLastModifiedBy( userEntity.getLastModifiedBy() );
            userEntity1.setVersionNumber( userEntity.getVersionNumber() );
            userEntity1.setDeleted( userEntity.getDeleted() );
        }

        return userEntity1;
    }

    @Override
    public UserEntity usrUpdateUserInfoToUserEntity(UpdateUserInfoReq updateUserInfoReq, UserEntity userEntity) {
        if ( updateUserInfoReq == null && userEntity == null ) {
            return null;
        }

        UserEntity userEntity1 = new UserEntity();

        if ( updateUserInfoReq != null ) {
            userEntity1.setUsername( updateUserInfoReq.getUsername() );
            userEntity1.setPhone( updateUserInfoReq.getPhone() );
            userEntity1.setEmail( updateUserInfoReq.getEmail() );
            userEntity1.setRemark( updateUserInfoReq.getRemark() );
        }
        if ( userEntity != null ) {
            userEntity1.setPasswd( userEntity.getPasswd() );
            userEntity1.setId( userEntity.getId() );
            userEntity1.setAccount( userEntity.getAccount() );
            userEntity1.setIntroduce( userEntity.getIntroduce() );
            userEntity1.setRoleCode( userEntity.getRoleCode() );
            userEntity1.setStatus( userEntity.getStatus() );
            userEntity1.setCurrentTenantId( userEntity.getCurrentTenantId() );
            userEntity1.setCreateDateTime( userEntity.getCreateDateTime() );
            userEntity1.setLastModifiedDateTime( userEntity.getLastModifiedDateTime() );
            userEntity1.setCreateBy( userEntity.getCreateBy() );
            userEntity1.setLastModifiedBy( userEntity.getLastModifiedBy() );
            userEntity1.setVersionNumber( userEntity.getVersionNumber() );
            userEntity1.setDeleted( userEntity.getDeleted() );
        }

        return userEntity1;
    }

    @Override
    public PageUserRes userEntityToUsrQueryAllUsersRes(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        PageUserRes pageUserRes = new PageUserRes();

        if ( userEntity.getCreateDateTime() != null ) {
            pageUserRes.setCreateDateTime( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( userEntity.getCreateDateTime() ) );
        }
        pageUserRes.setId( userEntity.getId() );
        pageUserRes.setUsername( userEntity.getUsername() );
        pageUserRes.setAccount( userEntity.getAccount() );
        pageUserRes.setStatus( userEntity.getStatus() );
        pageUserRes.setPhone( userEntity.getPhone() );
        pageUserRes.setEmail( userEntity.getEmail() );
        pageUserRes.setRemark( userEntity.getRemark() );

        return pageUserRes;
    }

    @Override
    public List<PageUserRes> userEntityToUsrQueryAllUsersResList(List<UserEntity> userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        List<PageUserRes> list = new ArrayList<PageUserRes>( userEntity.size() );
        for ( UserEntity userEntity1 : userEntity ) {
            list.add( userEntityToUsrQueryAllUsersRes( userEntity1 ) );
        }

        return list;
    }

    @Override
    public PageEnableUserRes userEntityToUsrQueryAllEnableUsersRes(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        PageEnableUserRes pageEnableUserRes = new PageEnableUserRes();

        if ( userEntity.getCreateDateTime() != null ) {
            pageEnableUserRes.setCreateDateTime( dateTimeFormatter_yyyy_MM_dd_HH_mm_ss_11333195168.format( userEntity.getCreateDateTime() ) );
        }
        pageEnableUserRes.setId( userEntity.getId() );
        pageEnableUserRes.setUsername( userEntity.getUsername() );
        pageEnableUserRes.setAccount( userEntity.getAccount() );
        pageEnableUserRes.setStatus( userEntity.getStatus() );
        pageEnableUserRes.setPhone( userEntity.getPhone() );
        pageEnableUserRes.setEmail( userEntity.getEmail() );
        pageEnableUserRes.setRemark( userEntity.getRemark() );

        return pageEnableUserRes;
    }

    @Override
    public List<PageEnableUserRes> userEntityToUsrQueryAllEnableUsersResList(List<UserEntity> userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        List<PageEnableUserRes> list = new ArrayList<PageEnableUserRes>( userEntity.size() );
        for ( UserEntity userEntity1 : userEntity ) {
            list.add( userEntityToUsrQueryAllEnableUsersRes( userEntity1 ) );
        }

        return list;
    }
}
