import React, { useState } from 'react';
import styled from 'styled-components';
import { Upload, FileText, BarChart3, FileStack, ChevronRight, ChevronLeft } from 'lucide-react';

const SidebarContainer = styled.div`
  position: relative;
  width: ${props => props.collapsed ? '60px' : '250px'};
  background: white;
  border-left: 1px solid #e0e0e0;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.05);
`;

const ToggleButton = styled.button`
  position: absolute;
  left: -15px;
  top: 20px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: white;
  border: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 10;

  &:hover {
    background: #f5f5f5;
  }
`;

const MenuList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 2rem 1rem;
`;

const MenuItem = styled.button`
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: ${props => props.active ? '#667eea' : 'transparent'};
  color: ${props => props.active ? 'white' : '#333'};
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  overflow: hidden;

  &:hover {
    background: ${props => props.active ? '#667eea' : '#f5f5f5'};
  }

  svg {
    min-width: 24px;
  }
`;

const MenuText = styled.span`
  font-size: 1rem;
  font-weight: 500;
  opacity: ${props => props.collapsed ? 0 : 1};
  transition: opacity 0.2s;
`;

const Sidebar = ({ collapsed, onToggle, activeMenu, onMenuChange }) => {
  const menuItems = [
    { id: 'upload', icon: Upload, label: '문서등록' },
    { id: 'documents', icon: FileText, label: '문서보기' },
    { id: 'statistics', icon: BarChart3, label: '통계조회' },
    { id: 'submissions', icon: FileStack, label: '제출서류' },
  ];

  return (
    <SidebarContainer collapsed={collapsed}>
      <ToggleButton onClick={onToggle}>
        {collapsed ? <ChevronLeft size={16} /> : <ChevronRight size={16} />}
      </ToggleButton>

      <MenuList>
        {menuItems.map(item => (
          <MenuItem
            key={item.id}
            active={activeMenu === item.id}
            onClick={() => onMenuChange(item.id)}
          >
            <item.icon size={24} />
            <MenuText collapsed={collapsed}>{item.label}</MenuText>
          </MenuItem>
        ))}
      </MenuList>
    </SidebarContainer>
  );
};

export default Sidebar;
