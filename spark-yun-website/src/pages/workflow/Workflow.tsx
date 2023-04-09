import React, {useEffect, useState} from 'react'
import {Button, Space, Table} from 'antd'
import {type ColumnsType} from 'antd/es/table'
import {useNavigate} from 'react-router-dom'
import {AddWorkflowModal} from '../../modals/workflow/AddWorkflowModal'
import {delWorkflowApi, queryWorkflowApi} from "../../services/worflow/workflowService";
import {Workflow} from "../../services/worflow/res/Workflow";
import './Workflow.less';

function Workflow () {

  const navigate = useNavigate();

  const [workflows, setWorkflows] = useState<Workflow[]>([]);
  const [pagination, setPagination] = useState({
    currentPage: 0,
    pageSize: 0,
    total: 0,
  });

  useEffect(() => {
    queryWorkflow(0, 10);
  }, []);

  const queryWorkflow = (page: number, pageSize: number) => {
    queryWorkflowApi({page: page, pageSize: pageSize}).then(function (response) {
      setWorkflows(response.content)
      setPagination({
        total: response.totalElements,
        currentPage: response.currentPage,
        pageSize: response.pageSize
      });
    });
  };

  const delWorkflow = (workflowId: string) => {
    delWorkflowApi(workflowId).then(function (response) {
      queryWorkflow(pagination.currentPage, pagination.pageSize);
    });
  };

  const [visible, setVisible] = useState(false);

  const showModal = () => {
    setVisible(true);
  }

  const handleCancel = () => {
    setVisible(false);
  }

  const columns: ColumnsType<Workflow> = [
    {
      title: '工作流名称',
      dataIndex: 'name',
      key: 'name',
      render: (text, record) => (
        <a onClick={() => {
          navigate('/worklist/' + record.id);
        }}>
          {text}
        </a>
      )
    },
    {
      title: '调度状态',
      key: 'status',
      dataIndex: 'status'
    },
    {
      title: '备注',
      key: 'comment',
      dataIndex: 'comment'
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <a>编辑</a>
          <a
            onClick={() => {
              delWorkflow(record.id)
            }}>
            删除
          </a>
        </Space>
      )
    }
  ];

  const handleTableChange = (pagination) => {
    queryWorkflow(pagination.current - 1, pagination.pageSize);
  };

  return (
    <>
      <div className={'workflow-bar'}>
        <Button type={'primary'} onClick={() => {
          showModal()
        }}>
          添加作业流
        </Button>
      </div>

      <Table columns={columns} dataSource={workflows} pagination={pagination} onChange={handleTableChange}/>

      <AddWorkflowModal isModalVisible={visible} handleCancel={handleCancel} queryWorkflow={queryWorkflow}/>
    </>
  );
}

export default Workflow
