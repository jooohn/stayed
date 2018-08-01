import { Dispatch } from 'redux';
import { Action, handleActions } from 'redux-actions';
import { Id, Transaction } from '../types/presence';
import { RootState } from './index';

export type State = {
  transactions: Transaction[]
}
const initialState: State = {
  transactions: [],
};

// Actions

const SET_TRANSACTIONS = 'PRESENCE_SET_TRANSACTIONS';
type SetTransactionPayload = { transactions: Transaction[] }
const setTransactions = (transactions: Transaction[]): Action<SetTransactionPayload> => ({
  type: SET_TRANSACTIONS,
  payload: {transactions},
});

export const fetchTransactions =
  (id: Id, year: number, month: number, timezone: string) =>
    async (dispatch: Dispatch, getState: () => RootState) => {
      setTimeout(() => {
        dispatch(setTransactions([]));
      }, 1000);
    };

type ActionPayloads = SetTransactionPayload

export default handleActions<State, ActionPayloads>({
  [SET_TRANSACTIONS]: (state: State, action: Action<SetTransactionPayload>) => ({
    ...state,
    transactions: action.payload!.transactions,
  }),
}, initialState);
