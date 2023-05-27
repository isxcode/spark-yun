##### 开发手册

#### 项目结构

.github/ISSUE_TEMPLATE  :  提交issue的模版
docs : 产品的所有相关文档
spark-yun-agent : 至轻云代理服务模块
spark-yun-api : 后端所有的对象中心
spark-yun-backend : 至轻云后端服务入口，只管理配置文档
spark-yun-client : 为了继承spark-star中的功能，客户端功能，支持更高效的访问代理中的服务
spark-yun-common : api模块，支持所有项目引用，common支持至轻云内部引用
spark-yun-community : 后端社区版本服务
spark-yun-connector : 多数据源连接器，使用ISP，实现动态读取指定数据库驱动
spark-yun-dist : 至轻云打包模块
spark-yun-plugins : 至轻云所有的spark插件
spark-yun-vip : 至轻云企业版模块
spark-yun-website : 至轻云前端模块
spark-yun-yarn : 使用yarn发布spark作业模块
.editorconfig : 编辑器编码格式配置
.gitignore : git忽视提交文件
build.gradle : gradle构建脚本
CODE_OF_CONDUCT.md : github文明说明
CONTRIBUTING.md : 开发者说明文档
Dockerfile : docker构建脚本
gradle.properties : gradle配置文件
LICENSE : 许可证
README.md : 说明文档
SECURITY.md : 安全升级说明
settings.gradle : gradle设置文件
VERSION : 全局版本号设置



