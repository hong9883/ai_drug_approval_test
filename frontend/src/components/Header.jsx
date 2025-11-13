import React from 'react';
import styled from 'styled-components';
import { Bell } from 'lucide-react';

const HeaderContainer = styled.header`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
`;

const Title = styled.h1`
  font-size: 1.5rem;
  font-weight: 600;
  margin: 0;
`;

const UserInfo = styled.div`
  display: flex;
  align-items: center;
  gap: 2rem;
`;

const UserDetails = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.25rem;
`;

const UserName = styled.span`
  font-weight: 500;
  font-size: 1rem;
`;

const UserMeta = styled.span`
  font-size: 0.85rem;
  opacity: 0.9;
`;

const NotificationIcon = styled.button`
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
  position: relative;

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }
`;

const NotificationBadge = styled.span`
  position: absolute;
  top: 5px;
  right: 5px;
  background: #ff4757;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.7rem;
  font-weight: bold;
`;

const Header = ({ user }) => {
  return (
    <HeaderContainer>
      <Title>의약품 허가심사 검토 시스템</Title>
      <UserInfo>
        <UserDetails>
          <UserName>{user.name} ({user.department})</UserName>
          <UserMeta>접속시간: {user.loginTime}</UserMeta>
        </UserDetails>
        <NotificationIcon>
          <Bell size={20} color="white" />
          <NotificationBadge>3</NotificationBadge>
        </NotificationIcon>
      </UserInfo>
    </HeaderContainer>
  );
};

export default Header;
