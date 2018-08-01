import { applyMiddleware, combineReducers, createStore } from 'redux';

import thunk from 'redux-thunk';
import { default as auth, withGoogle } from './auth';
import presence, { fetchTransactions, State as PresenceState } from './presence';

export const fetchPresenceTransactions = fetchTransactions;
export const authenticateWithGoogle = withGoogle;

export type RootState = {
  presence: PresenceState
}

export const configureStore = () => createStore(
  combineReducers({
    auth,
    presence,
  }),
  applyMiddleware(thunk)
);
