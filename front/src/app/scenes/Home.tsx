import { Button, createStyles, Typography, withStyles } from '@material-ui/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { Action } from 'redux';
import { ThunkDispatch } from 'redux-thunk';
import bg from '../../bg.jpg';
import { authenticateWithGoogle, RootState } from '../flux';

type Props = {
  dispatchAuthenticateWithGoogle: () => void,
};
const Home = withStyles(theme => createStyles({
  container: {
    flex: '1 1 100%',
    flexDirection: 'column',
    paddingTop: theme.spacing.unit * 8,
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundImage: `url(${bg})`,
    backgroundRepeat: 'no-repeat',
    backgroundPosition: 'center',
    backgroundSize: 'cover',
  },
  section: {
    marginTop: theme.spacing.unit * 2,
    marginBottom: theme.spacing.unit * 2,
  },
}))<Props>(({ classes, dispatchAuthenticateWithGoogle }) => (
  <div className={classes.container}>
    <section className={classes.section}>
      <Typography variant="display3">Record how long you stayed there.</Typography>
    </section>
    <section className={classes.section}>
      <Button
        size="large"
        variant="contained"
        color="secondary"
        onClick={dispatchAuthenticateWithGoogle}
      >
        START RECORDING
      </Button>
    </section>
  </div>
));
export default connect(null, (dispatch: ThunkDispatch<RootState, void, Action>) => ({
  dispatchAuthenticateWithGoogle: () => dispatch(authenticateWithGoogle()),
}))(Home);
