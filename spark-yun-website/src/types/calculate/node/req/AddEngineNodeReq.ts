export interface AddEngineNodeReq {
  clusterId: string | undefined

  name: string

  host: string

  comment: string

  post: string

  username: string

  password: string

  agentHomePath: string

  agentPort: string

  hadoopHomePath: string
}
