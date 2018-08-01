import { CssBaseline, MuiThemeProvider } from '@material-ui/core';
import * as firebase from 'firebase/app'
import 'firebase/auth'
import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import './App.css';
import { configureStore } from './app/flux';
import App from './app/scenes/App';
import './index.css';
import registerServiceWorker from './registerServiceWorker';
import { theme } from './theme';

firebase.initializeApp({
  apiKey: process.env.REACT_APP_FIREBASE_API_KEY,
  authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.REACT_APP_FIREBASE_PROJECT_ID,
});

const store = configureStore();

const root = document.getElementById('root');
ReactDOM.render(
  <Provider store={store}>
    <MuiThemeProvider theme={theme}>
    <React.Fragment>
      <CssBaseline />
      <App />
    </React.Fragment>
    </MuiThemeProvider>
  </Provider>,
  root
);
registerServiceWorker();
