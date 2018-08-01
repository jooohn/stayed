import * as firebase from 'firebase/app'
import { Action } from 'redux';
import { handleActions } from 'redux-actions';
import { ThunkDispatch } from 'redux-thunk';
import { RootState } from './index';

const googleAuthProvider = new firebase.auth.GoogleAuthProvider();
googleAuthProvider.addScope('profile');

export type State = {
  authenticated: boolean,
}
const initialState: State = {
  authenticated: false,
};

// Actions

export const withGoogle = () =>
  async (dispatch: ThunkDispatch<RootState, void, Action>) => {
    try {
      alert(firebase.auth().currentUser)
      // const { user } = await firebase.auth().signInWithPopup(googleAuthProvider);
      // alert(JSON.stringify(user))
    } catch (e) {
      alert(e);
    }
    return;
  };

type ActionPayloads = null

export default handleActions<State, ActionPayloads>({
}, initialState);
