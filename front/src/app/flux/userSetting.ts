import { Action } from 'redux';
import { Epic } from 'redux-observable';
import { of } from 'rxjs';
import { fromPromise } from 'rxjs/internal-compatibility';
import { catchError, filter, map, switchMap } from 'rxjs/operators';
import { ActionType, createAsyncAction, getType, isActionOf } from 'typesafe-actions';
import { default as api } from '../shared/api';
import { UserSetting } from '../types/userSetting';
import { RootState } from './index';

export type UserSettingState = UserSetting | null;

const fetchUserSetting = createAsyncAction(
  'userSetting/FETCH_REQUEST',
  'userSetting/FETCH_SUCCESS',
  'userSetting/FETCH_FAILURE'
)<void, UserSetting, Error>();

const fetchUserSettingEpic: Epic<Action, Action, RootState> =
  (action$, state$) =>
    action$
      .pipe(
        filter(isActionOf(fetchUserSetting.request)),
        switchMap(() =>
          fromPromise(api.fetchUserSetting()).pipe(
            map(fetchUserSetting.success),
            catchError(e => of(fetchUserSetting.failure(e)))
          )
        )
      );

export const userSettingActions = {
  fetchUserSetting: fetchUserSetting.request,
};
export const userSettingEpics = fetchUserSettingEpic;

const actions = {
  fetchUserSetting
};

export default (state: UserSettingState = null, action: ActionType<typeof actions>): UserSettingState => {
  switch (action.type) {
    case getType(fetchUserSetting.success):
      return action.payload;
    default:
      return state;
  }
}
