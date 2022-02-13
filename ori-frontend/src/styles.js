import { createTheme } from '@mui/material/styles';
import { blue } from '@mui/material/colors';
import { styled } from '@mui/system';
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
        ].join(',')
    }
});

/**
 * Page styles
 */
const RootDiv = styled('div')({
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
});

const PaperDiv = styled('div')({
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(3),
    padding: theme.spacing(2),
    [theme.breakpoints.up(600 + theme.spacing(3) * 2)]: {
        marginTop: theme.spacing(6),
        marginBottom: theme.spacing(6),
        padding: theme.spacing(3)
    }
});

const BoxDiv = styled('div')({
    marginTop: theme.spacing(3),
    marginBottom: theme.spacing(3),
    padding: theme.spacing(2),
    [theme.breakpoints.up(600 + theme.spacing(3) * 2)]: {
        width: 600,
        marginTop: theme.spacing(6),
        marginBottom: theme.spacing(6),
        padding: theme.spacing(3)
    },
    boxShadow: 0
})

export { theme, RootDiv, PaperDiv, BoxDiv};
