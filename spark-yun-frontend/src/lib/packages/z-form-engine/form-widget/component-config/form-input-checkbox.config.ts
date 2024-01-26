export default {
    editConfig: {
        name: '多选框',
        icon: 'Document',
        code: 'FormInputCheckbox'
    },
    componentConfig: {
        uuid: '16 uuid',
        type: 'simple',
        formValueCode: '',
        codeType: 'custom',
        label: '多选框',
        placeholder: '请选择',
        disabled: false,
        width: 4,
        multiple: true,
        componentType: 'FormInputCheckbox',
        isColumn: true,
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
        // 'WIDTH',
        'OPTIONS',
        // 'DEFAULTVALUE',
        'DEFAULTVALUE_MULTIPLE',
        'DISABLED',
        'REQUIRED',
        'LIST_COLUMN'
    ]
}