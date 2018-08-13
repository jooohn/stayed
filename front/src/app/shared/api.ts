import * as firebase from 'firebase/app';
import { UserLocation } from '../types/userLocation';
import { UserSetting } from '../types/userSetting';

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

const get = (uri: string): Request => new Request(uri);
const postJson = (uri: string, body: any): Request => new Request(uri, {
  method: 'POST',
  headers: {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
  },
  body: JSON.stringify(body)
});

const asJson = async (responsePromise: Promise<Response>): Promise<any> =>
  responsePromise.then(response => response.json());

const fetchUserSetting = async (): Promise<UserSetting> =>
  asJson(fetchWithAuth(get('/api/user_setting')));


export type CreateUserLocationPayload = { label: string };
const createUserLocation = async (payload: CreateUserLocationPayload): Promise<UserLocation> =>
  asJson(fetchWithAuth(postJson('/api/locations', {
    type: 'CREATE',
    attributes: payload,
  })));

const fetchUserLocations = async (): Promise<UserLocation[]> => asJson(fetchWithAuth(get('/api/locations')));

export default {
  createUserLocation,
  fetchUserLocations,
  fetchUserSetting,
};
