import React, { ReactElement, FC } from "react";
import GithubIcon from '@mui/icons-material/GitHub';
import LogoIcon from "../svg/LogoIcon";
import AppBar from "@mui/material/AppBar";
import Container from "@mui/material/Container";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton"
import Home from "@mui/icons-material/Home"
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';

/**
 * Responsive NAV bar component with links to home, swagger graphql and the github
 *  
 * @param title 
 * @returns MaterialUINav component
 */


const NavBar: FC<any> = ({ title }): ReactElement => {
  const links = [
    {
      label: "Home",
      link: "/",
      icon: null,
    },
    {
      label: "Swagger UI",
      link: "/q/swagger-ui",
      icon: null,
    },
    {
      label: "Github",
      link: "https://www.github.com/syntifi/ori",
      icon: GithubIcon,
    },
  ];

  return (
    <Toolbar>
      <Container
        maxWidth="lg"
        sx={{ display: `flex`, justifyContent: `space-between` }}
      >
        <Stack direction="row" spacing={4}>
          {links.map(({ label, link, icon }, i) => (
            <Link
              key={`${label}${i}`}
              href={link}
              variant="button"
              sx={{ color: `white`, opacity: 0.7 }}
            >
              {label}
            </Link>
          ))}
        </Stack>
      </Container>
    </Toolbar>
  );

};

export default NavBar;

