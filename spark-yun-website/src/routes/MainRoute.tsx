import React from 'react'
import { BrowserRouter, Navigate, Route, Routes, useLocation } from 'react-router-dom'
import CalculateEnginePage from '../pages/calculate/engine/CalculateEnginePage'
import EngineNodePage from '../pages/calculate/node/EngineNodePage'
import DatasourcePage from '../pages/datasource/DatasourcePage'
import WorkflowPage from '../pages/workflow/WorkflowPage'
import WorkPage from '../pages/work/WorkPage'
import WorksPage from '../pages/works/WorksPage'
import Auth from '../pages/auth/Auth'
import Layouts from '../layouts/Layouts'
import Login from '../pages/login/Login'
import UserPage from "../pages/user/UserPage";
import TenantUserPage from "../pages/tenant/user/TenantUserPage";
import TenantPage from "../pages/tenant/TenantPage";
import LicensePage from "../pages/license/LicensePage";

export default function MainRoute() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route
            path="/*"
            element={
              <RequireAuth>
                <Layouts/>
              </RequireAuth>
            }
          />
          <Route path="/login" element={<Login/>}/>
          <Route
            path={'/'}
            element={
              <RequireAuth>
                <Layouts/>
              </RequireAuth>
            }>
            <Route index element={<Navigate to={'/monitor'}/>}/>
            <Route
              path={'/monitor'}
              element={
                <RequireAuth>
                  <Auth/>
                </RequireAuth>
              }
            />
            <Route
              path={'/auth'}
              element={
                <RequireAuth>
                  <Auth/>
                </RequireAuth>
              }
            />
            <Route
              path={'/user'}
              element={
                <RequireAuth>
                  <UserPage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/tenant'}
              element={
                <RequireAuth>
                  <TenantPage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/license'}
              element={
                <RequireAuth>
                  <LicensePage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/tenant_user'}
              element={
                <RequireAuth>
                  <TenantUserPage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/engine'}
              element={
                <RequireAuth>
                  <CalculateEnginePage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/nodes/:calculateEngineId'}
              element={
                <RequireAuth>
                  <EngineNodePage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/datasource'}
              element={
                <RequireAuth>
                  <DatasourcePage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/workflow'}
              element={
                <RequireAuth>
                  <WorkflowPage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/work/:workId'}
              element={
                <RequireAuth>
                  <WorkPage/>
                </RequireAuth>
              }
            />
            <Route
              path={'/works/:workflowId'}
              element={
                <RequireAuth>
                  <WorksPage/>
                </RequireAuth>
              }
            />
          </Route>
        </Routes>
      </BrowserRouter>
    </>
  );
}

function RequireAuth({ children }: { children: JSX.Element }) {
  const location = useLocation()
  if (localStorage.getItem('Authorization') == null) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }
  return children
}
