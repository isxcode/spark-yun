import React, {useEffect, useState} from 'react'
import {Button, Col, Input, Row, Space, Table, Tag} from 'antd'
import {type ColumnsType} from 'antd/es/table'
import './LicensePage.less'
import {type BasePagination, defaultPagination} from '../../types/base/BasePagination'
import {LicenseRow} from '../../types/license/info/LicenseRow'
import {
  deleteLicenseApi,
  disableLicenseApi,
  enableLicenseApi,
  queryLicenseApi
} from '../../services/license/LicenseService'
import {QueryLicenseReq} from '../../types/license/req/QueryLicenseReq'
import {LicenseModal} from '../../modals/license/LicenseModal'

function LicensePage() {
  const [licenses, setLicense] = useState<LicenseRow[]>([])
  const [pagination, setPagination] = useState<BasePagination>(defaultPagination)
  const [isModalVisible, setIsModalVisible] = useState(false)

  useEffect(() => {
    fetchLicense()
  }, [pagination.currentPage])

  const queryLicenseReq: QueryLicenseReq = {
    page: pagination.currentPage,
    pageSize: pagination.pageSize,
    searchKeyWord: pagination.searchKeyWord
  }

  const fetchLicense = () => {
    queryLicenseApi(queryLicenseReq).then(function (response) {
      setLicense(response.content)
      setPagination((prevPagination) => ({
        ...prevPagination,
        totalItems: response.totalElements
      }))
    })
  }

  const enableLicense = (licenseId: string | undefined) => {
    enableLicenseApi(licenseId).then(function () {
      fetchLicense()
    })
  }

  const disableLicense = (licenseId: string | undefined) => {
    disableLicenseApi(licenseId).then(function () {
      fetchLicense()
    })
  }

  const handleOk = () => {
    fetchLicense()
    setIsModalVisible(false)
  }

  const deleteLicense = (licenseId: string | undefined) => {
    deleteLicenseApi(licenseId).then(function () {
      fetchLicense()
    })
  }

  const handleSearch = () => {
    setPagination((prevPagination) => ({
      ...prevPagination,
      currentPage: 1
    }))
    fetchLicense()
  }

  const columns: ColumnsType<LicenseRow> = [
    {
      title: '许可证编号',
      dataIndex: 'code',
      key: 'code',
      width: 120
    },
    {
      title: '创建时间',
      dataIndex: 'startDateTime',
      key: 'startDateTime',
      width: 120
    },
    {
      title: '到期时间',
      dataIndex: 'endDateTime',
      key: 'endDateTime',
      width: 120
    },
    {
      title: '成员',
      dataIndex: 'maxMemberNum',
      key: 'maxMemberNum',
      width: 80
    },
    {
      title: '租户',
      dataIndex: 'maxTenantNum',
      key: 'maxTenantNum',
      width: 80
    },
    {
      title: '作业流',
      dataIndex: 'maxWorkflowNum',
      key: 'maxWorkflowNum',
      width: 90
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (_, record) => (
        <Space size="middle">
          {record.status === 'ENABLE' && <Tag color="green">启用</Tag>}
          {record.status === 'DISABLE' && <Tag color="red">禁用</Tag>}
        </Space>
      )
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 200
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">

          {
            record.status === "ENABLE" ? <a
              className={'sy-table-a'}
              onClick={() => {
                disableLicense(record.id)
              }}>
              禁用
            </a> : <a
              className={'sy-table-a'}
              onClick={() => {
                enableLicense(record.id)
              }}>
              启用
            </a>
          }

          <a
            className={'sy-table-a'}
            onClick={() => {
              deleteLicense(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ]

  return (
    <div style={{padding: 24}}>
      <Row className={'datasource-bar'}>
        <Col span={8}>
          <Button
            type={'primary'}
            onClick={() => {
              setIsModalVisible(true)
            }}>
            上传证书
          </Button>
        </Col>
        <Col span={7} offset={9} style={{textAlign: 'right', display: 'flex'}}>
          <Input
            style={{marginRight: '10px'}}
            onPressEnter={handleSearch}
            defaultValue={queryLicenseReq.searchKeyWord}
            onChange={(e) => {
              setPagination({...pagination, searchKeyWord: e.target.value})
            }}
            placeholder={'备注'}
          />
          <Button type={'primary'} onClick={handleSearch}>
            搜索
          </Button>
        </Col>
      </Row>

      <Table columns={columns} dataSource={licenses}/>

      <LicenseModal
        isModalVisible={isModalVisible}
        handleCancel={() => {
          setIsModalVisible(false)
        }}
        handleOk={handleOk}
      />
    </div>
  )
}

export default LicensePage
