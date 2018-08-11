import * as firebase from 'firebase/app';

const fetchWithAuth = async (input: RequestInfo, init?: RequestInit) => {
  const currentUser = firebase.auth().currentUser;
  if (!currentUser) {
    throw new Error('Not logged in');
  }

  const idToken = await currentUser.getIdToken();
  const additionalHeaders = { 'X-ID-TOKEN': idToken };
  const newHeaders = (init !== undefined && init.headers !== undefined)
    ? { ...init.headers, ...additionalHeaders }
    : additionalHeaders;
  const newInit = (init !== undefined)
    ? { ...init, headers: newHeaders }
    : { headers: newHeaders };
  return await fetch(input, newInit);
};

const asJson = async (responsePromise: Promise<Response>): Promise<any> =>
  responsePromise.then(response => response.json());

const fetchUserSetting = async (): Promise<Response> => asJson(fetchWithAuth('/api/user_setting'));

export default {
  fetchUserSetting,
};
