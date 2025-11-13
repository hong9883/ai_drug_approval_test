import React, { useState, useRef, useEffect } from 'react';
import styled from 'styled-components';
import { Send, MessageSquare } from 'lucide-react';
import { queryAPI } from '../services/api';

const ChatContainer = styled.div`
  width: 350px;
  background: white;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
`;

const ChatHeader = styled.div`
  padding: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-weight: 600;
  font-size: 1.1rem;
`;

const PromptTypeSelector = styled.div`
  padding: 1rem;
  border-bottom: 1px solid #e0e0e0;
`;

const Select = styled.select`
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.9rem;
  cursor: pointer;

  &:focus {
    outline: none;
    border-color: #667eea;
  }
`;

const MessagesContainer = styled.div`
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
`;

const Message = styled.div`
  display: flex;
  flex-direction: column;
  align-items: ${props => props.isUser ? 'flex-end' : 'flex-start'};
`;

const MessageBubble = styled.div`
  max-width: 85%;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  background: ${props => props.isUser ? '#667eea' : '#f0f0f0'};
  color: ${props => props.isUser ? 'white' : '#333'};
  word-wrap: break-word;
  white-space: pre-wrap;
  font-size: 0.9rem;
  line-height: 1.5;
`;

const MessageTime = styled.span`
  font-size: 0.75rem;
  color: #999;
  margin-top: 0.25rem;
`;

const InputArea = styled.div`
  padding: 1rem;
  border-top: 1px solid #e0e0e0;
  display: flex;
  gap: 0.5rem;
`;

const Input = styled.textarea`
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: none;
  font-size: 0.9rem;
  font-family: inherit;

  &:focus {
    outline: none;
    border-color: #667eea;
  }
`;

const SendButton = styled.button`
  padding: 0.75rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;

  &:hover {
    background: #5568d3;
  }

  &:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
`;

const LoadingIndicator = styled.div`
  display: flex;
  gap: 0.25rem;
  padding: 0.75rem;

  span {
    width: 8px;
    height: 8px;
    background: #667eea;
    border-radius: 50%;
    animation: bounce 1.4s infinite ease-in-out both;

    &:nth-child(1) {
      animation-delay: -0.32s;
    }
    &:nth-child(2) {
      animation-delay: -0.16s;
    }
  }

  @keyframes bounce {
    0%, 80%, 100% {
      transform: scale(0);
    }
    40% {
      transform: scale(1);
    }
  }
`;

const Chatbot = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [promptType, setPromptType] = useState('BASIC');
  const messagesEndRef = useRef(null);

  const promptTypes = [
    { value: 'BASIC', label: '기본' },
    { value: 'STRUCTURED', label: '구조화' },
    { value: 'SIMPLE', label: '간단' },
    { value: 'DETAILED', label: '상세' },
    { value: 'POINT', label: '포인트' },
    { value: 'FACT_CHECK', label: '사실 확인' },
    { value: 'STEP_BY_STEP', label: '단계별 사고' },
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSend = async () => {
    if (!input.trim() || loading) return;

    const userMessage = {
      id: Date.now(),
      text: input,
      isUser: true,
      timestamp: new Date().toLocaleTimeString('ko-KR'),
    };

    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setLoading(true);

    try {
      const response = await queryAPI.ask({
        question: input,
        promptType: promptType,
        userName: '홍길동',
        userDepartment: '의약품안전국',
      });

      const botMessage = {
        id: Date.now() + 1,
        text: response.data.answer,
        isUser: false,
        timestamp: new Date().toLocaleTimeString('ko-KR'),
      };

      setMessages(prev => [...prev, botMessage]);
    } catch (error) {
      console.error('Error sending query:', error);
      const errorMessage = {
        id: Date.now() + 1,
        text: '죄송합니다. 응답을 생성하는 중 오류가 발생했습니다.',
        isUser: false,
        timestamp: new Date().toLocaleTimeString('ko-KR'),
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <ChatContainer>
      <ChatHeader>
        <MessageSquare size={24} />
        AI 어시스턴트
      </ChatHeader>

      <PromptTypeSelector>
        <Select value={promptType} onChange={(e) => setPromptType(e.target.value)}>
          {promptTypes.map(type => (
            <option key={type.value} value={type.value}>
              {type.label}
            </option>
          ))}
        </Select>
      </PromptTypeSelector>

      <MessagesContainer>
        {messages.map(message => (
          <Message key={message.id} isUser={message.isUser}>
            <MessageBubble isUser={message.isUser}>
              {message.text}
            </MessageBubble>
            <MessageTime>{message.timestamp}</MessageTime>
          </Message>
        ))}
        {loading && (
          <Message>
            <LoadingIndicator>
              <span />
              <span />
              <span />
            </LoadingIndicator>
          </Message>
        )}
        <div ref={messagesEndRef} />
      </MessagesContainer>

      <InputArea>
        <Input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="질문을 입력하세요..."
          rows={3}
          disabled={loading}
        />
        <SendButton onClick={handleSend} disabled={loading || !input.trim()}>
          <Send size={20} />
        </SendButton>
      </InputArea>
    </ChatContainer>
  );
};

export default Chatbot;
