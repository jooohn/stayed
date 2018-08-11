import * as firebase from 'firebase/app'
import { Action } from 'redux';
import { combineEpics, Epic } from 'redux-observable';
import { of } from 'rxjs';
import { fromPromise } from 'rxjs/internal-compatibility';
import { catchError, filter, map, switchMap } from 'rxjs/operators';
import { ActionType, createAsyncAction, getType, isActionOf } from 'typesafe-actions';
import { RootState } from './index';

const googleAuthProvider = new firebase.auth.GoogleAuthProvider();
googleAuthProvider.addScope('profile');

export type AuthState = {
  currentUser: UserAccount | null,
  loaded: boolean,
}

export type UserAccount = {
  photoURL: string | null,
  idToken: string,
}

const initialState: AuthState = {
  currentUser: null,
  loaded: false,
};

// Actions

type GetCurrentUserSuccess = UserAccount | null;

const getCurrentUserAccount = async (user: firebase.User): Promise<UserAccount> => {
  const idToken = await user.getIdToken();
  return { idToken, photoURL: user.photoURL };
};

const setCurrentUser = createAsyncAction(
  'auth/SET_CURRENT_USER_REQUEST',
  'auth/SET_CURRENT_USER_SUCCESS',
  'auth/SET_CURRENT_USER_FAILURE'
)<firebase.User | null, GetCurrentUserSuccess, Error>();
const setCurrentUserEpic: Epic<Action, Action, RootState> =
  (action$, state$) =>
    action$
      .pipe(
        filter(isActionOf(setCurrentUser.request)),
        map(({ payload }) => payload),
        switchMap((user: firebase.User | null) => {
          return (user !== null)
            ? fromPromise(getCurrentUserAccount(user)).pipe(
              map(setCurrentUser.success),
              catchError(e => of(setCurrentUser.failure(e)))
            )
            : of(setCurrentUser.success(null));
        })
      );


const signIn = async (): Promise<firebase.User> => {
  const result = await firebase.auth().signInWithPopup(googleAuthProvider);
  if (result.user !== null) {
    return result.user
  } else {
    throw new Error("Failed to login with Google account.")
  }
};

type AuthWithGoogleSuccess = UserAccount
const authWithGoogle = createAsyncAction(
  'auth/AUTH_WITH_GOOGLE_REQUEST',
  'auth/AUTH_WITH_GOOGLE_SUCCESS',
  'auth/AUTH_WITH_GOOGLE_FAILURE'
)<void, AuthWithGoogleSuccess, Error>();
const authWithGoogleEpic: Epic<Action, Action, RootState> =
  (action$) =>
    action$
      .pipe(
        filter(isActionOf(authWithGoogle.request)),
        switchMap(() => fromPromise(signIn().then(getCurrentUserAccount)).pipe(
          map(authWithGoogle.success),
          catchError(e => of(authWithGoogle.failure(e)))
        ))
      );

export const authEpics = combineEpics(setCurrentUserEpic, authWithGoogleEpic);

export const authSelectors = {
  isCurrentUserLoading: (state: AuthState) => !state.loaded,
  getCurrentUser: (state: AuthState) => state.currentUser,
};

// TODO: More sophisticated solution
export const authActions = {
  setCurrentUser: setCurrentUser.request,
  authWithGoogle: authWithGoogle.request,
};

const reducerActions = {
  setCurrentUser,
  authWithGoogle,
};
export default (state: AuthState = initialState, action: ActionType<typeof reducerActions>): AuthState => {
  switch (action.type) {

    case getType(setCurrentUser.request):
      return { ...state, loaded: false };

    case getType(setCurrentUser.success):
      if (action.payload !== null) {
        return { ...state, currentUser: action.payload, loaded: true };
      } else {
        return { ...state, currentUser: null, loaded: true }
      }

    case getType(setCurrentUser.failure):
      // TODO
      alert(action.payload);
      return { ...state, currentUser: null, loaded: true };

    case getType(authWithGoogle.success):
      return { ...state, currentUser: action.payload };

    case getType(authWithGoogle.failure):
      alert(action.payload);
      return state;

    default:
      return state;

  }
};
