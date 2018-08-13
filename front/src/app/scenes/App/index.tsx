import { createStyles, Theme, WithStyles, withStyles } from '@material-ui/core';
import * as firebase from 'firebase';
import * as React from 'react';
import { connect } from 'react-redux';
import { RootState } from '../../flux';
import { authActions, authSelectors, UserAccount } from '../../flux/auth';
import Home from '../Home';
import Login from '../Login';
import Header from './_/Header';

const styles = (theme: Theme) => createStyles({
  main: {
    display: 'flex',
    flex: '1 1 100%',
  },
});

type Props = {
  currentUser: UserAccount | null
  isCurrentUserLoading: boolean
  setCurrentUser: (user: firebase.User | null) => void
} & WithStyles<typeof styles>

class App extends React.Component<Props> {
  public componentDidMount() {
    firebase.auth().onAuthStateChanged(this.props.setCurrentUser);
  }

  public render() {
    const {
      classes,
      currentUser,
      isCurrentUserLoading,
    } = this.props;
    return !isCurrentUserLoading && (
      <React.Fragment>
        <Header currentUser={currentUser}/>
        <main className={classes.main}>
          {currentUser === null
            ? <Login/>
            : <Home/>
          }
        </main>
      </React.Fragment>
    );
  }
}

export default connect((state: RootState) => ({
  currentUser: authSelectors.getCurrentUser(state.auth),
  isCurrentUserLoading: authSelectors.isCurrentUserLoading(state.auth),
}), (dispatch) => ({
  setCurrentUser: (user: firebase.User | null) => dispatch(authActions.setCurrentUser(user)),
}))(withStyles(styles)<Props>(App));
