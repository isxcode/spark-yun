package com.isxcode.star.api.cluster.pojos.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateClusterNodeReq {

	@Schema(title = "引擎节点唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
	@NotEmpty(message = "id不能为空")
	private String id;

	@Schema(title = "计算引擎唯一id", example = "sy_fd34e4a53db640f5943a4352c4d549b9")
	@NotEmpty(message = "clusterId不能为空")
	private String clusterId;

	@Schema(title = "节点服务器hostname", example = "192.168.115.103")
	@NotEmpty(message = "host不能为空")
	private String host;

	@Schema(title = "节点服务器ssh端口号", example = "22")
	private String port;

	@Schema(title = "节点服务器用户名", example = "ispong")
	@NotEmpty(message = "用户名不能为空")
	private String username;

	@Schema(title = "节点服务器密码", example = "ispong123")
	@NotEmpty(message = "密码或者令牌不能为空")
	private String passwd;

	@Schema(title = "备注", example = "本地测试节点")
	private String remark;

	@Schema(title = "节点名称", example = "节点1")
	@NotEmpty(message = "节点名称不能为空")
	private String name;

	@Schema(title = "代理安装的路径", example = "/Users/ispong")
	private String agentHomePath;

	@Schema(title = "代理服务端口号", example = "30177")
	private String agentPort;

	@Schema(title = "hadoop的home目录", example = "/opt/homebrew/Cellar/hadoop/3.3.4/libexec")
	private String hadoopHomePath;

	@Schema(title = "是否安装spark-local组件", example = "true")
	private Boolean installSparkLocal;
}
