import React, { ReactElement, FC } from "react";
import AppBar from "@mui/material/AppBar";
import Box from '@mui/material/Box';
import Container from "@mui/material/Container";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton"
import Home from "@mui/icons-material/Home"
import Link from '@mui/material/Link';
import NavBar from "./NavBar";
import { SiSwagger } from "react-icons/si"
import { SiGithub } from "react-icons/si"

/**
 * Responsive NAV bar component with links to home, swagger graphql and the github
 *  
 * @param title 
 * @returns MaterialUINav component
 */


const Header: FC<any> = ({ title }): ReactElement => {
  const links = [
    {
      label: "Home",
      link: "https://www.syntifi.com",
      icon: <Home fontSize="large"/>
    },
    {
      label: "Swagger UI",
      link: "/q/swagger-ui",
      icon: <SiSwagger size={30}/>
    },
    {
      label: "Github",
      link: "https://www.github.com/syntifi/ori",
      icon: <SiGithub size={30}/>
    },
  ];

  return (
    <>
      <AppBar position="relative">
        <Toolbar>
          <Container
            maxWidth="lg"
            sx={{ display: `flex`, justifyContent: `space-between` }}
          >
            <Box>
              {links.map(({ label, link, icon }, i) => (
                <IconButton edge="start" aria-label="home" size="large">
                  <Link
                    key={`${label}${i}`}
                    href={link}
                    variant="button"
                    sx={{ color: `white` }}
                  >
                    {icon}
                  </Link>
                </IconButton>
              ))}
            </Box>
            <NavBar />
          </Container>
        </Toolbar>
      </AppBar>
    </>
  );

};

export default Header;