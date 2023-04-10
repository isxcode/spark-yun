import React from 'react'
import { BrowserRouter, Navigate, Route, Routes, useLocation } from 'react-router-dom'
import Engine from '../pages/engine/Engine'
import Node from '../pages/engine/node/Node'
import Datasource from '../pages/datasource/Datasource'
import Workflow from '../pages/workflow/Workflow'
import Work from '../pages/workflow/work/Work'
import Worklist from '../pages/workflow/worklist/Worklist'
import Auth from '../pages/auth/Auth'
import Layouts from '../layouts/Layouts'
import Login from '../pages/login/Login'

export default function MainRoute () {
  return (
    <>
      <React.StrictMode>
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
              <Route index element={<Navigate to={'/monitor'} />} />
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
                path={'/engine'}
                element={
                  <RequireAuth>
                    <Engine />
                  </RequireAuth>
                }
              />
              <Route
                path={'/node/:engineId'}
                element={
                  <RequireAuth>
                    <Node />
                  </RequireAuth>
                }
              />
              <Route
                path={'/datasource'}
                element={
                  <RequireAuth>
                    <Datasource />
                  </RequireAuth>
                }
              />
              <Route
                path={'/workflow'}
                element={
                  <RequireAuth>
                    <Workflow />
                  </RequireAuth>
                }
              />
              <Route
                path={'/work/:workId'}
                element={
                  <RequireAuth>
                    <Work />
                  </RequireAuth>
                }
              />
              <Route
                path={'/worklist/:workflowId'}
                element={
                  <RequireAuth>
                    <Worklist />
                  </RequireAuth>
                }
              />
            </Route>
          </Routes>
        </BrowserRouter>
      </React.StrictMode>
    </>
  )
}

function RequireAuth ({ children }: { children: JSX.Element }) {
  const location = useLocation()
  if (localStorage.getItem('Authorization') == null) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }
  return children
}
