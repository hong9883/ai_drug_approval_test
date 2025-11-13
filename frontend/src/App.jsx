import React, { useState } from 'react';
import { BrowserRouter } from 'react-router-dom';
import styled from 'styled-components';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import MainContent from './components/MainContent';
import Chatbot from './components/Chatbot';

const AppContainer = styled.div`
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
`;

const ContentArea = styled.div`
  display: flex;
  flex: 1;
  overflow: hidden;
`;

const App = () => {
  const [currentUser] = useState({
    name: '홍길동',
    department: '의약품안전국',
    loginTime: new Date().toLocaleString('ko-KR'),
  });

  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const [activeMenu, setActiveMenu] = useState('documents');

  return (
    <BrowserRouter>
      <AppContainer>
        <Header user={currentUser} />
        <ContentArea>
          <Chatbot />
          <MainContent activeMenu={activeMenu} currentUser={currentUser} />
          <Sidebar
            collapsed={sidebarCollapsed}
            onToggle={() => setSidebarCollapsed(!sidebarCollapsed)}
            activeMenu={activeMenu}
            onMenuChange={setActiveMenu}
          />
        </ContentArea>
      </AppContainer>
    </BrowserRouter>
  );
};

export default App;
