export interface ComponentInstance {
    componentType: string
    formValueCode: string
    label: string
    placeholder: string
    uuid: string
    width: number
    disabled: boolean
    required: boolean
    icon?: string
    codeType: string
    valid: boolean
    defaultValue: string | string[] | number | boolean
    type: string
}
