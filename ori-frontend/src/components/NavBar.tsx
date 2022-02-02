import React, { ReactElement, FC } from "react";
import { MaterialUINav } from "material-ui-responsive-nav"
import GithubIcon from '@material-ui/icons/GitHub';
import LogoIcon from "../svg/LogoIcon";

/**
 * Responsive NAV bar component with links to home, swagger graphql and the github
 *  
 * @param title 
 * @returns MaterialUINav component
 */
const NavBar : FC<any> = ({title}): ReactElement => {
  const links = {
    internal: [
      {
        label: "Home",
        link: "/",
      },
      {
        label: "Swagger UI",
        link: "/q/swagger-ui",
      }
    ],
    external: [
      {
        label: "Github",
        icon: GithubIcon,
        link: "https://www.github.com/syntifi/ori",
      },
    ],
  }

  return (
    <>
      <MaterialUINav
        global={{
          siteTitle: title,
          mobileBreakpoint: "xs",
        }}
        navbarConfig={{
          elevate: false,
        }}
        mobileMenuConfig={{
          slideTransition: true,
        }}
        logo={<LogoIcon />}
        links={links}
      />
    </>
  )

};

export default NavBar;

