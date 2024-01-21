export default {
    editConfig: {
        name: '下拉选择',
        icon: 'Document',
        code: 'FormInputSelect'
    },
    componentConfig: {
        uuid: '16 uuid',
        type: 'simple',
        formValueCode: '',
        codeType: 'table',
        label: '下拉选择',
        placeholder: '请选择',
        disabled: false,
        multiple: false,
        isColumn: true,
        width: 2,
        componentType: 'FormInputSelect',
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
        'WIDTH',
        'OPTIONS',
        'MULTIPLE',
        'DEFAULTVALUE_MULTIPLE',
        'PLACEHOLDER',
        'DISABLED',
        'REQUIRED',
        'LIST_COLUMN'
    ]
}