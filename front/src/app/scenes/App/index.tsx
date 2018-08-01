import { AppBar, Toolbar, Typography, withStyles } from '@material-ui/core';
import * as React from 'react';
import Home from '../Home';

const App = withStyles(theme => ({
  main: {
    display: 'flex',
    flex: '1 1 100%',
  }
}))(({ classes }) => (
  <React.Fragment>
    <AppBar position="fixed">
      <Toolbar>
        <Typography variant="title" color="inherit">
          Stayed
        </Typography>
      </Toolbar>
    </AppBar>
    <main className={classes.main}>
      <Home/>
    </main>
  </React.Fragment>
));
export default App;
