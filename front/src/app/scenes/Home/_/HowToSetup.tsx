import {
  Button,
  createStyles,
  Paper,
  Step,
  StepContent,
  StepLabel,
  Stepper, Theme,
  Typography, WithStyles,
  withStyles
} from '@material-ui/core';
import * as React from 'react';
import { UserLocation } from '../../../types/userLocation';
import { UserSetting } from '../../../types/userSetting';

const styles = (theme: Theme) => createStyles({
  root: {
    width: '90%',
  },
  button: {
    marginTop: theme.spacing.unit,
    marginRight: theme.spacing.unit,
  },
  actionsContainer: {
    marginBottom: theme.spacing.unit * 2,
  },
  resetContainer: {
    padding: theme.spacing.unit * 3,
  },
});

type Props = {
  userSetting: UserSetting
  userLocation: UserLocation
  onComplete: () => void
} & WithStyles<typeof styles>

type State = {
  activeStep: number
}

type OneStep = {
  label: string
  content: React.ComponentType
}

const steps: OneStep[] = [
  {
    label: '',
    content: () => (
      <div>
        hoge
      </div>
    ),
  },
];

export default withStyles(styles)(class HowToSetup extends React.Component<Props, State> {
  constructor(props: any, context: any) {
    super(props, context);
    this.state = {
      activeStep: 0,
    };
  }

  public handleNext = () => {
    this.setState(state => ({
      activeStep: state.activeStep + 1,
    }));
  };

  public handleBack = () => {
    this.setState(state => ({
      activeStep: state.activeStep - 1,
    }));
  };

  public handleReset = () => {
    this.setState({
      activeStep: 0,
    });
  };

  public render = () => {
    const {
      classes,
    } = this.props;
    const {
      activeStep,
    } = this.state;
    return (
      <div className={classes.root}>
        <Stepper activeStep={activeStep} orientation="vertical">
          {steps.map((step, index) => {
            return (
              <Step key={step.label}>
                <StepLabel>{step.label}</StepLabel>
                <StepContent>
                  <Typography>{step.content}</Typography>
                  <div className={classes.actionsContainer}>
                    <div>
                      <Button
                        disabled={activeStep === 0}
                        onClick={this.handleBack}
                        className={classes.button}
                      >
                        Back
                      </Button>
                      <Button
                        variant="contained"
                        color="primary"
                        onClick={this.handleNext}
                        className={classes.button}
                      >
                        {activeStep === steps.length - 1 ? 'Finish' : 'Next'}
                      </Button>
                    </div>
                  </div>
                </StepContent>
              </Step>
            );
          })}
        </Stepper>
        {activeStep === steps.length && (
          <Paper square={true} elevation={0} className={classes.resetContainer}>
            <Typography>All steps completed - you&quot;re finished</Typography>
          </Paper>
        )}
      </div>
    );
  }

})
