const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const CopyPlugin = require('copy-webpack-plugin')
const Dotenv = require('dotenv-webpack')

const config = {
  // 开发模式
  mode: process.env.NODE_ENV,
  devtool: process.env.DEV_TOOL,
  entry: {
    index: './src/index.tsx'
  },
  // 打包导出路径
  output: {
    path: path.resolve(__dirname, './dist/'),
    publicPath: process.env.PUBLIC_PATH,
    filename: 'static/index.js'
  },
  // 本地热部署
  devServer: {
    compress: false,
    historyApiFallback: true,
    port: process.env.PORT
  },
  resolve: {
    fallback: {
      'react/jsx-runtime': 'react/jsx-runtime.js',
      'react/jsx-dev-runtime': 'react/jsx-dev-runtime.js'
    },
    // 解析文件的后缀
    extensions: ['.js', '.jsx', '.tsx', '.ts']
  },
  plugins: [
    // 拷贝插件,拷贝静态资源
    new CopyPlugin({
      patterns: [
        {
          from: './public/assets',
          to: './static'
        }
      ]
    }),
    // html自动生成插件
    new HtmlWebpackPlugin({
      template: './public/index.html'
    }),
    // 清理插件
    new CleanWebpackPlugin(),
    // 环境变量配置
    new Dotenv({
      path: process.env.DOTENV_CONFIG
    })
  ],
  module: {
    rules: [
      // ts解析
      {
        test: /\.ts(x)?$/,
        loader: 'ts-loader',
        exclude: /node_modules/
      },
      // svg解析
      {
        test: /\.svg$/,
        loader: 'svg-sprite-loader',
        options: {}
      },
      // sass解析
      {
        test: /\.css|s[ac]ss$/i,
        use: ['style-loader', 'css-loader', 'sass-loader']
      },
      // png解析
      {
        test: /\.(png|jpe?g|gif)$/i,
        use: 'file-loader',
        exclude: /node_modules/
      }
    ]
  }
}

module.exports = config
