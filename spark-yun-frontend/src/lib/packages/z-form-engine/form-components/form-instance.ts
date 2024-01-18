import FormInputText from './common/form-input-text/index.vue'
import FormInputSelect from './common/form-input-select/index.vue'
import FormInputNumber from './common/form-input-number/index.vue'
import FormInputDate from './common/form-input-date/index.vue'
import FormInputSwitch from './common/form-input-switch/index.vue'
import FormInputRadio from './common/form-input-radio/index.vue'
import FormInputCheckbox from './common/form-input-checkbox/index.vue'
import FormInputMoney from './common/form-input-money/index.vue'

import FormStaticPlaceholder from './static/form-static-placeholder/index.vue'
import FormStaticEmpty from './static/form-static-empty/index.vue'

const Common = {
    FormInputText,
    FormInputSelect,
    FormInputNumber,
    FormInputDate,
    FormInputSwitch,
    FormInputRadio,
    FormInputCheckbox,
    FormInputMoney
}

const Static = {
    FormStaticPlaceholder,
    FormStaticEmpty
}

export default {
    ...Common,
    ...Static
}
