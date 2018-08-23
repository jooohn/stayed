import {
  Checkbox,
  createStyles,
  Divider,
  Drawer,
  Grid,
  Icon,
  IconButton,
  List,
  ListItem,
  ListItemIcon,
  ListItemSecondaryAction,
  ListItemText,
  Theme,
  Typography,
  WithStyles,
  withStyles
} from '@material-ui/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { RootState } from '../../flux';
import { userLocationActions, userLocationSelectors } from '../../flux/userLocation';
import { userSettingActions, userSettingSelectors } from '../../flux/userSetting';
import { UserLocation, UserLocationId } from '../../types/userLocation';
import { UserSetting } from '../../types/userSetting';
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
    width: 260,
  },
  main: {
    backgroundColor: theme.palette.background.default,
    padding: theme.spacing.unit * 3,
    flexGrow: 1,
  }
});

type Props = {
  isActive: (userLocationId: UserLocationId) => boolean
  userSetting: UserSetting | null
  userLocationLoading: boolean
  userLocations: UserLocation[]
  toggleUserLocationSelection: (userLocationId: UserLocationId) => void
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

  public handleToggle = (userLocationId: UserLocationId) => () => {
    this.props.toggleUserLocationSelection(userLocationId);
  };

  public render() {
    const {
      classes,
      isActive,
      userLocationLoading,
      userLocations,
      userSetting,
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
            {userLocations.map(userLocation => (
              <ListItem
                key={userLocation.id}
                dense={true}
                role={undefined}
                button={true}
                onClick={this.handleToggle(userLocation.id)}
              >
                <Checkbox
                  checked={isActive(userLocation.id)}
                  tabIndex={-1}
                  disableRipple={true}
                />
                <ListItemText primary={userLocation.label} />
                <ListItemSecondaryAction>
                  <IconButton>
                    <Icon>settings</Icon>
                  </IconButton>
                </ListItemSecondaryAction>
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
              {!userLocationLoading && userSetting !== null && userLocations.map(userLocation => (
                <div>
                <pre>
                  {window.location.origin}/api/locations/{userLocation.id}
                </pre>
                  <pre>
                    {JSON.stringify({
                      apiToken: userSetting.apiToken,
                      type: "{{EnteredOrExited}}",
                      attributes: {
                        occurredAt: "{{OccurredAt}}"
                      }
                    })}
                </pre>
                </div>
              ))}
            </Grid>
          </Grid>
        </div>
      </div>
    );
  }

}
export default connect((state: RootState) => ({
  isActive: userLocationSelectors.isActive(state),
  userSetting: userSettingSelectors.getUserSetting(state),
  userLocations: userLocationSelectors.getUserLocations(state),
  userLocationLoading: userLocationSelectors.isLoading(state),
}), (dispatch) => ({
  toggleUserLocationSelection:
    (userLocationId: UserLocationId) => dispatch(userLocationActions.toggleUserLocationSelection(userLocationId)),
  openUserLocationRegistrationDialog: () => dispatch(userLocationActions.openUserLocationRegistrationDialog()),
  fetchUserSetting: () => dispatch(userSettingActions.fetchUserSetting()),
  fetchUserLocations: () => dispatch(userLocationActions.fetchUserLocations()),
}))(withStyles(styles)(Home))
