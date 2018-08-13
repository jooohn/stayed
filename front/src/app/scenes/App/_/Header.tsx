import { AppBar, Avatar, createStyles, Toolbar, Typography, withStyles } from '@material-ui/core';
import { AccountCircle } from '@material-ui/icons';
import * as React from 'react';
import { UserAccount } from '../../../flux/auth';

type Props = { currentUser: UserAccount | null }
export default withStyles(theme => createStyles({
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
  },
  flex: {
    flexGrow: 1,
  },
}))<Props>(({ classes, currentUser }) => (
  <AppBar position="fixed" className={classes.appBar}>
    <Toolbar>
      <Typography variant="title" color="inherit" className={classes.flex}>
        Stayed
      </Typography>
      {currentUser !== null && (
        <div>
          {currentUser.photoURL
            ? (
              <Avatar alt="Profile Icon" src={currentUser.photoURL} />
            )
            : <AccountCircle/>
          }
        </div>
      )}
    </Toolbar>
  </AppBar>
));
