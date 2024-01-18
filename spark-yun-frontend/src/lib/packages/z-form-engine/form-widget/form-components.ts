import FormInputTextConfig from './component-config/form-input-text.config'
import FormInputSelectConfig from './component-config/form-input-select.config'
import FormInputNumberConfig from './component-config/form-input-number.config'
import FormInputDateConfig from './component-config/form-input-date.config'
import FormInputSwitchConfig from './component-config/form-input-switch.config'
import FormInputRadioConfig from './component-config/form-input-radio.config'
import FormInputCheckboxConfig from './component-config/form-input-checkbox.config'
import FormInputMoneyConfig from './component-config/form-input-money.config'

import FormStaticPlaceholderConfig from './component-config/form-static-placeholder.config'
import FormStaticEmptyConfig from './component-config/form-static-empty.config'

const commonComponents = [
    FormInputTextConfig,
    FormInputSelectConfig,
    FormInputNumberConfig,
    FormInputMoneyConfig,
    FormInputDateConfig,
    FormInputSwitchConfig,
    FormInputRadioConfig,
    FormInputCheckboxConfig
]

const staticComponents = [
    FormStaticPlaceholderConfig,
    FormStaticEmptyConfig
]

export default [
    ...commonComponents,
    ...staticComponents
]