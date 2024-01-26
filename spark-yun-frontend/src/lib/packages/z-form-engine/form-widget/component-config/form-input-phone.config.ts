export default {
    editConfig: {
        name: '手机号输入',
        icon: 'Document',
        code: 'FormInputPhone'
    },
    componentConfig: {
        uuid: '16 uuid',
        type: 'simple',
        formValueCode: '',
        codeType: 'custom',
        label: '手机号输入',
        placeholder: '请输入',
        disabled: false,
        required: false,
        isColumn: true,
        width: 2,
        componentType: 'FormInputPhone',
        valid: true
    },
    conponentSetConfig: [
        'LABEL',
        'CODE_SELECT',
        'WIDTH',
        // 'MAXLENGTH',
        'DEFAULTVALUE',
        'PLACEHOLDER',
        'DISABLED',
        'REQUIRED',
        'LIST_COLUMN'
    ]
}