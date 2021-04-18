import axios from "axios";
import {all, call, fork, put, takeEvery} from "redux-saga/effects";
import {push} from "connected-react-router";
import {
  CLEAR_ERROR_FAILURE,
  CLEAR_ERROR_REQUEST,
  CLEAR_ERROR_SUCCESS,
  LOGIN_FAILURE,
  LOGIN_REQUEST,
  LOGIN_SUCCESS,
  LOGOUT_FAILURE,
  LOGOUT_REQUEST,
  LOGOUT_SUCCESS,
  MEMBER_LOADING_FAILURE,
  MEMBER_LOADING_REQUEST,
  MEMBER_LOADING_SUCCESS,
  MEMBER_REGISTER_FAILURE,
  MEMBER_REGISTER_REQUEST,
  MEMBER_REGISTER_SUCCESS,
} from "../types";

// Register
const registerMemberAPI = (req) => {
  const config = {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  };
  return axios.post("/api/v1/members", req, config);
};

function* registerMember(action) {
  try {
    const result = yield call(registerMemberAPI, action.payload);
    yield put({
      type: MEMBER_REGISTER_SUCCESS,
      payload: result.data,
    });
    yield put(push("/"));
  } catch (e) {
    yield put({
      type: MEMBER_REGISTER_FAILURE,
      payload: e.response,
    });
  }
}

function* watchRegisterMember() {
  yield takeEvery(MEMBER_REGISTER_REQUEST, registerMember);
}

// clear Error
function* clearError() {
  try {
    yield put({
      type: CLEAR_ERROR_SUCCESS,
    });
  } catch (e) {
    yield put({
      type: CLEAR_ERROR_FAILURE,
    });
    console.error(e);
  }
}

function* watchClearError() {
  yield takeEvery(CLEAR_ERROR_REQUEST, clearError);
}

// Member Loading

const memberLoadingAPI = (token) => {
  const config = {
    headers: {
      "Content-Type": "application/json",
    },
  };
  if (token) {
    config.headers["Authorization"] = "Bearer " + token;
  }
  return axios.get("/api/v1/members/me", config);
};

function* memberLoading(action) {
  try {
    const result = yield call(memberLoadingAPI, action.payload);
    yield put({
      type: MEMBER_LOADING_SUCCESS,
      payload: result.data,
    });
  } catch (e) {
    yield put({
      type: MEMBER_LOADING_FAILURE,
      payload: e.response,
    });
  }
}

function* watchMemberLoading() {
  yield takeEvery(MEMBER_LOADING_REQUEST, memberLoading);
}

// Login
const loginMemberAPI = (loginData) => {
  const config = {
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  };
  return axios.post("api/auth", loginData, config);
};

function* loginMember(action) {
  try {
    const result = yield call(loginMemberAPI, action.payload);
    yield put({
      type: LOGIN_SUCCESS,
      payload: result.data,
    });
    yield put(push("/"));
  } catch (e) {
    yield put({
      type: LOGIN_FAILURE,
      payload: e.response,
    });
  }
}

function* watchLoginMember() {
  yield takeEvery(LOGIN_REQUEST, loginMember);
}

// LOGOUT

function* logout(action) {
  try {
    yield put({
      type: LOGOUT_SUCCESS,
    });
  } catch (e) {
    yield put({
      type: LOGOUT_FAILURE,
    });
    console.log(e);
  }
}

function* watchLogout() {
  yield takeEvery(LOGOUT_REQUEST, logout);
}

export default function* authSaga() {
  yield all([
    fork(watchRegisterMember),
    fork(watchMemberLoading),
    fork(watchClearError),
    fork(watchLoginMember),
    fork(watchLogout),
    fork(watchClearError),
  ]);
}
