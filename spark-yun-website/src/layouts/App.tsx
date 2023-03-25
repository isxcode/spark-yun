import React from 'react'
import {BrowserRouter, Navigate, Route, Routes, useLocation} from 'react-router-dom';
import Login from "../pages/login/Login";
import Monitor from "../pages/monitor/Monitor";
import Layouts from "./Layouts";

export default function App() {
  return <>
    <React.StrictMode>
      <BrowserRouter>
        <Routes>
          <Route path="/*" element={<RequireAuth><Layouts/></RequireAuth>}/>
          <Route path="/login" element={<Login/>}/>
          <Route path={'/'} element={<RequireAuth><Layouts/></RequireAuth>}>
            <Route index element={<Navigate to={'/monitor'}/>}/>
            <Route path={'/monitor'} element={<RequireAuth><Monitor/></RequireAuth>}/>
          </Route>
        </Routes>
      </BrowserRouter>
    </React.StrictMode>
  </>;
};

function RequireAuth({children}: { children: JSX.Element }) {
  const location = useLocation();
  if (localStorage.getItem('Authorization') == null) {
    return <Navigate to="/login" state={{from: location}} replace/>;
  }
  return children;
}
