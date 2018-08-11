import * as React from 'react';
import { connect } from 'react-redux';
import { userSettingActions } from '../flux/userSetting';

type Props = {
  fetchUserSetting: () => void
}
class Home extends React.Component<Props> {

  public componentDidMount() {
    this.props.fetchUserSetting();
  }

  public render() {
    return (
      <div>
        hoge
      </div>
    );
  }

}
export default connect(null, (dispatch) => ({
  fetchUserSetting: () => dispatch(userSettingActions.fetchUserSetting()),
}))(Home)
