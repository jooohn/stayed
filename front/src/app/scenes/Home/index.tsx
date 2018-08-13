import {
  createStyles, Divider,
  Drawer,
  Grid, Icon,
  List,
  ListItem, ListItemIcon, ListItemText,
  Theme,
  Typography,
  WithStyles,
  withStyles
} from '@material-ui/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RootState } from '../../flux';
import { userLocationActions, userLocationSelectors } from '../../flux/userLocation';
import { userSettingActions } from '../../flux/userSetting';
import { UserLocation } from '../../types/userLocation';
import RegistrationDialog from './_/RegistrationDialog';

const styles = (theme: Theme) => createStyles({
  container: {
    flexGrow: 1,
    zIndex: 1,
    overflow: 'hidden',
    position: 'relative',
    display: 'flex',
  },
  toolbar: theme.mixins.toolbar,
  drawerPaper: {
    position: 'relative',
    width: 240,
  },
  main: {
    backgroundColor: theme.palette.background.default,
    padding: theme.spacing.unit * 3,
    flexGrow: 1,
  }
});

type Props = {
  userLocationLoading: boolean
  userLocations: UserLocation[]
  openUserLocationRegistrationDialog: () => void
  fetchUserSetting: () => void
  fetchUserLocations: () => void
} & WithStyles<typeof styles>
type State = {
  now: Date,
  clearInterval?: () => void,
}
class Home extends React.Component<Props, State> {

  constructor(props: any, context: any) {
    super(props, context);

    this.state = {
      now: new Date(),
    };
  }

  public componentDidMount() {
    this.props.fetchUserSetting();
    this.props.fetchUserLocations();

    const interval = setInterval(() => {
      this.setState({
        now: new Date(),
      });
    }, 5000);
    this.setState({
      clearInterval: () => clearInterval(interval),
    });
  }

  public componentWillUnmount() {
    if (this.state.clearInterval !== undefined) {
      this.state.clearInterval();
    }
  }

  public handleClickRegister = () => {
    this.props.openUserLocationRegistrationDialog();
  };

  public render() {
    const {
      classes,
      userLocationLoading,
      userLocations,
    } = this.props;
    return (
      <div className={classes.container}>
        <RegistrationDialog/>
        <Drawer
          variant="permanent"
          classes={{
            paper: classes.drawerPaper,
          }}
        >
          <div className={classes.toolbar} />
          <List component="nav">
            <ListItem button={true} onClick={this.handleClickRegister}>
              <ListItemIcon>
                <Icon>add</Icon>
              </ListItemIcon>
              <ListItemText primary="Create New"/>
            </ListItem>
          </List>
          <Divider/>
          <List component="nav">
            <ListItem button={true}>
              <ListItemText primary="All"/>
            </ListItem>
            {userLocations.map(userLocation => (
              <ListItem key={userLocation.id} button={true}>
                <ListItemText primary={userLocation.label} />
              </ListItem>
            ))}
          </List>
        </Drawer>
        <div className={classes.main}>
          <div className={classes.toolbar} />
          <Typography variant="display1">
            Your locations
          </Typography>
          <Grid container={true}>
            <Grid item={true} xs={12}>
              {!userLocationLoading && (
                (userLocations.length === 0)
                  ? <span>空！</span>
                  : <span>あり</span>
              )}
            </Grid>
          </Grid>
        </div>
      </div>
    );
  }

}
export default connect((state: RootState) => ({
  userLocations: userLocationSelectors.getUserLocations(state),
  userLocationLoading: userLocationSelectors.isLoading(state),
}), (dispatch) => ({
  openUserLocationRegistrationDialog: () => dispatch(userLocationActions.openUserLocationRegistrationDialog()),
  fetchUserSetting: () => dispatch(userSettingActions.fetchUserSetting()),
  fetchUserLocations: () => dispatch(userLocationActions.fetchUserLocations()),
}))(withStyles(styles)(Home))
