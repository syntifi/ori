import {
  createTheme,
  makeStyles
} from '@material-ui/core/styles';
import blue from '@material-ui/core/colors/blue';

/**
 * Material UI Theme
 */
let theme = createTheme({
  palette: {
    type: 'light',
    primary: {
      main: "#e53e3e",
    },
    secondary: {
      main: blue[500],
    },
  },
  typography: {
    fontFamily: [
      'Roboto',
      '"Open Sans"',
      'Ubuntu',
      '"Apple Color Emoji"',
      '"Segoe UI Emoji"',
      '"Segoe UI Symbol"',
    ].join(',')}
});

/**
 * Page styles
 */ 
const useStyle = makeStyles(() => ({
    root: {
      width: 'auto',
      marginLeft: theme.spacing(2),
      marginRight: theme.spacing(2),
      [theme.breakpoints.up(600 + theme.spacing(2) * 2)]: {
        width: '80%',
        marginLeft: 'auto',
        marginRight: 'auto'
      },
      backgroundColor: theme.palette.background.default,
      color: theme.palette.text.primary
    },  
    paper: {
      marginTop: theme.spacing(3),
      marginBottom: theme.spacing(3),
      padding: theme.spacing(2),
      [theme.breakpoints.up(600 + theme.spacing(3) * 2)]: {
        marginTop: theme.spacing(6),
        marginBottom: theme.spacing(6),
        padding: theme.spacing(3)
      }
    },
    box: {
      marginTop: theme.spacing(3),
      marginBottom: theme.spacing(3),
      padding: theme.spacing(2),
      [theme.breakpoints.up(600 + theme.spacing(3) * 2)]: {
        width: 600,
        marginTop: theme.spacing(6),
        marginBottom: theme.spacing(6),
        padding: theme.spacing(3)
      },
      boxShadow:0
    }
  }));
  
  export { theme, useStyle };
  