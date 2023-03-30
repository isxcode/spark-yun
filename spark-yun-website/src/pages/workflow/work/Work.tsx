import React, {useEffect, useState} from 'react'
import './Work.scss'
import {Avatar, Button, Divider, Form, Input, List, Skeleton, Space, Table, Tabs, TabsProps, Tag, theme} from 'antd'
import { ColumnsType } from 'antd/es/table'
import { useNavigate } from 'react-router-dom'
import TextArea from "antd/es/input/TextArea";
function Work () {

  const onChange = (key: string) => {
    console.log(key);
  };

  const {
    token: { colorBgContainer, colorPrimary }
  } = theme.useToken()

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: `提交日志`,
      children: `Content of Tab Pane 1`,
    },
    {
      key: '2',
      label: `运行日志`,
      children: `Content of Tab Pane 2`,
    },
    {
      key: '3',
      label: `返回数据`,
      children: `Content of Tab Pane 3`,
    }
  ];

  return (
    <>
      <div>
        <div className={'work-bar'}>
          <Button>返回作业流</Button>
          <Button>运行</Button>
          <Button>中止</Button>
          <Button>刷新撤回</Button>
          <Button>保存</Button>
          <Button>运行配置</Button>
        </div>
        <TextArea rows={18} />
        <Tabs defaultActiveKey="1" items={items} onChange={onChange} />
      </div>
    </>
  );
}

export default Work
