import React from 'react'
import { BrowserRouter, Navigate, Route, Routes, useLocation } from 'react-router-dom'
import CalculateEnginePage from '../pages/cluster/ClusterPage'
import ClusterNodePage from '../pages/cluster/node/ClusterNodePage'
import DatasourcePage from '../pages/datasource/DatasourcePage'
import WorkflowPage from '../pages/workflow/WorkflowPage'
import WorkPage from '../pages/work/WorkPage'
import WorksPage from '../pages/works/WorksPage'
import Auth from '../pages/auth/Auth'
import Layouts from '../layouts/Layouts'
import Login from '../pages/login/Login'
import UserPage from '../pages/user/UserPage'
import TenantUserPage from '../pages/tenant/user/TenantUserPage'
import TenantPage from '../pages/tenant/TenantPage'
import LicensePage from '../pages/license/LicensePage'
import SettingPage from '../pages/setting/SettingPage'
import ApiPage from '../pages/api/ApiPage'

export default function MainRoute() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route
            path="/*"
            element={
              <RequireAuth>
                <Layouts />
              </RequireAuth>
            }
          />
          <Route path="/login" element={<Login />} />
          <Route
            path={'/'}
            element={
              <RequireAuth>
                <Layouts />
              </RequireAuth>
            }>
            <Route
              index
              element={<Navigate to={localStorage.getItem('Role') == 'ROLE_SYS_ADMIN' ? '/user' : '/engine'} />}
            />
            <Route
              path={'/monitor'}
              element={
                <RequireAuth>
                  <Auth />
                </RequireAuth>
              }
            />
            <Route
              path={'/auth'}
              element={
                <RequireAuth>
                  <Auth />
                </RequireAuth>
              }
            />
            <Route
              path={'/user'}
              element={
                <RequireAuth>
                  <UserPage />
                </RequireAuth>
              }
            />
            <Route
              path={'/tenant'}
              element={
                <RequireAuth>
                  <TenantPage />
                </RequireAuth>
              }
            />
            <Route
              path={'/license'}
              element={
                <RequireAuth>
                  <LicensePage />
                </RequireAuth>
              }
            />
            <Route
              path={'/tenant_user'}
              element={
                <RequireAuth>
                  <TenantUserPage />
                </RequireAuth>
              }
            />
            <Route
              path={'/setting'}
              element={
                <RequireAuth>
                  <SettingPage />
                </RequireAuth>
              }
            />
            <Route
              path={'/api'}
              element={
                <RequireAuth>
                  <ApiPage />
                </RequireAuth>
              }
            />
            <Route
              path={'/engine'}
              element={
                <RequireAuth>
                  <CalculateEnginePage />
                </RequireAuth>
              }
            />
            <Route
              path={'/nodes/:clusterId'}
              element={
                <RequireAuth>
                  <ClusterNodePage />
                </RequireAuth>
              }
            />
            <Route
              path={'/datasource'}
              element={
                <RequireAuth>
                  <DatasourcePage />
                </RequireAuth>
              }
            />
            <Route
              path={'/workflow'}
              element={
                <RequireAuth>
                  <WorkflowPage />
                </RequireAuth>
              }
            />
            <Route
              path={'/work/:workId'}
              element={
                <RequireAuth>
                  <WorkPage />
                </RequireAuth>
              }
            />
            <Route
              path={'/works/:workflowId'}
              element={
                <RequireAuth>
                  <WorksPage />
                </RequireAuth>
              }
            />
          </Route>
        </Routes>
      </BrowserRouter>
    </>
  )
}

function RequireAuth({ children }: { children: JSX.Element }) {
  const location = useLocation()
  if (localStorage.getItem('Authorization') == null) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }
  return children
}
