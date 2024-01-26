export default {
    editConfig: {
        name: '金额输入',
        icon: 'Document',
        code: 'FormInputMoney'
    },
    componentConfig: {
        uuid: '16 uuid',
        type: 'simple',
        formValueCode: '',
        codeType: 'custom',
        label: '金额输入',
        placeholder: '请输入',
        disabled: false,
        required: false,
        isColumn: true,
        precision: 2,
        width: 2,
        componentType: 'FormInputMoney',
        valid: true
    },
    conponentSetConfig: [
        'LABEL',
        'CODE_SELECT',
        'PRIMARY_COLUMN',
        'WIDTH',
        'DEFAULTVALUE_NUMBER',
        'PLACEHOLDER',
        'DISABLED',
        'REQUIRED',
        'LIST_COLUMN'
    ]
}