import {reactive} from 'vue'
import mitt from 'mitt'
const Mit = mitt();


export const $Bus = reactive({
    $Bus: Mit
})
