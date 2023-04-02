import React from 'react'
import { BrowserRouter, Navigate, Route, Routes, useLocation } from 'react-router-dom'
import Login from '../pages/login/Login'
import Monitor from '../pages/monitor/Monitor'
import Layouts from './Layouts'
import Engine from '../pages/engine/Engine'
import Node from '../pages/engine/node/Node'
import Datasource from '../pages/datasource/Datasource'
import Workflow from '../pages/workflow/Workflow'
import Work from '../pages/workflow/work/Work'
import Worklist from '../pages/workflow/worklist/Worklist'

export default function App () {
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
                    <Monitor />
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
