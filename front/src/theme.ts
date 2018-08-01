import { createMuiTheme } from '@material-ui/core';

export const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#f5f5f5',
    },
    secondary: {
      light: '#ff5983',
      main: '#f50057',
      // dark: will be calculated from palette.secondary.main,
      contrastText: '#000000',
    },
    // error: will use the default color
  },
});
