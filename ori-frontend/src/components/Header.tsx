import React, { ReactElement, FC } from "react";
import LogoIcon from "../svg/LogoIcon";
import AppBar from "@mui/material/AppBar";
import Container from "@mui/material/Container";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton"
import Home from "@mui/icons-material/Home"
import Link from '@mui/material/Link';
import NavBar from "./NavBar";

/**
 * Responsive NAV bar component with links to home, swagger graphql and the github
 *  
 * @param title 
 * @returns MaterialUINav component
 */


const Header: FC<any> = ({ title }): ReactElement => {

  return (
    <>
      <AppBar position="fixed">
        <Toolbar>
          <Container
            maxWidth="lg"
            sx={{ display: `flex`, justifyContent: `space-between` }}
          >
            <IconButton edge="start" aria-label="home">
              <Link>
                <Home
                  sx={{
                    color: (theme) => theme.palette.common.white,
                  }}
                  fontSize="large"
                />
              </Link>
            </IconButton>
            <NavBar />
          </Container>
        </Toolbar>
      </AppBar>
    </>
  );

};

export default Header;