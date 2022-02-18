import React, { ReactElement, FC } from "react";
import Container from "@mui/material/Container";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton"
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import MenuItem from '@mui/material/MenuItem';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';

/**
 * Responsive NAV bar component with links to home, swagger graphql and the github
 *  
 * @param title 
 * @returns MaterialUINav component
 */


const NavBar: FC<any> = (): ReactElement => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
  const handleMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const menu = [
    {
      label: "Transactions",
      link: "/transaction"
    },
    {
      label: "Back trace",
      link: "/backtrace"
    },
    {
      label: "Forward trace",
      link: "/forwardtrace"
    },
  ];

  return (
    <Toolbar>
      <Container
        maxWidth="lg"
        sx={{ display: `flex`, justifyContent: `space-between` }}
      >
        <Stack direction="row" spacing={4}>
          <IconButton
            size="large"
            aria-label="account of current user"
            aria-controls="menu-appbar"
            aria-haspopup="true"
            onClick={handleMenu}
            color="inherit"
          >
            <MenuIcon />
          </IconButton>
          <Menu
            id="menu-appbar"
            anchorEl={anchorEl}
            anchorOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            keepMounted
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
            open={Boolean(anchorEl)}
            onClose={handleClose}
          >
            {menu.map(({ label, link }) => (
              <MenuItem component={Link} href={link}>{label}</MenuItem>
            ))}
          </Menu>

        </Stack>
      </Container>
    </Toolbar>
  );

};

export default NavBar;

