export default {
    editConfig: {
        name: '开关组件',
        icon: 'Open',
        code: 'FormInputSwitch'
    },
    componentConfig: {
        uuid: '16 uuid',
        type: 'simple',
        formValueCode: '',
        codeType: 'custom',
        label: '开关组件',
        disabled: false,
        required: false,
        width: 2,
        isColumn: true,
        componentType: 'FormInputSwitch',
        switchInfo: {
            open: '是',
            close: '否'
        },
        valid: true
    },
    conponentSetConfig: [
        'LABEL',
        'CODE_SELECT',
        'PRIMARY_COLUMN',
        'WIDTH',
        'DEFAULTVALUE',
        'SWITCH_INFO',
        'DISABLED',
        'LIST_COLUMN'
    ]
}