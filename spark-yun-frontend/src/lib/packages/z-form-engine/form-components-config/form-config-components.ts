import FormLabel from './form-config-list/form-label.vue'
import FormWidth from './form-config-list/form-width.vue'
import FormPlaceholder from './form-config-list/form-placeholder.vue'
import FormDisabled from './form-config-list/form-disabled.vue'
import FormRequired from './form-config-list/form-required.vue'
import FormMaxlength from './form-config-list/form-maxlength.vue'
import FormDefaultValue from './form-config-list/form-default-value.vue'
import FormListColumn from './form-config-list/form-list-column.vue'
import FormDefaultNumber from './form-config-list/form-default-number.vue'
import FormCodeSelect from './form-config-list/form-code-select.vue'
import FormDateType from './form-config-list/form-date-type.vue'
import FormDefaultDate from './form-config-list/form-default-date.vue'
import FormOptions from './form-config-list/form-options.vue'
import FormSelectMultiple from './form-config-list/form-select-multiple.vue'
import FormDefaultMultiple from './form-config-list/form-default-multiple.vue'
import FormColorPicker from './form-config-list/form-color-picker.vue'
import FormSwitchInfo from './form-config-list/form-switch-info.vue'
import FormPrecision from './form-config-list/form-precision.vue'
import FormIsPrimaryColumn from './form-config-list/form-isPrimaryColumn.vue'

export default {
    FormLabel,
    FormWidth,
    FormPlaceholder,
    FormDisabled,
    FormRequired,
    FormMaxlength,
    FormDefaultValue,
    FormCodeSelect,
    FormListColumn,
    FormDefaultNumber,
    FormDateType,
    FormDefaultDate,
    FormOptions,
    FormSelectMultiple,
    FormDefaultMultiple,
    FormColorPicker,
    FormSwitchInfo,
    FormPrecision,
    FormIsPrimaryColumn
}

// vite 写法
// const ConfigInstances = {} as any
// const modules = import.meta.glob('./form-config-list/*.vue', { eager: true })
// for (let each in modules) {
//     const name = (modules[each] as any).default.__name
//     ConfigInstances[name] = (modules[each] as any).default
// }

// export default ConfigInstances