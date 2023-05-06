import React, { useEffect } from 'react'
import { Form, Input, message, Modal, Upload, UploadProps } from 'antd'
import './LicenseModal.less'
import { UserRow } from '../../types/user/info/UserRow'
import { addUserApi, updateUserApi } from '../../services/user/UserService'
import { UpdateUserReq } from '../../types/user/req/UpdateUserReq'
import { AddUserReq } from '../../types/user/req/AddUserReq'
import { InboxOutlined } from '@ant-design/icons'

export const LicenseModal = (props: { isModalVisible: boolean, handleCancel: () => void, handleOk: () => void }) => {
  const { isModalVisible, handleCancel, handleOk } = props

  const { Dragger } = Upload

  const uploadProps: UploadProps = {
    name: 'license',
    multiple: true,
    headers: {
      Authorization: localStorage.getItem('Token') as string
    },
    action: 'http://localhost:8080/lic/uploadLicense',
    onChange(info) {
      const { status } = info.file
      if (status !== 'uploading') {
        console.log(info.file, info.fileList)
      }
      if (status === 'done') {
        message.success(`${info.file.name} 上传成功`)
      } else if (status === 'error') {
        message.error(`${info.file.name} 上传失败`)
      }
    },
    onDrop(e) {
      console.log('Dropped files', e.dataTransfer.files)
    }
  }

  return (
    <>
      <Modal
        title={'上传许可证'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={handleCancel}
        okText={'保存'}
        cancelText={'取消'}>
        <Dragger {...uploadProps}>
          <p className="ant-upload-drag-icon">
            <InboxOutlined />
          </p>
          <p className="ant-upload-text">单击或拖动文件到此区域进行上传</p>
          <p className="ant-upload-hint">上传企业许可证</p>
        </Dragger>
      </Modal>
    </>
  )
}
