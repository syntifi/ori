import React from 'react';
import Header from './components/Header'
import { RootDiv, PaperDiv } from './styles';
import AccountTrace from './components/AccountTrace';
import { Route, BrowserRouter, Routes } from "react-router-dom";
import AccountList from './components/AccountList';

function App() {
  return (
    <>
      <Header title="ORI - Onchain Risk Intelligence" />
      <RootDiv>
        <PaperDiv>
          <BrowserRouter>
            <Routes>
              <Route path="/" />
              <Route path="/transaction" element={<AccountList date="To" submit="Transactions" />} />
              <Route path="/forwardtrace" element={<AccountTrace date="From" submit="Forward trace" direction="forward" />} />
              <Route path="/backtrace" element={<AccountTrace date="To" submit="Back trace" direction="back" />} />
            </Routes>
          </BrowserRouter>
        </PaperDiv>
      </RootDiv>
    </>
  );
}

export default App;