import pluginVue from 'eslint-plugin-vue'

export default [
  {
    ignores: ['node_modules/**', 'dist/**', 'coverage/**']
  },
  ...pluginVue.configs['flat/essential'],
  {
    rules: {
      'vue/multi-word-component-names': 'off'
    }
  }
]
