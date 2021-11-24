import React from 'react';
import NavBar from './components/NavBar';
import { useStyle } from './styles';
import { Box, Button, ButtonGroup, Grid } from "@material-ui/core";
import AccountTrace from './components/AccountTrace';
import { Route, BrowserRouter, Routes, Link } from "react-router-dom";
import AccountList from './components/AccountList';

function App() {
  const classes = useStyle();
  return (
    <>
      <NavBar title="ORI - Onchain Risk Intelligence" />
      <div className={classes.root}>
        <div className={classes.paper}>
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
        </div>

      </div>
    </>
  );
}

export default App;