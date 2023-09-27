import { createPinia } from 'pinia'
import PiniaPluginPersistedState from 'pinia-plugin-persistedstate'

const pinia = createPinia()

pinia.use(PiniaPluginPersistedState)

export default pinia