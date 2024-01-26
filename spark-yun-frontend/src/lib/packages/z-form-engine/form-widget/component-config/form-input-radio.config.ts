export default {
    editConfig: {
        name: '单选框',
        icon: 'Document',
        code: 'FormInputRadio'
    },
    componentConfig: {
        uuid: '16 uuid',
        type: 'simple',
        formValueCode: '',
        codeType: 'custom',
        label: '单选框',
        placeholder: '请选择',
        disabled: false,
        width: 4,
        isColumn: true,
        componentType: 'FormInputRadio',
        options: [
            {
                label: '选项1',
                value: '1'
            }
        ],
        valid: true
    },
    conponentSetConfig: [
        'LABEL',
        'CODE_SELECT',
        'OPTIONS',
        'DEFAULTVALUE_MULTIPLE',
        'DISABLED',
        'REQUIRED',
        'LIST_COLUMN'
    ]
}