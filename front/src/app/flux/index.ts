import { Action, applyMiddleware, combineReducers, createStore } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import { combineEpics, createEpicMiddleware, Epic } from 'redux-observable';

import { authEpics, AuthState, default as auth } from './auth';
import { default as userLocation, userLocationEpics, UserLocationState } from './userLocation';
import { default as userSetting, userSettingEpics, UserSettingState } from './userSetting';

export type RootState = {
  auth: AuthState
  userSetting: UserSettingState,
  userLocation: UserLocationState,
}

const rootEpic: Epic<Action, Action, RootState> =
  combineEpics(
    authEpics,
    userSettingEpics,
    userLocationEpics,
  );

export const configureStore = () => {
  const epicMiddleware = createEpicMiddleware<Action, Action, RootState>();
  const store = createStore(
    combineReducers({
      auth,
      userSetting,
      userLocation,
    }),
    composeWithDevTools(applyMiddleware(epicMiddleware))
  );
  epicMiddleware.run(rootEpic);

  return store;
};

