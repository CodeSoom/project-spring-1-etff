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

const initialState = {
  token: localStorage.getItem("token"),
  isAuthenticated: null,
  isLoading: false,
  member: "",
  memberId: "",
  memberName: "",
  memberRole: "",
  errorMsg: "",
  successMsg: "",
  previousMatchMsg: "",
};

const authReducer = (state = initialState, action) => {
  switch (action.type) {
    case LOGIN_REQUEST:
    case LOGOUT_REQUEST:
    case MEMBER_REGISTER_REQUEST:
      return {
        ...state,
        errorMsg: "",
        isLoading: true,
      };
    case MEMBER_REGISTER_SUCCESS:
      return {
        ...state,
        ...action.payload,
        isAuthenticated: null,
        isLoading: false,
        memberId: action.payload,
        memberRole: null,
        errorMsg: "",
      };

    case LOGIN_SUCCESS:
      localStorage.setItem("token", action.payload.accessToken);
      return {
        ...state,
        ...action.payload,
        isAuthenticated: true,
        isLoading: false,
        memberRole: null,
        errorMsg: "",
      };

    case MEMBER_REGISTER_FAILURE:
    case LOGIN_FAILURE:
    case LOGOUT_FAILURE:
      localStorage.removeItem("token");
      return {
        ...state,
        ...action.payload,
        token: null,
        member: null,
        memberId: null,
        isAuthenticated: false,
        isLoading: false,
        memberRole: null,
        errorMsg: action.payload.data.message,
      };
    case LOGOUT_SUCCESS:
      localStorage.removeItem("token");
      return {
        token: null,
        member: null,
        memberId: null,
        isAuthenticated: false,
        isLoading: false,
        memberRole: null,
        errorMsg: "",
      };
    case MEMBER_LOADING_REQUEST:
      return {
        ...state,
        isLoading: true,
      };
    case MEMBER_LOADING_SUCCESS:
      return {
        ...state,
        isAuthenticated: true,
        isLoading: false,
        member: action.payload,
        memberId: action.payload.id,
        memberName: action.payload.name,
        memberRole: "",
      };
    case MEMBER_LOADING_FAILURE:
      return {
        ...state,
        user: null,
        isAuthenticated: false,
        isLoading: false,
        memberRole: "",
      };

    case CLEAR_ERROR_REQUEST:
      return {
        ...state,
      };
    case CLEAR_ERROR_SUCCESS:
      return {
        ...state,
        errorMsg: "",
        previousMatchMsg: "",
      };
    case CLEAR_ERROR_FAILURE:
      return {
        ...state,
        errorMsg: "Clear Error Fail",
        previousMatchMsg: "Clear Error Fail",
      };

    default:
      return state;
  }
};

export default authReducer;
