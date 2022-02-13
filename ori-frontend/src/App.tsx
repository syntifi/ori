import React from 'react';
import Header from './components/Header'
import { RootDiv, PaperDiv } from './styles';
import { Box, Button, ButtonGroup, Grid } from "@mui/material";
import AccountTrace from './components/AccountTrace';
import { Route, BrowserRouter, Routes, Link } from "react-router-dom";
import AccountList from './components/AccountList';

function App() {
  return (
    <>
      <Header title="ORI - Onchain Risk Intelligence" />
      <RootDiv>
        <PaperDiv>
          <BrowserRouter>
            <Grid
              container
              spacing={0}
              direction="column"
              alignItems="center"
            >
              <Box padding={5}>
                <Grid item >
                  <ButtonGroup variant="text" color="primary" aria-label="text primary button group">
                    <Button component={Link} to={'/transaction'}> Transactions</Button>
                    <Button component={Link} to={'/backtrace'}> Back trace</Button>
                    <Button component={Link} to={'/forwardtrace'}> Forward Trace</Button>
                  </ButtonGroup>
                </Grid>
              </Box>
            </Grid>
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