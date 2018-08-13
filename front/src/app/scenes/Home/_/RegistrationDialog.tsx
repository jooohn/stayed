import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@material-ui/core';
import * as React from 'react';
import { ReactEventHandler } from 'react';
import { connect } from 'react-redux';
import { RootState } from '../../../flux';
import { userLocationActions, userLocationSelectors } from '../../../flux/userLocation';
import { CreateUserLocationPayload } from '../../../shared/api';

type Props = {
  open: boolean
  onClose: ReactEventHandler<{}>
  onRegister: (payload: CreateUserLocationPayload) => void
}
type State = {
  label: string
}
class RegistrationDialog extends React.Component<Props, State> {

  constructor(props: any, context: any) {
    super(props, context);

    this.state = {
      label: '',
    };
  }

  public handleChange = (name: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    this.setState({
      [name]: e.target.value,
    } as any);
  };

  public handleRegister = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    e.stopPropagation();
    const { label } = this.state;
    this.props.onRegister({
      label,
    })
  };

  public render() {
    const {
      open,
      onClose,
    } = this.props;
    const {
      label,
    } = this.state;
    return (
      <Dialog
        open={open}
        onClose={onClose}
      >
        <form onSubmit={this.handleRegister}>
          <DialogTitle>Register new location</DialogTitle>
          <DialogContent>
            <TextField
              id="label"
              label="Label"
              value={label}
              autoFocus={true}
              onChange={this.handleChange('label')}
            />
          </DialogContent>
          <DialogActions>
            <Button variant="contained" onClick={onClose} color="primary">
              Cancel
            </Button>
            <Button variant="contained" type="submit" color="secondary">
              Register
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    );
  }

}

export default connect((state: RootState) => ({
  open: userLocationSelectors.isRegistrationDialogOpen(state),
}), dispatch => ({
  onClose: () => dispatch(userLocationActions.closeUserLocationRegistrationDialog()),
  onRegister: (payload: CreateUserLocationPayload) => dispatch(userLocationActions.createUserLocation(payload)),
}))(RegistrationDialog);
