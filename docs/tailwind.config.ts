import defaultTheme from 'tailwindcss/defaultTheme'
import type { Config } from 'tailwindcss'

export default <Partial<Config>>{
  theme: {
    extend: {
      colors: {
        slate: {
          50: '#f8fafc',
          100: 'red',
          200: 'yellow',
          300: 'green',
          400: 'blue',
          500: 'indigo',
          600: '#475569',
          700: '#334155',
          800: '#1e293b',
          900: '#0f172a',
          950: '#020420'
        },
        green: {
          50: '#EFFDF5',
          100: '#D9FBE8',
          200: '#B3F5D1',
          300: '#75EDAE',
          400: '#00DC82',
          500: '#00C16A',
          600: '#00A155',
          700: '#007F45',
          800: '#016538',
          900: '#0A5331',
          950: '#052e16'
        }
      },
      fontFamily: {
        sans: ['DM Sans', 'DM Sans fallback', ...defaultTheme.fontFamily.sans]
      }
    }
  }
}
