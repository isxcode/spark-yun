/*
 * @Author: fanciNate
 * @Date: 2023-06-05 21:39:26
 * @LastEditTime: 2023-06-05 22:33:09
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /spark-yun/spark-yun-website/.eslintrc.js
 */
module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: [ 'plugin:vue/vue3-recommended' ],
  parserOptions: {
    parser: '@typescript-eslint/parser',
    ecmaVersion: 2020,
    sourceType: 'module',
    ecmaFeatures: {
      ts: true
    }
  },
  rules: {
    'vue/no-template-shadow': 'off',
    'no-console': import.meta.env.VITE_NODE_ENV === 'production' ? 'warn' : 'off', // 只在开发环境下使用console
    'no-debugger': import.meta.env.VITE_NODE_ENV === 'production' ? 'warn' : 'off', // 只在开发环境下使用debugger
    'indent': [ 'warn', 2 ], // 缩进为4个空格
    'quotes': [ 'warn', 'single' ], // 单引号
    'semi': [ 'warn', 'never' ], // 添加结尾分号
    'comma-dangle': [ 'warn', 'never' ], // 去掉结尾逗号
    'space-before-function-paren': [ 'warn', 'never' ], // 函数的左括号前面不能有空格
    'func-call-spacing': [ 'warn', 'never' ], // 函数调用前面不能有空格
    'space-before-blocks': [ 'warn', 'always' ], // 块代码的前面要有空格
    'block-spacing': [ 'warn', 'always' ], // 块代码内的空格
    'key-spacing': [ 'warn', {
      'beforeColon': false, 'afterColon': true
    } ], // 对象属性冒号的前后空格
    'comma-spacing': [ 'warn', {
      'before': false, 'after': true
    } ], // 逗号前后的空格
    'semi-spacing': 'warn', // 分号的后面要有空格
    'array-bracket-spacing': [ 'warn', 'always' ], // 数组方括号内的空格
    'space-in-parens': [ 'warn', 'never' ], // 圆括号内的空格
    'object-curly-spacing': [ 'warn', 'always' ], // 大括号内的空格
    'keyword-spacing': [ 'warn', {
      before: true, after: true
    } ], // 关键字的周围要有空格
    'space-infix-ops': 'warn', // 操作符的周围要有空格
    'spaced-comment': [ 'warn', 'always' ], // 注释的周围要有空格
    'lines-around-comment': [ 'warn', {
      'beforeBlockComment': true
    } ], // 注释的前面要有空行
    'eol-last': [ 'warn', 'always' ], // 文件的末尾要有空行
    'object-curly-newline': [ 'warn', {
      'ObjectExpression': 'always',
      'ObjectPattern': 'always',
      'ImportDeclaration': 'never',
      'ExportDeclaration': 'always'
    } ], // 大括号的换行
    'one-var-declaration-per-line': [ 'warn', 'always' ], // 变量赋值后必须换行
    'curly': 'warn', // 控制语句要使用括号
    'no-unused-vars': 'off', // 不要出现未使用的变量
    'no-empty': 'warn', // 不要出现空的代码块
    'vue/comment-directive': 'off',
    'vue/multi-word-component-names': 'off'
  }
}

