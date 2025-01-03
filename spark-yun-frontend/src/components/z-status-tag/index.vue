<template>
    <div :class="vmStatusClass">
        <div class="vm-status__pointer"></div>
        <span class="vm-status__text">{{ vmStatus.name }}</span>
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';

interface ColonyInfo {
    id: string
    name: string
    status: string
    defaultCluster: boolean
}

const status = ref<any>({
    SUCCESS: '成功',
    FAIL: '失败',
    ABORT: '已中止',
    ABORTING: '中止中',
    RUNNING: '运行中',
    PENDING: '等待中',

    ACTIVE: '可用',
    DELETED: '已删除',
    NO_ACTIVE: '不可用',
    NEW: '待配置',
    UN_CHECK: '待检测',

    STOP: '待激活',
    UN_INSTALL: '未安装',
    CHECKING: '检测中',
    STARTING: '启动中',
    STOPPING: '停止中',
    INSTALLING: '安装中',
    REMOVING: '卸载中',
    CHECK_ERROR: '检测失败',
    CAN_NOT_INSTALL: '不可安装',
    CAN_INSTALL: '可安装',
    INSTALL_ERROR: '安装失败',

    UN_AUTO: '未运行',
    PUBLISHED: '已发布',
    UN_PUBLISHED: '已下线',
    OFFLINE: '已下线',
    UNPUBLISHED: '未发布',

    DEPLOYING: '部署中',
    STOP_S: '停止',
    DEPLOY: '启动中',
    RUNNING_S: '运行中',

    DISABLE: '禁用',
    CHECK_SUCCESS: '检测成功',
    ENABLE: '启用',

    COLLECTING: '采集中'
})

const props = withDefaults(defineProps<{
    status: string
}>(), {
    status: 'SUCCESS'
})

const vmStatusClass = computed(() => {
    if (props.status) {
        return ['vm-status', 'is-' + props.status.toLowerCase()]
    } else {
        return ['vm-status', 'is-none']
    }
})

const vmStatus = computed<{ status: ColonyInfo['status'], name: string }>(() => {
    if (props.status) {
        return {
            status: props.status,
            name: status.value[props.status]
        }
    } else {
        return {
            status: 'is-none',
            name: '未运行'
        }
    }
})

</script>

<style scoped lang="scss">
.vm-status {
    display: flex;
    align-items: center;
    // justify-content: center;
    font-size: 12px;
    line-height: 22px;
    .vm-status__text {
        margin-left: 4px;
    }

    &.is-success,
    &.is-can_install,
    &.is-published,
    &.is-running_s,
    &.is-enable,
    &.is-check_success,
    &.is-active {
        color: #43CF7C;

        .vm-status__pointer {
            background-color: #43CF7C;
        }
        .vm-status__text {
            color: #43CF7C;
        }
    }

    &.is-fail,
    &.is-no_active,
    &.is-check_error,
    &.is-can_not_install,
    &.is-install_error,
    &.is-un_published,
    &.is-offline,
    &.is-unpublished,
    &.is-deleted,
    &.is-stop {
        color: #FA541C;

        .vm-status__pointer {
            background-color: #FA541C;
        }
        .vm-status__text {
            color: #FA541C;
        }
    }

    &.is-abort {
        color: #9f26e1;

        .vm-status__pointer {
            background-color: #9f26e1;
        }
        .vm-status__text {
            color: #9f26e1;
        }
    }

    &.is-aborting,
    &.is-un_check,
    &.is-un_install,
    &.is-checking,
    &.is-stopping,
    &.is-installing,
    &.is-removing,
    &.is-un_auto,
    &.is-deploying,
    &.is-stop_s,
    &.is-disable,
    &.is-collecting,
    &.is-starting {
        color: #b2b2b2;

        .vm-status__pointer {
            background-color: #b2b2b2;
        }
        .vm-status__text {
            color: #b2b2b2;
        }
    }

    &.is-running,&.is-new {
        color: #1890ff;

        .vm-status__pointer {
            background-color: #1890ff;
        }
        .vm-status__text {
            color: #1890ff;
        }
    }

    &.is-pending {
        color: #F5B041;

        .vm-status__pointer {
            background-color: #F5B041;
        }
        .vm-status__text {
            color: #F5B041;
        }
    }

    &.is-none {
        color: #b2b2b2;

        .vm-status__pointer {
            background-color: #b2b2b2;
        }
        .vm-status__text {
            color: #b2b2b2;
        }
    }

    &__pointer {
        width: 8px;
        height: 8px;
        border-radius: 50%;
        margin-right: 4px;
    }
}
</style>